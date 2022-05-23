import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me.jmsj"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.google.code.gson:gson:2.8.5") // GSON -> DataClass
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.20") // LOGGER
    implementation("org.slf4j:slf4j-simple:1.7.32") // LOGGER
    implementation("com.oracle.database.jdbc:ojdbc10:19.14.0.0")
    implementation("com.h2database:h2:2.1.210")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}