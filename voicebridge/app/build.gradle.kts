plugins {
    id("com.android.application")
}

android {
    namespace = "com.emjeek.chatgptvoicebridge"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.emjeek.chatgptvoicebridge"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "3.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
