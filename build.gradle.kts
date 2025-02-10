plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose.compiler)
  `maven-publish`
}

group = "dev.trindadedev"
version = libs.versions.lib.version.get()

android {
  namespace = "dev.trindadedev.scrolleffect"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
  buildToolsVersion = libs.versions.android.buildToolsVersion.get()

  defaultConfig {
    minSdk = libs.versions.android.minSdk.get().toInt()
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlin {
    jvmToolchain(17)
  }

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(libs.androidx.appcompat)

  implementation(libs.compose.foundation)
  implementation(libs.compose.foundation.layout)
  implementation(libs.compose.runtime)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
}

afterEvaluate {
  publishing {
    publications {
      register("mavenRelease", MavenPublication::class) {
        groupId = "dev.trindadedev"
        artifactId = "scrolleffect"
        version = libs.versions.lib.version.get()
        from(components["release"])
      }
    }
  }
}
