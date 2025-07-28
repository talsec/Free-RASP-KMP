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
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    /*listOf(
        iosX64(), // Pridal som aj x64, ktorý si mal definovaný vyššie
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Library"
            isStatic = false

            // SPRÁVNE NASTAVENIE CESTY
            linkerOpts("-F", "${projectDir}/src/iosMain", "-framework", "TalsecRuntime")
        }
        iosTarget.compilations["main"].apply {
            cinterops.create("talsecruntime") {
                definitionFile = file("src/nativeInterop/cinterop/talsecruntime.def")
            }
        }
    }*/

    /*listOf(iosArm64(), iosX64(), iosSimulatorArm64()).forEach {
        it.compilations.getByName("main") {
            // Toto explicitne NIE JE potrebné, swiftklib plugin všetko nakonfiguruje automaticky,
            // ale môžeš mať, ak chceš:
            cinterops.create("FreeRASP")
        }
    }*/

    /*cocoapods {
        version = "1.0"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"
        name = "freeRASP_KMP"
        ios.deploymentTarget = "14.0"

        framework {
            baseName = "freeRASP_KMP"
            isStatic = false
        }

        pod(
            "TalsecRuntime",
            path = project.file("/Users/martinzigrai/Documents/iOS/Free-RASP-iOS/Talsec")
            )
    }*/

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            }
        }
        /*androidMain.dependencies {
            implementation("com.aheaditec.talsec.security:TalsecSecurity-Community:16.0.1")
        }*/
        val androidMain by getting {
            //dependsOn(commonMain)
            //kotlin.srcDir("src/androidMain/kotlin")
            languageSettings.optIn("kotlin.ExperimentalMultiplatform")

            dependencies{
                api("com.aheaditec.talsec.security:TalsecSecurity-Community:16.0.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
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

/*swiftklib {
    create("TalsecRuntime") {
        path = file("/Users/martinzigrai/Documents/iOS/Free-RASP-iOS/Talsec/TalsecRuntime.xcframework")
        packageName = "TalsecRuntime"
    }
}*/


/*swiftklib {
    create("FreeRASP") {
        path = file("/Users/martinzigrai/Documents/iOS/Free-RASP-iOS/Talsec/TalsecRuntime.xcframework")
        packageName = "com.lynxsft.TalsecRuntime"
    }
}*/


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
