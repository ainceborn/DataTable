plugins {
    libPlugins(isComposeEnable = true)
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