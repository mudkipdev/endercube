plugins {
    java
    application
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "net.endercube"
version = "1.0.0"
application.mainClass = "net.endercube.endercube.Main"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // Minecraft
    implementation("net.minestom:minestom-snapshots:1c528d8ae2")
    implementation("net.kyori:adventure-text-serializer-plain:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-ansi:4.17.0")
    implementation("com.github.GoldenStack:window:-SNAPSHOT")
    implementation("dev.hollowcube:polar:1.9.5")

    // Configuration
    implementation("org.spongepowered:configurate-hocon:4.1.2")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // Other
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("club.minnced:discord-webhooks:0.8.4")
    implementation("redis.clients:jedis:5.1.1")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    mergeServiceFiles()
    archiveClassifier = ""
}

tasks.build {
    dependsOn(tasks.shadowJar)
}