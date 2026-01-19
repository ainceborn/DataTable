
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.LibraryBuildType
import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.api.dsl.TestOptions
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension


fun BaseAppModuleExtension.defineApp(
    namespace: String,
    environmentParams: EnvironmentParams = defaultEnvironmentParams(),
    isComposeEnable:Boolean = false,
    defaultConfig : ApplicationDefaultConfig.() ->Unit = {},
    debug: ApplicationBuildType.() ->Unit = {},
    release: ApplicationBuildType.() ->Unit = {},
    buildFeatures: BuildFeatures.() -> Unit = {},
){

    this.namespace = namespace
    this.compileSdk = environmentParams.compileSdkVersion
    this.buildToolsVersion = buildToolsVersion

    defaultConfig {
        appInfo(
            applicationId = namespace,
            minSDKVersion =  environmentParams.minSDKVersion,
            targetSDKVersion =  environmentParams.targetSDKVersion,
            versionName =  environmentParams.versionName,
            versionCode =  environmentParams.versionCode
        )
        defaultConfig.invoke(this)
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            isTestCoverageEnabled = true
            proguardFiles(getDefaultProguardFile(PROGUARD_ANDROID_OPTIMIZE_FILE), PROGUARD_RULES_FILE)
            debug.invoke(this)
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile(PROGUARD_ANDROID_OPTIMIZE_FILE), PROGUARD_RULES_FILE)
            release.invoke(this)
        }
    }

    projectCompileOptions(version = environmentParams.javaVersion)

    buildFeatures{
        buildFeatures.invoke(this)
        if(isComposeEnable){
            compose = true
        }
    }

    testOptions {
        defaultTestOptions()
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE",
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE-notice",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE",
                "META-INF/DEPENDENCIES"
            )
        }
    }

}

fun LibraryExtension.defineLib(
    namespace: String,
    environmentParams: EnvironmentParams = defaultEnvironmentParams(),
    isComposeEnable:Boolean = false,
    defaultConfig : LibraryDefaultConfig.() ->Unit = {},
    debug: LibraryBuildType.() ->Unit = {},
    release: LibraryBuildType.() ->Unit = {},
    buildFeatures: BuildFeatures.() -> Unit = {
        if(isComposeEnable){
            compose = true
        }
    },
){
    this.namespace = namespace
    this.compileSdk = environmentParams.compileSdkVersion
    this.buildToolsVersion = buildToolsVersion

    defaultConfig {
        projectInfo(
            minSDKVersion = environmentParams.minSDKVersion,
            targetSDKVersion = environmentParams.targetSDKVersion
        )
        defaultConfig.invoke(this)
    }

    buildTypes {

        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            isTestCoverageEnabled = true
            proguardFiles(getDefaultProguardFile(PROGUARD_ANDROID_OPTIMIZE_FILE), PROGUARD_RULES_FILE)
            debug.invoke(this)
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile(PROGUARD_ANDROID_OPTIMIZE_FILE), PROGUARD_RULES_FILE)
            release.invoke(this)
        }
    }

    projectCompileOptions(version = environmentParams.javaVersion)

    buildFeatures{
        buildFeatures.invoke(this)
        if(isComposeEnable){
            compose = true
        }
    }

    testOptions {
        defaultTestOptions()
    }

    lint {
        abortOnError = false
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE",
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE-notice",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE",
                "META-INF/DEPENDENCIES"
            )
        }
    }
}

fun ApplicationDefaultConfig.appInfo(
    applicationId:String,
    minSDKVersion:Int,
    targetSDKVersion:Int,
    versionName:String,
    versionCode:Int
){
    projectInfo(
        minSDKVersion = minSDKVersion,
        targetSDKVersion = targetSDKVersion
    )
    this.applicationId = applicationId
    this.versionCode = versionCode
    this.versionName = versionName
}

fun ApplicationDefaultConfig.projectInfo(
    minSDKVersion:Int,
    targetSDKVersion:Int
){
    targetSdk =targetSDKVersion
    projectInfo(minSDKVersion = minSDKVersion)
}

fun LibraryDefaultConfig.projectInfo(
    minSDKVersion:Int,
    targetSDKVersion:Int
){
    targetSdk =targetSDKVersion
    projectInfo(minSDKVersion = minSDKVersion)
}

fun DefaultConfig.projectInfo(minSDKVersion:Int){
    minSdk  = minSDKVersion
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}

fun BaseExtension.projectCompileOptions(version:JavaVersion){
    compileOptions(version = version)

    kotlinOptions {
        jvmTarget = version.toString()

        freeCompilerArgs = freeCompilerArgs + arrayOf(
            *allowDelicateCoroutinesApi(),
            *allowExperimentalStdLib(),
            *allowContextReceivers()
        )
    }
}


fun BaseExtension.compileOptions(version:JavaVersion) {
    compileOptions {
        sourceCompatibility = version
        targetCompatibility = version
    }
}

private fun TestOptions.defaultTestOptions(){
    animationsDisabled = true
    unitTests {
        isIncludeAndroidResources = true
        unitTests.isReturnDefaultValues = true
    }
    unitTests.all {
        it.extensions.configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
}

data class EnvironmentParams(
    val minSDKVersion:Int,
    val compileSdkVersion: Int,
    val targetSDKVersion: Int,
    val versionName:String,
    val versionCode:Int,
    val buildToolsVersion: String,
    val javaVersion: JavaVersion,
)

fun defaultEnvironmentParams(
    minSDKVersion:Int = Metadata.minSDK,
    compileSdkVersion: Int = Metadata.compileSdk,
    targetSDKVersion: Int = Metadata.targetSdk,
    versionName:String = Metadata.versionName,
    versionCode:Int = Metadata.versionCode,
    buildToolsVersion: String = Metadata.buildToolsVersion,
    javaVersion: JavaVersion = Metadata.Versions.javaVersion
) = EnvironmentParams(
    minSDKVersion = minSDKVersion,
    compileSdkVersion = compileSdkVersion,
    targetSDKVersion = targetSDKVersion,
    versionName = versionName,
    versionCode = versionCode,
    buildToolsVersion = buildToolsVersion,
    javaVersion = javaVersion
)
