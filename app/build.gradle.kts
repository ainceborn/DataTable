plugins {
    androidApplication(isComposeEnable = true)
}

android {
    defineApp(
        namespace = "com.rainc.sample",
        isComposeEnable = true
    )
}

dependencies {
    material()
    androidXCore()
    androidLifecycle()
    kotlinToolsApi()
    composeLibs()
    lib()
}