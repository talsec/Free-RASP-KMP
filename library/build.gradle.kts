import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.cocoaPods)
}

group = "com.aheaditec.talsec"
version = "1.0.13"

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    cocoapods {
        name = "freeraspKMP"
        summary = "RASP SDK for iOS mobile devices."
        homepage = "talsec.app"
        version = project.version.toString()
        license = "{ :type => 'MIT' }"
        authors = "Talsec"
        description = "FreeRASP for iOS is a lightweight and easy-to-use mobile app protection and security monitoring SDK."

        framework {
            baseName = "freeraspKMP"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    val iosTargets = listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    )

    iosTargets.forEach { target ->
        target.binaries.framework {
            baseName = "freeraspKMP"
            isStatic = true
        }

        target.compilations["main"].cinterops.create("Talsec") {
            defFile(project.file("src/nativeInterop/cinterop/talsec.def"))
            val nativeDir = project.file("src/iosMain/nativeTalsec")
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
                implementation("com.aheaditec.talsec.security:TalsecSecurity-Community-KMP:17.0.0")
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {
    coordinates(group.toString(), "library", version.toString())
    pom {
        name = "freeRASP KMP"
        description = "A library."
        inceptionYear = "2025"
        url = "https://github.com/talsec/Free-RASP-KMP"
        licenses {
            license {
                name = "XXX"
                url = "YYY"
                distribution = "ZZZ"
            }
        }
        developers {
            developer {
                id = "XXX"
                name = "YYY"
                url = "ZZZ"
            }
        }
        scm {
            url = "XXX"
            connection = "YYY"
            developerConnection = "ZZZ"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.PodspecTask> {
    doLast {
        val podspecFile = outputFile
        var podspecText = podspecFile.readText()

        // Add a dummy git source to satisfy CocoaPods
        podspecText = podspecText.replace(
            "spec.source           = { :http=> '' }",
            "spec.source           = { :git => \"Not a real git repo\" }"
        )

        // Add the Talsec frameworks to the vendored_frameworks
        podspecText = podspecText.replace(
            "spec.vendored_frameworks      = 'build/cocoapods/framework/freeraspKMP.framework'",
            "spec.vendored_frameworks      = 'build/cocoapods/framework/freeraspKMP.framework', 'src/iosMain/nativeTalsec/TalsecBridge.xcframework', 'src/iosMain/nativeTalsec/TalsecRuntime.xcframework'"
        )

        podspecFile.writeText(podspecText)
    }
}