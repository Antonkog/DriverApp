package dependencies

private object Versions {
    val androidCompileSdkVersion = 29
    val androidMinSdkVersion = 23

    private val versionMajor = 1
    private val versionMinor = 0
    private val versionPatch = 0
    private val versionOffset = 0
    val androidVersionCode = (versionMajor * 10000 + versionMinor * 100 + versionPatch) * 100 + versionOffset

    val androidVersionName = "$versionMajor.$versionMinor.$versionPatch"
}