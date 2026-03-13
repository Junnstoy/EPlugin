import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `java-library`
}

repositories {
    jitpack()
    lumine()
    phoenix()
    itemsadder()
}

dependencies {
    spigot()
    eplugin()
    eplugin("serialization")
    eplugin("hook-itemsadder")
    eplugin("hook-mmoitems")
    mythicLib()
    mmoitems()
    itemsadder()
    serializationCore()
}

java {
    withSourcesJar()
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}
