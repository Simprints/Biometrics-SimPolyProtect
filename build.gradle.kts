plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

val projectGroupId = "com.simprints.biometrics"
val projectArtifactId = "polyprotect"
val projectVersion = "2024.4.1"

android {
    namespace = "$projectGroupId.$projectArtifactId"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    testImplementation(libs.junit)
}

publishing {

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/simprints/Biometrics-SimPolyProtect")
            credentials {
                username =
                    project.findProperty("gpr.user") as String? ?: System.getenv("GITHUBUSERNAME")
                password =
                    project.findProperty("gpr.key") as String? ?: System.getenv("GITHUBTOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("ReleaseAar") {
            groupId = projectGroupId
            artifactId = projectArtifactId
            version = projectVersion
            afterEvaluate { artifact(tasks.getByName("bundleReleaseAar")) }
        }
    }
}
