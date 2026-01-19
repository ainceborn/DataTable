// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}


dependencies {
    implementation("com.android.tools.build:gradle:8.6.0")
    implementation(kotlin("gradle-plugin", "2.0.21"))
}