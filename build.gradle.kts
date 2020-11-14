import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.10"
}
group = "me.wulkanat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/koltinx")
}

dependencies {
    testImplementation(kotlin("test-junit"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")

    compile("net.dv8tion:JDA:4.2.0_189")
    compile("org.jsoup:jsoup:1.13.1")
    compile("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
}

apply {
    plugin("kotlinx-serialization")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar>() {
    manifest {
        attributes(mapOf(Pair("Main-Class", "de.wulkana.MainKt")))
    }
}

tasks.create<Jar>("fatJar") {
    archiveBaseName.set("${project.name}-all")
    manifest {
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "de.wulkanat.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}
