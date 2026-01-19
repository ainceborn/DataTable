import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion

const val PROGUARD_ANDROID_OPTIMIZE_FILE = "proguard-android-optimize.txt"
const val PROGUARD_RULES_FILE = "proguard-rules.pro"

fun Project.kotlin() {
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(Metadata.Versions.javaVersion.majorVersion))
        }
    }
}