plugins {
    id("com.android.application")
}

android {
    namespace = "au.com.emjeek.heychatgpt"
    compileSdk = 35

    defaultConfig {
        applicationId = "au.com.emjeek.heychatgpt"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
