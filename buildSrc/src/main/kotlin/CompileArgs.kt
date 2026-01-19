import java.io.File

fun generateComposeReports(dir: File) = arrayOf("-P", "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${dir.absolutePath}")
fun allowDelicateCoroutinesApi() = arrayOf("-opt-in=kotlinx.coroutines.DelicateCoroutinesApi")
fun allowExperimentalStdLib() = arrayOf("-opt-in=kotlin.ExperimentalStdlibApi")
fun allowContextReceivers() = arrayOf("-Xcontext-receivers")