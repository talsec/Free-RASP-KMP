package app.talsec.kmp.ios

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget

// Adds the freeRASP iOS frameworks to the app during the Xcode build, so the integrator configures nothing in Xcode
class TalsecKmpIosPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            configure(project)
        }
    }

    private fun configure(project: Project) {
        val metadata = PluginMetadata.load()
        val providers = project.providers
        val rootDir = project.rootProject.layout.projectDirectory

        val prepare = project.tasks.register(PREPARE_TASK, PrepareIosFrameworksTask::class.java) {
            group = TASK_GROUP
            description = "Provides the verified freeRASP iOS frameworks ${metadata.version}"
            frameworksVersion.set(metadata.version)
            expectedChecksums.set(metadata.checksums)
            localFrameworksDir.set(providers.gradleProperty(FRAMEWORKS_DIR_PROPERTY).map { rootDir.dir(it) })
            downloadBaseUrl.set(providers.gradleProperty(DOWNLOAD_URL_PROPERTY).orElse(DEFAULT_DOWNLOAD_URL))
            cacheDir.set(project.gradle.gradleUserHomeDir.resolve("caches/talsec-kmp-ios"))
            outputDir.set(project.layout.buildDirectory.dir("talsec-kmp-ios/frameworks"))
        }

        val embed = project.tasks.register(EMBED_TASK, EmbedIosFrameworksForXcodeTask::class.java) {
            group = TASK_GROUP
            description = "Embeds and signs the freeRASP iOS frameworks in the app built by Xcode"
            frameworksDir.set(prepare.flatMap { it.outputDir })
            embedRuntime.set(providers.gradleProperty(EMBED_RUNTIME_PROPERTY).map { it.toBoolean() }.orElse(true))
            platformName.set(providers.environmentVariable("PLATFORM_NAME"))
            builtProductsDir.set(providers.environmentVariable("BUILT_PRODUCTS_DIR"))
            targetBuildDir.set(providers.environmentVariable("TARGET_BUILD_DIR"))
            frameworksFolderPath.set(providers.environmentVariable("FRAMEWORKS_FOLDER_PATH"))
            codeSignIdentity.set(providers.environmentVariable("EXPANDED_CODE_SIGN_IDENTITY"))
            codeSigningAllowed.set(providers.environmentVariable("CODE_SIGNING_ALLOWED"))
            projectFilePath.set(providers.environmentVariable("PROJECT_FILE_PATH"))
            userScriptSandboxing.set(providers.environmentVariable("ENABLE_USER_SCRIPT_SANDBOXING"))
        }

        // The Kotlin task is skipped for static frameworks, but its dependencies still run
        project.tasks.matching { it.name == XCODE_TASK }.configureEach { dependsOn(embed) }

        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val iosTargets = kotlin.targets.withType(KotlinNativeTarget::class.java).matching { it.konanTarget.family == Family.IOS }
        iosTargets.configureEach {
            binaries.withType(Framework::class.java).configureEach {
                linkTaskProvider.configure { dependsOn(prepare) }
            }
        }

        project.afterEvaluate {
            val frameworksDir = prepare.get().outputDir.get().asFile
            iosTargets.forEach { target ->
                val slice = if (target.konanTarget == KonanTarget.IOS_ARM64) IosFrameworks.DEVICE_SLICE else IosFrameworks.SIMULATOR_SLICE
                // A dynamic Kotlin framework is linked by Gradle, which needs to find the frameworks itself
                target.binaries.withType(Framework::class.java).filter { !it.isStatic }.forEach { framework ->
                    IosFrameworks.NAMES.forEach { name ->
                        framework.linkerOpts("-F", frameworksDir.resolve("$name.xcframework/$slice").path)
                    }
                }
            }

            val iosTarget = iosTargets.firstOrNull() ?: return@afterEvaluate
            val classpath = iosTarget.compilations.getByName("main").compileDependencyConfigurationName
            prepare.configure {
                libraryVersion.set(
                    project.configurations.named(classpath)
                        .flatMap { it.incoming.resolutionResult.rootComponent }
                        .map { findLibraryVersion(it).orEmpty() }
                )
            }
        }
    }

    companion object {
        const val PREPARE_TASK = "talsecPrepareIosFrameworks"
        const val EMBED_TASK = "talsecEmbedIosFrameworksForXcode"
        const val XCODE_TASK = "embedAndSignAppleFrameworkForXcode"
        const val TASK_GROUP = "talsec"

        const val FRAMEWORKS_DIR_PROPERTY = "talsec.kmp.ios.frameworksDir"
        const val DOWNLOAD_URL_PROPERTY = "talsec.kmp.ios.downloadBaseUrl"
        const val EMBED_RUNTIME_PROPERTY = "talsec.kmp.ios.embedRuntime"
        const val DEFAULT_DOWNLOAD_URL = "https://github.com/talsec/Free-RASP-KMP/releases/download"
    }
}

// Top level, so the resolution provider does not capture the plugin instance
internal fun findLibraryVersion(root: ResolvedComponentResult): String? {
    val visited = mutableSetOf<ResolvedComponentResult>()
    val queue = ArrayDeque(listOf(root))
    while (queue.isNotEmpty()) {
        val component = queue.removeFirst()
        if (!visited.add(component)) continue
        val module = component.moduleVersion
        if (module != null && module.group == IosFrameworks.LIBRARY_GROUP && module.name.startsWith(IosFrameworks.LIBRARY_NAME)) {
            return module.version
        }
        component.dependencies.filterIsInstance<ResolvedDependencyResult>().forEach { queue.add(it.selected) }
    }
    return null
}
