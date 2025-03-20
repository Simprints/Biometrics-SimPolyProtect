plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

val projectGroupId = "com.simprints.biometrics"
val projectArtifactId = "polyprotect"
val projectVersion = "2025.1.1"

android {
    namespace = "$projectGroupId.$projectArtifactId"
    compileSdk = 35
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    testImplementation(libs.testing.junit)
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
