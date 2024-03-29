import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("edu.sc.seis.launch4j") version "2.5.4"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

val kotestVersion = "5.5.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")

    implementation(project(":modules:lang"))
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "pl.bfelis.llang.exec.MainKt"
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("shadow")
    mergeServiceFiles()
    manifest {
        attributes(mapOf("Main-Class" to "pl.bfelis.llang.exec.MainKt"))
    }
}

tasks.build {
    dependsOn(tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>())
}

application {
    mainClass.set("pl.bfelis.llang.exec.MainKt")
}

launch4j {
    headerType = "console"
    outfile = "llang.exe"
    outputDir = "llang"
    mainClassName = application.mainClass.get()
    jarTask = project.tasks.shadowJar.get()
    bundledJreAsFallback = true
    bundledJrePath = "%HKEY_LOCAL_MACHINE\\SOFTWARE\\UnusualJavaVendor\\JavaHome%;%JAVA_HOME%;%PATH%"
    dontWrapJar = false
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}
