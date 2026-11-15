package app.talsec.kmp.ios

import java.io.File
import java.security.MessageDigest
import java.util.Properties

internal object IosFrameworks {

    const val BRIDGE = "TalsecBridge"
    const val RUNTIME = "TalsecRuntime"
    val NAMES = listOf(BRIDGE, RUNTIME)

    const val DEVICE_SLICE = "ios-arm64"
    const val SIMULATOR_SLICE = "ios-arm64_x86_64-simulator"
    val SLICES = listOf(DEVICE_SLICE, SIMULATOR_SLICE)

    const val LIBRARY_GROUP = "com.aheaditec.talsec.security"
    const val LIBRARY_NAME = "freeRASP_KMP"
    const val SWIFT_PACKAGE_PRODUCT = "FreeRASP_KMP"

    // TalsecRuntime versions the bridge was verified with when another component provides the runtime
    private val MIN_RUNTIME = listOf(7, 1)
    private const val MAX_RUNTIME_MAJOR_EXCLUSIVE = 9

    fun sliceForPlatform(platformName: String): String = when (platformName) {
        "iphoneos" -> DEVICE_SLICE
        "iphonesimulator" -> SIMULATOR_SLICE
        else -> throw IllegalArgumentException("Unsupported Xcode platform '$platformName', expected iphoneos or iphonesimulator")
    }

    fun frameworkDir(root: File, name: String, slice: String): File = File(root, "$name.xcframework/$slice/$name.framework")

    fun checksumKey(name: String, slice: String): String = "sha256.$name.$slice"

    fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    // Returns one message per missing or modified binary, empty when all match
    fun verify(root: File, expected: Map<String, String>): List<String> = buildList {
        for (name in NAMES) {
            for (slice in SLICES) {
                val binary = File(frameworkDir(root, name, slice), name)
                val expectedSum = expected[checksumKey(name, slice)]
                when {
                    expectedSum == null -> add("No checksum is known for $name ($slice)")
                    !binary.isFile -> add("Missing ${binary.path}")
                    sha256(binary) != expectedSum -> add("${binary.path} does not match the freeRASP KMP release")
                }
            }
        }
    }

    // Release archives may wrap the frameworks in a folder, so look one level deep as well
    fun findFrameworksRoot(extracted: File): File? {
        if (File(extracted, "$BRIDGE.xcframework").isDirectory) return extracted
        return extracted.listFiles()
            ?.filter { it.isDirectory }
            ?.firstOrNull { File(it, "$BRIDGE.xcframework").isDirectory }
    }

    // Xcode project references showing the frameworks are also integrated manually or through Swift Package Manager
    fun conflictingReferences(pbxproj: String, embedRuntime: Boolean): List<String> = buildList {
        if ("$BRIDGE.xcframework" in pbxproj) add("$BRIDGE.xcframework")
        if (embedRuntime && "$RUNTIME.xcframework" in pbxproj) add("$RUNTIME.xcframework")
        if (Regex("""productName\s*=\s*"?$SWIFT_PACKAGE_PRODUCT"?\s*;""").containsMatchIn(pbxproj)) add("Swift package product $SWIFT_PACKAGE_PRODUCT")
    }

    fun isCompatibleRuntime(version: String): Boolean {
        val parts = version.trim().split('.').map { it.toIntOrNull() ?: return false }
        if (parts.isEmpty()) return false
        val major = parts[0]
        val minor = parts.getOrElse(1) { 0 }
        if (major >= MAX_RUNTIME_MAJOR_EXCLUSIVE) return false
        return major > MIN_RUNTIME[0] || (major == MIN_RUNTIME[0] && minor >= MIN_RUNTIME[1])
    }
}

internal class PluginMetadata(val version: String, val checksums: Map<String, String>) {

    companion object {
        private const val RESOURCE = "/app/talsec/kmp/ios/frameworks.properties"

        fun load(): PluginMetadata {
            val properties = Properties()
            val stream = PluginMetadata::class.java.getResourceAsStream(RESOURCE)
                ?: error("Missing $RESOURCE in the app.talsec.kmp.ios plugin")
            stream.use { properties.load(it) }
            val checksums = properties.stringPropertyNames()
                .filter { it.startsWith("sha256.") }
                .associateWith { properties.getProperty(it) }
            return PluginMetadata(properties.getProperty("version"), checksums)
        }
    }
}
