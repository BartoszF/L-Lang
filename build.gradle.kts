import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "pl.bfelis"
version = "0.0.4"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    google()
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        google()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}

tasks.register("package") {
    dependsOn(tasks.withType<Test>())
    dependsOn(project(":modules:lang").tasks.getByName("jar"))
    dependsOn(project(":modules:exec").tasks.getByName("createExe"))
}
