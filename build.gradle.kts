import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val calimero = "3.0-SNAPSHOT"
val j2mod = "3.2.1"
val junitJupiter = "5.12.1"
val kotlinxCoroutinesReactor = "1.10.2"
val kotlinxSerializationJson = "1.8.1"
val ktor = "1.6.4"
val pgjdbcNg = "0.8.9"

plugins {
    id("application")
    id("com.diffplug.spotless") version "7.0.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springframework.boot") version "3.5.0"
    id("org.jetbrains.kotlin.plugin.spring") version "2.2.0-RC"
    kotlin("jvm") version "2.2.0-RC"
    kotlin("plugin.serialization") version "2.2.0-RC"
}

dependencies {
    implementation("com.ghgande", "j2mod", j2mod)
    implementation("com.impossibl.pgjdbc-ng", "pgjdbc-ng", pgjdbcNg)
    implementation("io.calimero", "calimero-core", calimero)
    implementation("io.calimero", "calimero-device", calimero)
    implementation("io.ktor", "ktor-client-cio", ktor)
    implementation("io.ktor", "ktor-client-core", ktor)
    implementation("io.ktor", "ktor-client-serialization", ktor)
    implementation("org.jetbrains.kotlin", "kotlin-reflect")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-reactor", kotlinxCoroutinesReactor)
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", kotlinxSerializationJson)
    implementation("org.springframework.boot", "spring-boot-starter")
    implementation("org.springframework.boot", "spring-boot-starter-actuator")
    implementation("org.springframework.boot", "spring-boot-starter-aop")
    implementation("org.springframework.boot", "spring-boot-starter-web")
    runtimeOnly("io.calimero", "calimero-rxtx", calimero)
    testImplementation("org.junit.jupiter", "junit-jupiter-api", junitJupiter)
    testImplementation("org.springframework.boot", "spring-boot-starter-test")
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitJupiter)
}

repositories {
    mavenCentral()

    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

application {
    mainClass = "housemeasurementlogger.infrastructure.HouseMeasurementLoggerApplicationKt"
}

spotless {
    kotlin {
        target("**/*.kt", "**/*.kts")
        ktfmt().kotlinlangStyle()
    }

    yaml {
        target("**/*.yaml", "**/*.yml")
        jackson()
    }

    format("misc") {
        target("**/*.gitignore", "**/*.properties", "**/*.md", "LICENSE")

        trimTrailingWhitespace()
        indentWithSpaces(4)
        endWithNewline()
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = freeCompilerArgs.get() + listOf("-Xjsr305=strict")
        jvmTarget = JvmTarget.JVM_24
    }
}

tasks.withType<Test> { useJUnitPlatform() }

group = "cx.mh"

version = "1.0-SNAPSHOT"
