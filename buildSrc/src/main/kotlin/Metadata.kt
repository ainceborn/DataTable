import org.gradle.api.JavaVersion

object Metadata {
    const val Id = "com.rainc.compose.datatable"
    const val minSDK = 30
    const val compileSdk = 35
    const val targetSdk = 35
    const val versionCode = 2
    const val versionName = "1.0.4"
    const val buildToolsVersion = "35.0.1"

    object Versions {
        const val kotlinVersion = "2.1.20"
        val javaVersion = JavaVersion.VERSION_17
        const val koinVersion = "4.0.1"
        const val materialVersion = "1.12.0"

        const val androidxCoreVersion = "1.16.0"
        const val appcompatVersion = "1.7.0"
        const val constraintlayoutToolsVersion = "2.1.4"
        const val androidLifecycleVersion = "2.8.7"

        const val composeBomVersion = "2025.02.00"
        const val activityComposeVersion = "1.9.2"

        const val kotlinToolsVersion = "1.10.1"
        const val kotlinSerializationVersion = "1.8.0"

        const val junitVersion="4.13.2"
        const val mockitoVersion="5.12.0"
        const val mockitoKotlinVersion="5.4.0"
        const val robolectricVersion = "4.14.1"
        const val supportTestRunnerVersion="1.6.2"
        const val testRulesVersion="1.6.1"
        const val archLifecycleVersion = "2.2.0"
        const val androidXTestCoreVersion = "1.5.0"
        const val uiAutomatorVersion = "2.2.0"
        const val espressoVersion = "3.5.1"
        const val mockKVersion = "1.14.5"
        const val junitExtVersion = "1.1.5"
    }
}
