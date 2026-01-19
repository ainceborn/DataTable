plugins {
    libPlugins(isComposeEnable = true)
    id("maven-publish")
}
group = "com.github.ainceborn"

android {
    defineLib(
        namespace = Metadata.Id,
        isComposeEnable = true
    )
}

dependencies {
    androidXCore()
    androidLifecycle()
    kotlinToolsApi()
    composeLibs()
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.ainceborn"
                artifactId = "data-table"
                version = Metadata.versionName
            }
        }
    }
}