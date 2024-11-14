plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.1.0-RC"
    `maven-publish`
}

val projectGroupId = "com.simprints"
val projectArtifactId = "biometrics_simpolyprotect"
val projectVersion = "0.0.2-SNAPSHOT"

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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
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
