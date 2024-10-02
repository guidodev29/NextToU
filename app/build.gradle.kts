plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.api.contigo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.api.contigo"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // Jetpack Compose core libraries (actualizadas)
    implementation("androidx.compose.ui:ui:1.7.2")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.2")

    // Navigation for Compose (actualizada)
    implementation("androidx.navigation:navigation-compose:2.8.1")

    // Optional: Compose tooling for previews and debugging (actualizadas)
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.2")

    // Play Services for Maps (actualizada)
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // Play Services for Location (actualizada)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Accompanist Permissions (actualizada)
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // Coil para Compose (para cargar imágenes)
    implementation("io.coil-kt:coil-compose:2.1.0")

    // Otros
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
