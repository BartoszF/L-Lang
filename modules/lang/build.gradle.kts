
plugins {
    kotlin("jvm") version "1.7.20"
    application
}

val kotestVersion = "5.5.4"

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<Jar>() {
    dependsOn(tasks.withType<Test>())
}
