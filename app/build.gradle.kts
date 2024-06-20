import com.android.build.gradle.tasks.PackageAndroidArtifact

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cn.lalaki.comm"
    compileSdkPreview = "VanillaIceCream"
    defaultConfig {
        applicationId = "cn.lalaki.comm"
        minSdk = 21
        targetSdk = 34
        versionCode = 10
        versionName = "16"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.guava:guava:33.0.0-android")
    //implementation("cn.lalaki:SerialPort.Android:1.2")
    implementation(project(":library"))
}

tasks.configureEach {
    if (name.contains("aarmetadata", ignoreCase = true)) enabled = false
}

tasks.withType<PackageAndroidArtifact> {
    doFirst { appMetadata.asFile.get().writeText("") }
}