package dependencies

@Suppress("unused")
object Dep {
  object GradlePlugin {
    val android = "com.android.tools.build:gradle:3.5.1"
  }

  object Dagger {
    //val version = "2.24"
    val version = "2.13"
    val core = "com.google.dagger:dagger:$version"
    val compiler = "com.google.dagger:dagger-compiler:$version"
    val androidSupport = "com.google.dagger:dagger-android-support:$version"
    val android = "com.google.dagger:dagger-android:$version"
    val androidProcessor = "com.google.dagger:dagger-android-processor:$version"
    val assistedInjectAnnotations = "com.squareup.inject:assisted-inject-annotations-dagger2:0.5.0"
    val assistedInjectProcessor = "com.squareup.inject:assisted-inject-processor-dagger2:0.5.0"
  }
}