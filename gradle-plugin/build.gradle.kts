import java.security.MessageDigest
import java.util.Properties

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

// The plugin shares its version with the freeRASP KMP library, declared once in the root gradle.properties
val rootProperties = Properties().apply {
    rootDir.resolve("../gradle.properties").inputStream().use { load(it) }
}
val localProperties = Properties().apply {
    val file = rootDir.resolve("../local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun getVariable(name: String): String {
    return System.getenv(name) ?: localProperties.getProperty(name) ?: ""
}

group = "com.aheaditec.talsec.security"
version = rootProperties.getProperty("freeraspKmpVersion")

dependencies {
    // Compiled against the Kotlin version embedded in Gradle; the integrator's Kotlin Gradle plugin is used at runtime
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
}

gradlePlugin {
    plugins {
        create("talsecKmpIos") {
            id = "app.talsec.kmp.ios"
            implementationClass = "app.talsec.kmp.ios.TalsecKmpIosPlugin"
            displayName = "freeRASP KMP iOS integration"
            description = "Embeds and signs the freeRASP iOS frameworks during the Xcode build of a Kotlin Multiplatform app"
        }
    }
}

// Writes the plugin version and the SHA-256 of every shipped framework binary, so downloaded frameworks can be verified
abstract class GenerateFrameworkMetadata : DefaultTask() {

    @get:Input
    abstract val frameworksVersion: Property<String>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val nativeDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val lines = mutableListOf("version=${frameworksVersion.get()}")
        for (name in listOf("TalsecBridge", "TalsecRuntime")) {
            for (slice in listOf("ios-arm64", "ios-arm64_x86_64-simulator")) {
                val binary = nativeDir.file("$name.xcframework/$slice/$name.framework/$name").get().asFile
                val digest = MessageDigest.getInstance("SHA-256").digest(binary.readBytes())
                lines += "sha256.$name.$slice=" + digest.joinToString("") { "%02x".format(it) }
            }
        }
        val file = outputDir.file("app/talsec/kmp/ios/frameworks.properties").get().asFile
        file.parentFile.mkdirs()
        file.writeText(lines.joinToString("\n", postfix = "\n"))
    }
}

val generateFrameworkMetadata = tasks.register<GenerateFrameworkMetadata>("generateFrameworkMetadata") {
    frameworksVersion.set(version.toString())
    nativeDir.set(layout.projectDirectory.dir("../library/src/iosMain/nativeTalsec"))
    outputDir.set(layout.buildDirectory.dir("generated/frameworkMetadata"))
}

tasks.processResources {
    from(generateFrameworkMetadata)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "GcpArtifactRegistry"
            url = uri("https://europe-west3-maven.pkg.dev/${getVariable("GCP_PROJECT_ID")}/${getVariable("GCP_REPO_NAME")}")
            credentials {
                username = getVariable("GCP_USERNAME")
                password = getVariable("GCP_PASSWORD")
            }
        }
    }
}
