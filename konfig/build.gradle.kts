import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.12.0"
}

val pluginGroup: String by project
val pluginName: String by project
val pluginId: String by project
val pluginVersion: String by project

group = pluginGroup
version = pluginVersion

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }


repositories {
    mavenCentral()
}

repositories {
    mavenCentral()
    google()
    jcenter()
}


gradlePlugin {
    plugins {
        create(pluginName) {
            id = pluginId
            implementationClass = "fr.sjcqs.konfig.KonfigPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/sjcqs/gradle-konfig"
    vcsUrl = "https://github.com/sjcqs/gradle-konfig"
    description = "Gradle plugin to add multi-variants yaml settings to Android."
    tags = setOf("gradle", "plugin", "android", "config")
    (plugins) {
        (pluginName) {
            displayName = "Gradle Konfig"
            version = pluginVersion
        }
    }
}


publishing {
    repositories {
        maven {
            name = "test"
            url = uri("$rootDir/test-repository")
        }
    }
}

dependencies {
    val snakeYamlVersion: String by project
    val androidBuildToolsVersion: String by project
    val jupiterVersion: String by project
    val kotlinPoetVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("com.squareup", "kotlinpoet", kotlinPoetVersion)
    implementation("org.yaml", "snakeyaml", snakeYamlVersion)
    compileOnly("com.android.tools.build", "gradle", androidBuildToolsVersion)
    testImplementation("org.junit.jupiter", "junit-jupiter-api", jupiterVersion)
    testImplementation("org.junit.jupiter", "junit-jupiter-params", jupiterVersion)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", jupiterVersion)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
        )
        showStandardStreams = true
    }
    maxHeapSize = "1G"
}