import Metadata.Versions.activityComposeVersion
import Metadata.Versions.androidLifecycleVersion
import Metadata.Versions.androidXTestCoreVersion
import Metadata.Versions.androidxCoreVersion
import Metadata.Versions.appcompatVersion
import Metadata.Versions.archLifecycleVersion
import Metadata.Versions.composeBomVersion
import Metadata.Versions.constraintlayoutToolsVersion
import Metadata.Versions.espressoVersion
import Metadata.Versions.junitExtVersion
import Metadata.Versions.junitVersion
import Metadata.Versions.koinVersion
import Metadata.Versions.kotlinSerializationVersion
import Metadata.Versions.kotlinToolsVersion
import Metadata.Versions.kotlinVersion
import Metadata.Versions.materialVersion
import Metadata.Versions.mockKVersion
import Metadata.Versions.mockitoKotlinVersion
import Metadata.Versions.mockitoVersion
import Metadata.Versions.robolectricVersion
import Metadata.Versions.supportTestRunnerVersion
import Metadata.Versions.testRulesVersion
import Metadata.Versions.uiAutomatorVersion
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.androidXCore() {
    implementation("androidx.core:core-ktx:$androidxCoreVersion")
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("androidx.appcompat:appcompat-resources:$appcompatVersion")
    implementation("androidx.fragment:fragment-ktx:$appcompatVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintlayoutToolsVersion")
}

fun DependencyHandler.androidLifecycle(){
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$androidLifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$androidLifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common:$androidLifecycleVersion")
}

fun DependencyHandler.composeLibs() {
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3.adaptive:adaptive")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$androidLifecycleVersion")
}

fun DependencyHandler.apiComposeLibs() {
    api(platform("androidx.compose:compose-bom:$composeBomVersion"))
    api("androidx.compose.material3:material3")
    api("androidx.compose.ui:ui")
    api("androidx.compose.ui:ui-tooling-preview")
    api("androidx.compose.material:material-icons-core")
    api("androidx.compose.material:material-icons-extended")
    api("androidx.compose.material3.adaptive:adaptive")
    api("androidx.compose.ui:ui-tooling")
    api("androidx.activity:activity-compose:$activityComposeVersion")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:$androidLifecycleVersion")
}


fun DependencyHandler.koin() {
    // Koin DI
    implementation("io.insert-koin:koin-android:$koinVersion")
}

fun DependencyHandler.tests(){
    testImplementation ("androidx.test:core:$androidXTestCoreVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.insert-koin:koin-test-junit4:$koinVersion")

    testImplementation ("junit:junit:$junitVersion")
    testImplementation ("org.mockito:mockito-core:$mockitoVersion")
    testImplementation ("org.robolectric:robolectric:$robolectricVersion")
    testImplementation ("io.mockk:mockk:$mockKVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinToolsVersion")

    androidTestImplementation ("androidx.test:core:$androidXTestCoreVersion")
    androidTestImplementation ("androidx.test:runner:$supportTestRunnerVersion")
    androidTestImplementation ("androidx.test:rules:$testRulesVersion")
    androidTestImplementation ("androidx.test.ext:junit:$junitExtVersion")

    androidTestImplementation ("org.mockito:mockito-android:$mockitoVersion")
    androidTestImplementation ("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    androidTestImplementation ("org.mockito:mockito-core:$mockitoVersion")

    androidTestImplementation ("androidx.arch.core:core-testing:$archLifecycleVersion")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:$supportTestRunnerVersion")
    androidTestImplementation ("androidx.test.uiautomator:uiautomator:$uiAutomatorVersion")
    androidTestImplementation ("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation ("io.mockk:mockk-android:$mockKVersion")
}

fun DependencyHandler.material() {
    implementation("com.google.android.material:material:$materialVersion")
}

fun DependencyHandler.kotlinToolsApi(){
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinToolsVersion")
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinToolsVersion")
    api("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
}

