import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.lib() {
    project(":data-table")
}