import com.android.build.gradle.tasks.PackageAndroidArtifact

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cn.lalaki.comm"
    compileSdk = 35
    defaultConfig {
        applicationId = "cn.lalaki.comm"
        minSdk = 21
        targetSdk = 35
        versionCode = 10
        versionName = "17"
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
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.guava:guava:33.4.0-android")
    //implementation("cn.lalaki:SerialPort.Android:1.4")
    implementation(project(":library"))
}

tasks.configureEach {
    if (name.contains("aarmetadata", ignoreCase = true)) enabled = false
}

tasks.withType<PackageAndroidArtifact> {
    doFirst { appMetadata.asFile.get().writeText("") }
}