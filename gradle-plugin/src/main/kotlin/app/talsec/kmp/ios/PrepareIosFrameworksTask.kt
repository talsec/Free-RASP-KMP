package app.talsec.kmp.ios

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.inject.Inject

// Provides verified TalsecBridge and TalsecRuntime XCFrameworks from a local folder or the GitHub release of this version
@DisableCachingByDefault(because = "Copies or downloads large binaries that are cheap to obtain again")
abstract class PrepareIosFrameworksTask @Inject constructor(
    private val execOperations: ExecOperations,
) : DefaultTask() {

    @get:Input
    abstract val frameworksVersion: Property<String>

    @get:Input
    abstract val expectedChecksums: MapProperty<String, String>

    @get:Optional
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val localFrameworksDir: DirectoryProperty

    @get:Input
    abstract val downloadBaseUrl: Property<String>

    // Version of freeRASP_KMP resolved for iOS; empty when it comes from a project dependency
    @get:Input
    @get:Optional
    abstract val libraryVersion: Property<String>

    @get:Internal
    abstract val cacheDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun prepare() {
        val version = frameworksVersion.get()
        val resolvedLibrary = libraryVersion.orNull.orEmpty()
        if (resolvedLibrary.isNotEmpty() && resolvedLibrary != version) {
            throw GradleException(
                "app.talsec.kmp.ios $version requires ${IosFrameworks.LIBRARY_NAME} $version, but $resolvedLibrary is used. " +
                    "Use the same version for the plugin and the library."
            )
        }

        val source = localFrameworksDir.orNull?.asFile ?: downloadedFrameworks(version)
        val problems = IosFrameworks.verify(source, expectedChecksums.get())
        if (problems.isNotEmpty()) {
            throw GradleException(
                "The iOS frameworks in ${source.path} are not the ones released with freeRASP KMP $version:\n" +
                    problems.joinToString("\n") { "  - $it" }
            )
        }

        val output = outputDir.get().asFile
        output.deleteRecursively()
        output.mkdirs()
        for (name in IosFrameworks.NAMES) {
            ditto(File(source, "$name.xcframework"), File(output, "$name.xcframework"))
        }
        logger.lifecycle("Talsec: prepared the iOS frameworks $version from ${source.path}")
    }

    private fun downloadedFrameworks(version: String): File {
        val versionDir = cacheDir.get().asFile.resolve(version)
        val extracted = versionDir.resolve("extracted")
        IosFrameworks.findFrameworksRoot(extracted)?.let { return it }

        val archive = versionDir.resolve("Frameworks.zip")
        if (!archive.isFile) {
            val url = "${downloadBaseUrl.get().trimEnd('/')}/$version/Frameworks.zip"
            logger.lifecycle("Talsec: downloading $url")
            download(url, archive)
        }
        extracted.deleteRecursively()
        extracted.mkdirs()
        execOperations.exec { commandLine("ditto", "-x", "-k", archive.path, extracted.path) }
        return IosFrameworks.findFrameworksRoot(extracted)
            ?: throw GradleException("${archive.path} does not contain ${IosFrameworks.BRIDGE}.xcframework")
    }

    private fun download(url: String, target: File) {
        target.parentFile.mkdirs()
        val partial = File(target.path + ".part")
        val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
        val response = client.send(HttpRequest.newBuilder(URI(url)).GET().build(), HttpResponse.BodyHandlers.ofFile(partial.toPath()))
        if (response.statusCode() != 200) {
            partial.delete()
            throw GradleException("Downloading $url failed with HTTP ${response.statusCode()}")
        }
        Files.move(partial.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    private fun ditto(source: File, target: File) {
        execOperations.exec { commandLine("ditto", source.path, target.path) }
    }
}
