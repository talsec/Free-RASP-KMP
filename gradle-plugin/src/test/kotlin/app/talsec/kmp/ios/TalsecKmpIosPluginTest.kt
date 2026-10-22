package app.talsec.kmp.ios

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TalsecKmpIosPluginTest {

    @Test
    fun `does nothing without Kotlin Multiplatform`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("app.talsec.kmp.ios")

        assertNull(project.tasks.findByName(TalsecKmpIosPlugin.EMBED_TASK))
    }

    @Test
    fun `runs before the Kotlin framework step of the Xcode build`() {
        val project = kmpProject(isStatic = true)

        val xcodeTask = project.tasks.getByName(TalsecKmpIosPlugin.XCODE_TASK)
        val dependencies = xcodeTask.taskDependencies.getDependencies(xcodeTask).map { it.name }
        assertTrue(TalsecKmpIosPlugin.EMBED_TASK in dependencies)
    }

    @Test
    fun `adds framework search paths only to dynamic Kotlin frameworks`() {
        // binaries.framework creates a debug and a release framework
        frameworksOf(kmpProject(isStatic = false)).forEach { dynamic ->
            val searchPaths = dynamic.linkerOpts.filter { it.endsWith(IosFrameworks.SIMULATOR_SLICE) }
            assertEquals(2, searchPaths.size)
            assertTrue(searchPaths.any { "${IosFrameworks.BRIDGE}.xcframework" in it })
            assertTrue(searchPaths.any { "${IosFrameworks.RUNTIME}.xcframework" in it })
        }
        frameworksOf(kmpProject(isStatic = true)).forEach { static ->
            assertTrue(static.linkerOpts.none { it.endsWith(IosFrameworks.SIMULATOR_SLICE) })
        }
    }

    private fun kmpProject(isStatic: Boolean): Project {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("app.talsec.kmp.ios")
        project.extensions.getByType(KotlinMultiplatformExtension::class.java).apply {
            iosSimulatorArm64 {
                binaries.framework {
                    baseName = "Shared"
                    this.isStatic = isStatic
                }
            }
        }
        (project as ProjectInternal).evaluate()
        return project
    }

    private fun frameworksOf(project: Project): List<Framework> =
        project.extensions.getByType(KotlinMultiplatformExtension::class.java)
            .iosSimulatorArm64().binaries.withType(Framework::class.java).toList()
            .also { assertEquals(2, it.size) }
}
