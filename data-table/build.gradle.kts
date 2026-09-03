plugins {
    libPlugins(isComposeEnable = true)
    id("maven-publish")
}
group = "com.github.ainceborn"
version = Metadata.versionName

android {
    defineLib(
        namespace = Metadata.Id,
        isComposeEnable = true
    )

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
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