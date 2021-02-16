buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }


    val kotlinVersion: String by project
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}