import com.android.build.gradle.internal.tasks.AarMetadataTask
import cn.lalaki.pub.BaseCentralPortalPlusExtension.PublishingType

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("cn.lalaki.central") version "1.2.7"
    `maven-publish`
    signing
}
android {
    namespace = "cn.lalaki"
    compileSdk = 35
    version = "1.6"

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
            version = "3.31.4"
        }
    }
    base.archivesName = "SerialPort.Android"
    ndkVersion = "28.0.12916984 rc3"
}

tasks.withType<AarMetadataTask> {
    isEnabled = false
}

tasks.configureEach {
    if (name.contains("checkDebugAndroidTestAarMetadata"))
        enabled = false
}
val localMavenRepo = uri("D:\\repo\\")
centralPortalPlus {
    url = localMavenRepo
    tokenXml = uri("D:\\user_token.xml")
    publishingType = PublishingType.USER_MANAGED
}
publishing {
    repositories {
        maven {
            url = localMavenRepo
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