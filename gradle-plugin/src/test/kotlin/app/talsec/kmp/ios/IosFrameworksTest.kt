package app.talsec.kmp.ios

import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IosFrameworksTest {

    @Test
    fun `maps Xcode platforms to XCFramework slices`() {
        assertEquals(IosFrameworks.DEVICE_SLICE, IosFrameworks.sliceForPlatform("iphoneos"))
        assertEquals(IosFrameworks.SIMULATOR_SLICE, IosFrameworks.sliceForPlatform("iphonesimulator"))
        assertFailsWith<IllegalArgumentException> { IosFrameworks.sliceForPlatform("macosx") }
    }

    @Test
    fun `accepts frameworks matching the release checksums`() {
        val root = fakeFrameworks()
        assertEquals(emptyList(), IosFrameworks.verify(root, checksumsOf(root)))
    }

    @Test
    fun `reports modified and missing binaries`() {
        val root = fakeFrameworks()
        val expected = checksumsOf(root)
        File(IosFrameworks.frameworkDir(root, IosFrameworks.BRIDGE, IosFrameworks.DEVICE_SLICE), IosFrameworks.BRIDGE).writeText("tampered")
        File(IosFrameworks.frameworkDir(root, IosFrameworks.RUNTIME, IosFrameworks.SIMULATOR_SLICE), IosFrameworks.RUNTIME).delete()

        val problems = IosFrameworks.verify(root, expected)

        assertEquals(2, problems.size)
        assertTrue(problems.any { "does not match" in it && IosFrameworks.BRIDGE in it })
        assertTrue(problems.any { it.startsWith("Missing") && IosFrameworks.RUNTIME in it })
    }

    @Test
    fun `finds the frameworks at the archive root or one folder deep`() {
        val flat = fakeFrameworks()
        assertEquals(flat, IosFrameworks.findFrameworksRoot(flat))

        val wrapped = Files.createTempDirectory("wrapped").toFile()
        val inner = File(wrapped, "Frameworks")
        fakeFrameworks(inner)
        assertEquals(inner, IosFrameworks.findFrameworksRoot(wrapped))
    }

    @Test
    fun `detects manual and Swift Package Manager integrations in the Xcode project`() {
        val manual = "7DE5 /* TalsecBridge.xcframework in Frameworks */ 7D2E /* TalsecRuntime.xcframework */"
        val spm = "66191A6B /* FreeRASP_KMP */ = {isa = XCSwiftPackageProductDependency; productName = FreeRASP_KMP; };"

        assertEquals(listOf("TalsecBridge.xcframework", "TalsecRuntime.xcframework"), IosFrameworks.conflictingReferences(manual, embedRuntime = true))
        assertEquals(listOf("Swift package product FreeRASP_KMP"), IosFrameworks.conflictingReferences(spm, embedRuntime = true))
        assertEquals(emptyList(), IosFrameworks.conflictingReferences("PRODUCT_NAME = Example;", embedRuntime = true))
    }

    @Test
    fun `allows a runtime from another component when only the bridge is embedded`() {
        val runtimeOnly = "7D2E /* TalsecRuntime.xcframework */"
        assertEquals(emptyList(), IosFrameworks.conflictingReferences(runtimeOnly, embedRuntime = false))
    }

    @Test
    fun `accepts runtime versions the bridge was verified with`() {
        assertTrue(IosFrameworks.isCompatibleRuntime("7.1.2"))
        assertTrue(IosFrameworks.isCompatibleRuntime("8.0.3"))
        assertFalse(IosFrameworks.isCompatibleRuntime("7.0.9"))
        assertFalse(IosFrameworks.isCompatibleRuntime("9.0.0"))
        assertFalse(IosFrameworks.isCompatibleRuntime("unknown"))
    }

    @Test
    fun `ships checksums for every framework binary`() {
        val metadata = PluginMetadata.load()

        assertTrue(metadata.version.isNotBlank())
        IosFrameworks.NAMES.forEach { name ->
            IosFrameworks.SLICES.forEach { slice ->
                assertEquals(64, metadata.checksums.getValue(IosFrameworks.checksumKey(name, slice)).length)
            }
        }
    }

    private fun fakeFrameworks(root: File = Files.createTempDirectory("frameworks").toFile()): File {
        IosFrameworks.NAMES.forEach { name ->
            IosFrameworks.SLICES.forEach { slice ->
                val dir = IosFrameworks.frameworkDir(root, name, slice)
                dir.mkdirs()
                File(dir, name).writeText("$name $slice")
            }
        }
        return root
    }

    private fun checksumsOf(root: File): Map<String, String> = IosFrameworks.NAMES.flatMap { name ->
        IosFrameworks.SLICES.map { slice ->
            IosFrameworks.checksumKey(name, slice) to IosFrameworks.sha256(File(IosFrameworks.frameworkDir(root, name, slice), name))
        }
    }.toMap()
}
