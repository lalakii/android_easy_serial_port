import com.android.build.gradle.internal.tasks.AarMetadataTask

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "cn.lalaki"
    compileSdk = 34
    version = "1.1"

    defaultConfig {
        minSdk = 12
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    ndkVersion = "26.2.11394342"
}

tasks.withType<AarMetadataTask> {
    isEnabled = false
}

tasks.configureEach {
    if (name == "assembleRelease") {
        doLast {
            val buildRoot = file("${project.layout.buildDirectory.get()}\\outputs\\aar")
            val pomXml = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <!-- This module was also published with a richer model, Gradle metadata,  -->
  <!-- which should be used instead. Do not delete the following line which  -->
  <!-- is to indicate to Gradle or any Gradle module metadata file consumer  -->
  <!-- that they should prefer consuming it instead. -->
  <!-- do_not_remove: published-with-gradle-metadata -->
  <modelVersion>4.0.0</modelVersion>
  <groupId>cn.lalaki</groupId>
  <artifactId>${base.archivesName.get()}</artifactId>
  <version>${version}</version>
  <packaging>aar</packaging>
  <name>${base.archivesName.get()}</name>
  <description>Android platform serial communication implementation.</description>
  <url>https://github.com/lalakii/android_easy_serial_port</url>
  <inceptionYear>2024</inceptionYear>
  <licenses>
    <license>
      <name>The Apache Software License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <developers>
    <developer>
      <name>lalakii</name>
      <email>dazen@189.cn</email>
      <organization>lalaki.cn</organization>
    </developer>
  </developers>
  <scm>
    <connection>scm:git:https://github.com/lalakii/android_easy_serial_port.git</connection>
    <url>https://github.com/lalakii/android_easy_serial_port</url>
  </scm>
</project>
"""
            val pomFile =
                file("${buildRoot.absolutePath}\\${base.archivesName.get()}-${version}.pom")
            pomFile.writeText(pomXml)
            sign(pomFile.parentFile, "*.pom")
            sign(pomFile.parentFile, "*.aar")
            val osName = System.getProperty("os.name").lowercase()
            if (osName.contains("windows"))
                openExplorer()
        }
    }
}

fun openExplorer() {
    exec {
        executable = "cmd.exe"
        args(
            "/c",
            "sleep",
            "3",
            "&&",
            "start",
            "${project.layout.buildDirectory.get()}\\outputs\\aar\\"
        )
    }
}

fun sign(path: File, fileName: String) {
    try {
        exec {
            workingDir = path
            executable = "gpg"
            args("--yes", "--armor", "--detach-sign", fileName)
        }
    } catch (_: Exception) {
        System.err.println("Please install GnuPG: https://gnupg.org/download/")
    }
}