# SkyWayClient
SkyWayClient for Kotlin

## Install

### 1. Add SkyWay Module
To get started, download the `skyway.aar`.
Download it from the skyway-android-sdk [release page](https://github.com/skyway/skyway-android-sdk/releases).

Once downloaded, import skyway.aar into your android project to create the skyway module.

[How to add an aar file as a module](https://developer.android.com/studio/projects/android-library#AddDependency)

### 2. GitHub Packages Setting
Add the maven configuration to the build.gradle of your project.

[How to create a personal access token](https://docs.github.com/ja/free-pro-team@latest/github/authenticating-to-github/creating-a-personal-access-token)

```build.gradle
allprojects {
    repositories {
        ...
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Takahana/SkyWayClient")
            credentials {
                username = YOUR_GITHUB_USERNAME
                password = YOUR_GITHUB_TOKEN
            }
        }
    }
}
```

### 3. Add Dependencies

```build.gradle
dependencies {
    ...
    implementation "tech.takahana.skywayclient:skywayclient:1.0.0"
    implementation project(":skyway")
}
```
