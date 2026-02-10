import com.github.jengelman.gradle.plugins.shadow.transformers.AppendingTransformer

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

group = "github.renderbr.hytale"
version = "0.2.8"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.hytale.com/release")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    compileOnly("com.hypixel.hytale:Server:2026.01.27-734d39026")
    implementation(files("libs/AverageHytaleCore.jar"))
    implementation("org.slf4j:slf4j-simple:2.0.12")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    isZip64 = true
}