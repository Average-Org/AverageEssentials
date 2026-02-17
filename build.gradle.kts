import com.github.jengelman.gradle.plugins.shadow.transformers.AppendingTransformer

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

group = "github.renderbr.hytale"
version = "0.2.10"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.hytale.com/release")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    val hytaleServer = "com.hypixel.hytale:Server:2026.02.17-255364b8e"
    compileOnly(hytaleServer)
    testImplementation(hytaleServer)
    implementation(files("libs/AverageHytaleCore.jar"))
    implementation("org.slf4j:slf4j-simple:2.0.12")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("net.bytebuddy.experimental", "true")
}

tasks.shadowJar {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    isZip64 = true
}