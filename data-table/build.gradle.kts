plugins {
    libPlugins(isComposeEnable = true)
    id("maven-publish")
}

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
                groupId = Metadata.Id
                artifactId = "data-table"
                version = Metadata.versionName
                from(components["release"])
            }
        }
    }
}