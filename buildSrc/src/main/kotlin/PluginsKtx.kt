import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugin.use.PluginDependenciesSpec

fun PluginDependenciesSpec.androidApplication(
    isComposeEnable:Boolean= false,
    isJacocoEnable: Boolean = false
) {
    id("com.android.application")
    defaultPluginSpec(isComposeEnable = isComposeEnable, isJacocoEnable = isJacocoEnable)
}

fun PluginDependenciesSpec.libPlugins(
    isComposeEnable:Boolean= false,
    isJacocoEnable: Boolean = false
) {
    id("com.android.library")
    defaultPluginSpec(isComposeEnable = isComposeEnable, isJacocoEnable = isJacocoEnable)
}

private fun PluginDependenciesSpec.defaultPluginSpec(
    isComposeEnable:Boolean,
    isJacocoEnable: Boolean
) {
    kotlin("android")
    id("kotlin-parcelize")

    if(isComposeEnable){
        id("org.jetbrains.kotlin.plugin.compose")
    }
    if(isJacocoEnable){
        id("jacoco")
    }
}