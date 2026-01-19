// Top-level build file where you can add configuration options common to all sub-projects/modules.
import java.util.Locale

buildscript {
	repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://www.jitpack.io" ) }
	}

	dependencies {
		classpath("com.android.tools.build:gradle:8.6.0")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Metadata.Versions.kotlinVersion}")
		classpath("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:${Metadata.Versions.kotlinVersion}")
	}
}

allprojects {
	repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://www.jitpack.io" ) }
	}
}