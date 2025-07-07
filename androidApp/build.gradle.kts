plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.phoneapp.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.phoneapp.android"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation(libs.firebase.messaging)
    debugImplementation(libs.compose.ui.tooling)
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")

    implementation(libs.linphone)

    implementation(libs.ktor.client.okhttp)

    implementation(libs.kotlinx.coroutines.android)
}


