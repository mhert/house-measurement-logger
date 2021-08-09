import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    application
}

sourceSets {
    main {
        dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3")
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
    implementation("com.github.calimero:calimero-core:2.4")
    implementation("com.github.calimero:calimero-device:2.4")
    implementation("com.github.calimero:calimero-rxtx:2.4")
    implementation("com.ghgande:j2mod:2.7.0")
    implementation("khttp:khttp:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("org.litote.kmongo:kmongo:4.2.3")
    implementation("org.litote.kmongo:kmongo-async:4.2.3")
    implementation("org.litote.kmongo:kmongo-coroutine:4.2.3")
    implementation("org.litote.kmongo:kmongo-serialization:4.2.3")
    implementation("com.impossibl.pgjdbc-ng", "pgjdbc-ng", "0.8.9")
    runtimeOnly("org.slf4j:slf4j-simple:1.8.0-beta4")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "14"
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

application {
    mainClassName = "MainKt"
}
