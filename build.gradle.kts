plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.droidneststudio"
version = "1.0.0"

application {
    mainClass.set("com.droidneststudio.auth.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

// ✅ এটা Add করুন - Render এর জন্য Fat JAR
ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server Bundle
    implementation(libs.bundles.ktor.server)

    // Logging
    implementation(libs.logback.classic)

    // MongoDB
    implementation(libs.kmongo.coroutine)

    // Dependency Injection - Koin
    implementation(libs.bundles.koin)

    // Security - BCrypt
    implementation(libs.bcrypt)

    // Email
    implementation(libs.commons.email)

    // Environment Variables
    implementation(libs.dotenv.kotlin)

    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.koin.test)
}

// Task to copy .env file for run
tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}

// ✅ এটা Add করুন - Render এর জন্য stage task
tasks.register("stage") {
    dependsOn("buildFatJar")
}