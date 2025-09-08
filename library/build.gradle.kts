import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    //alias(libs.plugins.kotlinCocoapods)
    //id("io.github.ttypic.swiftklib") version "0.6.4"

}

group = "com.aheaditec.talsec"
version = "1.0.0"

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
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

            // -------- HEADERS (podľa architektúry) --------
            val headersDir = when (target.konanTarget) {
                org.jetbrains.kotlin.konan.target.KonanTarget.IOS_ARM64 ->
                    "$nativeDir/TalsecBridge.xcframework/ios-arm64/TalsecBridge.framework/Headers"
                else ->
                    "$nativeDir/TalsecBridge.xcframework/ios-arm64_x86_64-simulator/TalsecBridge.framework/Headers"
            }
            compilerOpts("-F$nativeDir", "-I$headersDir")

            // -------- LINKER --------
            linkerOpts("-F$nativeDir", "-framework", "TalsecBridge", "-framework", "TalsecRuntime")
        }
    }

    /*iosTargets.forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = false

            linkerOpts.add("-F$nativeDir")
            linkerOpts.addAll(
                listOf(
                    "-framework", "TalsecBridge",
                    "-framework", "TalsecRuntime"
                )
            )
        }

        iosTarget.compilations.getByName("main") {
            //cinterops.create("TalsecBridge")
            cinterops.create("Talsec"){
                defFile(project.file("src/nativeInterop/cinterop/talsec.def"))

                compilerOpts(
                    "-F$nativeDir",

                    "-I$nativeDir/TalsecBridge.xcframework/ios-arm64_x86_64-simulator/TalsecBridge.framework/Headers",
                    "-I$nativeDir/TalsecBridge.xcframework/ios-arm64/TalsecBridge.framework/Headers",
                    "-I$nativeDir/TalsecRuntime.xcframework/ios-arm64_x86_64-simulator/TalsecRuntime.framework/Headers",
                    "-I$nativeDir/TalsecRuntime.xcframework/ios-arm64/TalsecRuntime.framework/Headers"
                )
            }
        }
    }*/

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
                api("com.aheaditec.talsec.security:TalsecSecurity-Community:16.0.1")
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
    namespace = "com.aheaditec.talsec"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


}

mavenPublishing {
    //uncomment for publishing to Maven Central
    /*publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications() */

    coordinates(group.toString(), "library", version.toString())

    pom {
        name = "freeRASP KMP"
        description = "A library."
        inceptionYear = "2025"
        url = "https://github.com/martinzigrai/Free-RASP-KMP"
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
