package app.talsec.kmp.ios

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

// Runs inside the Xcode "Compile Kotlin Framework" script phase, before Xcode links the app
@UntrackedTask(because = "Writes into the build products of the running Xcode build")
abstract class EmbedIosFrameworksForXcodeTask @Inject constructor(
    private val execOperations: ExecOperations,
) : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val frameworksDir: DirectoryProperty

    @get:Input
    abstract val embedRuntime: Property<Boolean>

    @get:Input @get:Optional
    abstract val platformName: Property<String>

    @get:Input @get:Optional
    abstract val builtProductsDir: Property<String>

    @get:Input @get:Optional
    abstract val targetBuildDir: Property<String>

    @get:Input @get:Optional
    abstract val frameworksFolderPath: Property<String>

    @get:Input @get:Optional
    abstract val codeSignIdentity: Property<String>

    @get:Input @get:Optional
    abstract val codeSigningAllowed: Property<String>

    @get:Input @get:Optional
    abstract val projectFilePath: Property<String>

    @get:Input @get:Optional
    abstract val userScriptSandboxing: Property<String>

    @TaskAction
    fun embed() {
        val platform = platformName.orNull
        val products = builtProductsDir.orNull
        val target = targetBuildDir.orNull
        val frameworksFolder = frameworksFolderPath.orNull
        if (platform == null || products == null || target == null || frameworksFolder == null) {
            logger.lifecycle("Talsec: not running inside an Xcode build, nothing to embed")
            return
        }
        if (userScriptSandboxing.orNull == "YES") {
            throw GradleException(
                "Talsec: set ENABLE_USER_SCRIPT_SANDBOXING to NO in the Xcode build settings of your app target, " +
                    "so the Kotlin framework build phase can add the iOS frameworks to the app."
            )
        }
        requireNoOtherIntegration()

        val slice = IosFrameworks.sliceForPlatform(platform)
        val linkDir = File(products)
        val appFrameworksDir = File(target, frameworksFolder)
        val names = if (embedRuntime.get()) IosFrameworks.NAMES else listOf(IosFrameworks.BRIDGE)
        for (name in names) {
            val source = IosFrameworks.frameworkDir(frameworksDir.get().asFile, name, slice)
            // Xcode links against the copy in the build products and ships the copy in the app bundle
            copyFramework(source, File(linkDir, "$name.framework"))
            val embedded = File(appFrameworksDir, "$name.framework")
            copyFramework(source, embedded)
            sign(embedded)
            logger.lifecycle("Talsec: embedded $name.framework ($slice)")
        }
        if (!embedRuntime.get()) {
            requireCompatibleRuntime(File(linkDir, "${IosFrameworks.RUNTIME}.framework"))
        }
    }

    private fun requireNoOtherIntegration() {
        val project = projectFilePath.orNull ?: return
        val pbxproj = File(project, "project.pbxproj")
        if (!pbxproj.isFile) return
        val conflicts = IosFrameworks.conflictingReferences(pbxproj.readText(), embedRuntime.get())
        if (conflicts.isNotEmpty()) {
            throw GradleException(
                "Talsec: ${pbxproj.parentFile.name} still integrates ${conflicts.joinToString()}. " +
                    "The app.talsec.kmp.ios plugin adds the iOS frameworks itself, so remove them from the Xcode project " +
                    "(Frameworks, Libraries, and Embedded Content, and Package Dependencies)."
            )
        }
    }

    private fun copyFramework(source: File, target: File) {
        target.deleteRecursively()
        target.parentFile.mkdirs()
        execOperations.exec { commandLine("ditto", source.path, target.path) }
    }

    private fun sign(framework: File) {
        val identity = codeSignIdentity.orNull
        if (codeSigningAllowed.orNull == "NO" || identity.isNullOrBlank()) return
        execOperations.exec {
            commandLine(
                "codesign", "--force", "--sign", identity,
                "--preserve-metadata=identifier,entitlements", "--timestamp=none", framework.path,
            )
        }
    }

    // Another component (for example a future licensing plugin) provides TalsecRuntime, so only its version is checked
    private fun requireCompatibleRuntime(runtime: File) {
        val infoPlist = File(runtime, "Info.plist")
        if (!infoPlist.isFile) {
            logger.warn("Talsec: ${IosFrameworks.RUNTIME}.framework is not in ${runtime.parent} yet, its version could not be checked")
            return
        }
        val output = ByteArrayOutputStream()
        execOperations.exec {
            commandLine("plutil", "-extract", "CFBundleShortVersionString", "raw", infoPlist.path)
            standardOutput = output
        }
        val version = output.toString().trim()
        if (!IosFrameworks.isCompatibleRuntime(version)) {
            throw GradleException("Talsec: ${IosFrameworks.BRIDGE} does not support ${IosFrameworks.RUNTIME} $version provided by another component")
        }
    }
}
