pluginManagement {
    repositories {
        maven("https://mirrors.cloud.tencent.com/maven/")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://mirrors.cloud.tencent.com/maven/")
        google()
        mavenCentral()
    }
}
rootProject.name = "android_easy_serial_port"
include(":app")
include(":library")