import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "com.aheaditec.talsec"
version = "1.0.0"
kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            }
        }

        val androidMain by getting {
            dependencies{
                implementation("com.aheaditec.talsec.security:TalsecSecurity-Community-KMP:16.0.4")
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
