import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

internal fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? =
    add("implementation", dependencyNotation)

internal fun DependencyHandler.debugImplementation(depName: String): Dependency? =
    add("debugImplementation", depName)

internal fun DependencyHandler.qaImplementation(depName: String): Dependency? =
    add("QAImplementation", depName)

internal fun DependencyHandler.releaseImplementation(depName: String): Dependency? =
    add("releaseImplementation", depName)

internal fun DependencyHandler.api(depName: String): Dependency? =
    add("api", depName)

internal fun DependencyHandler.api(dep: Any): Dependency? =
    add("api", dep)

internal fun DependencyHandler.kapt(depName: String): Dependency? =
    add("kapt", depName)

internal fun DependencyHandler.testImplementation(depName: String): Dependency? =
    add("testImplementation", depName)

internal fun DependencyHandler.androidTestImplementation(depName: String): Dependency? =
    add("androidTestImplementation", depName)

internal fun DependencyHandler.androidTestUtil(depName: String): Dependency? =
    add("androidTestUtil", depName)

internal fun DependencyHandler.project(depName: String) {
    add("implementation", (project(mapOf("path" to depName))))
}

internal fun DependencyHandler.projectApi(depName: String) {
    add("api", (project(mapOf("path" to depName))))
}

internal fun DependencyHandler.ksp(depName: String): Dependency? =
    add("ksp", depName)

internal fun LibraryExtension.`kotlinOptions`(configure: Action<org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("kotlinOptions", configure)

internal fun BaseExtension.`kotlinOptions`(configure: Action<org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("kotlinOptions", configure)

internal fun org.gradle.api.Project.`kotlin`(configure: Action<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("kotlin", configure)