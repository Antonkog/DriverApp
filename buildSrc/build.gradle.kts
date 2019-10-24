buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.google.apis:google-api-services-androidpublisher:v3-rev103-1.25.0")
        classpath("com.google.api-client:google-api-client:1.30.2")
    }
}

plugins {
    `kotlin-dsl`
}
repositories {
    jcenter()
}

dependencies {
    implementation("com.google.guava:guava:28.1-jre")
    implementation("com.google.apis:google-api-services-androidpublisher:v3-rev103-1.25.0") {
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation("com.google.api-client:google-api-client:1.30.2") {
        exclude(group = "com.google.guava", module = "guava")
    }
}