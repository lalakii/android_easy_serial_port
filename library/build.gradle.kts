import com.android.build.gradle.internal.tasks.AarMetadataTask

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
    signing
}
android {
    namespace = "cn.lalaki"
    compileSdk = 34
    version = "1.4"

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/jni/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    base.archivesName = "SerialPort.Android"
    ndkVersion = "27.0.11718014 rc1"
}

tasks.withType<AarMetadataTask> {
    isEnabled = false
}

tasks.configureEach {
    if (name.contains("checkDebugAndroidTestAarMetadata"))
        enabled = false
}
publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            val publishToLocal = false
            if (publishToLocal) {
                url = uri("D:\\repo\\")
            } else {
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = "iamverycute"
                    password = System.getenv("my_final_password")
                }
            }
        }
    }
    publications {
        create<MavenPublication>("release") {
            artifactId = "SerialPort.Android"
            groupId = "cn.lalaki"
            afterEvaluate { artifact(tasks.named("bundleReleaseAar")) }
            pom {
                name = "SerialPort.Android"
                description = "Android platform serial communication implementation."
                url = "https://github.com/lalakii/android_easy_serial_port"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        name = "lalakii"
                        email = "dazen@189.cn"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/lalakii/android_easy_serial_port.git"
                    developerConnection = "scm:git:https://github.com/lalakii/android_easy_serial_port.git"
                    url = "https://github.com/lalakii/android_easy_serial_port"
                }
            }
        }
    }
}
signing {
    useGpgCmd()
    sign(publishing.publications["release"])
}