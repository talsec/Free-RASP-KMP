import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

fun getVariable(name: String): String {
    return System.getenv(name) ?: localProperties.getProperty(name) ?: ""
}


group = "com.aheaditec.talsec.security"
version = "2.0.0"
kotlin {
    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    applyDefaultHierarchyTemplate()

    val iosTargets = listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    )

    val nativeDir = project.file("src/iosMain/nativeTalsec")

    iosTargets.forEach { target ->
        target.compilations["main"].cinterops.create("Talsec") {
            defFile(project.file("src/nativeInterop/cinterop/talsec.def"))

            val headersDir = when (target.konanTarget) {
                org.jetbrains.kotlin.konan.target.KonanTarget.IOS_ARM64 ->
                    "$nativeDir/TalsecBridge.xcframework/ios-arm64/TalsecBridge.framework/Headers"
                else ->
                    "$nativeDir/TalsecBridge.xcframework/ios-arm64_x86_64-simulator/TalsecBridge.framework/Headers"
            }
            compilerOpts("-F$nativeDir", "-I$headersDir")

            linkerOpts("-F$nativeDir", "-framework", "TalsecBridge", "-framework", "TalsecRuntime")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            }
        }

        val iosMain by getting

        val androidMain by getting {
            languageSettings.optIn("kotlin.ExperimentalMultiplatform")

            dependencies{
                implementation("com.aheaditec.talsec.security:TalsecSecurity-Community-KMP:18.3.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
                implementation("androidx.startup:startup-runtime:1.2.0")
                implementation("androidx.annotation:annotation:1.9.1")
                implementation("androidx.core:core-ktx:1.17.0")
                implementation("androidx.lifecycle:lifecycle-process:2.9.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }

}

android {
    if (!project.hasProperty("namespace")) {
        namespace = "com.freeraspkmp"
    }
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        consumerProguardFiles("proguard-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


}

publishing {
    repositories {
        maven {
            name = "GcpArtifactRegistry"

            val gcpPublicUrl = "https://europe-west3-maven.pkg.dev"

            val projectId = getVariable("GCP_PROJECT_ID")
            val repoName = getVariable("GCP_REPO_NAME")

            url = uri("$gcpPublicUrl/$projectId/$repoName")

            credentials {
                username = getVariable("GCP_USERNAME")
                password = getVariable("GCP_PASSWORD")
            }
        }
    }
}

mavenPublishing {
    coordinates(group.toString(), "freeRASP_KMP", version.toString())

    pom {
        name = "freeRASP KMP"
        description = "Talsec freeRASP for Kotlin Multiplatform. Runtime App Self Protection (RASP) SDK for Android and iOS."
        inceptionYear = "2025"
        url = "https://github.com/talsec/Free-RASP-KMP"
        licenses {
            license {
                name = "MIT License"
                url = "https://github.com/talsec/Free-RASP-KMP/LICENSE"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "talsec"
                name = "Talsec Team"
                url = "https://www.talsec.app"
            }
        }
        scm {
            url = "https://github.com/talsec/Free-RASP-KMP"
            connection = "scm:git:https://github.com/talsec/Free-RASP-KMP.git"
            developerConnection = "scm:git:ssh://git@github.com/talsec/Free-RASP-KMP.git"
        }
    }
}
