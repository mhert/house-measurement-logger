import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    application
}

sourceSets {
    main {
        dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
        }
    }
}

group = "me.mhert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()

    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

dependencies {
    implementation("com.github.calimero:calimero-core:2.5.1")
    implementation("com.github.calimero:calimero-device:2.5.1")
    implementation("com.github.calimero:calimero-rxtx:2.5.1")
    implementation("com.ghgande:j2mod:3.2.1")
    implementation("io.ktor:ktor-client-core:1.6.4")
    implementation("io.ktor:ktor-client-cio:1.6.4")
    implementation("io.ktor:ktor-client-serialization:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("com.impossibl.pgjdbc-ng", "pgjdbc-ng", "0.8.9")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.13")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
