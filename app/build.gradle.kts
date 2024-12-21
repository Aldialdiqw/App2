plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.app2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app2"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.cronet.embedded)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gridlayout)
    implementation(libs.androidx.coordinatorlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.androidx.constraintlayout.v214)
    implementation (libs.androidx.recyclerview)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.appcompat.v161)
    implementation(libs.material.v190)
    implementation(libs.androidx.sqlite)
    implementation(libs.android.mail)
    implementation(libs.android.activation)
}