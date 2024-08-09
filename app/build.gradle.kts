plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.rodz.vault"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rodz.vault"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.glide)
    implementation("commons-io:commons-io:2.16.1")
    implementation("com.facebook.fresco:fresco:2.5.0")
    implementation("com.facebook.fresco:animated-gif:2.5.0")
    implementation("com.facebook.fresco:animated-webp:2.6.0")
    implementation("com.facebook.fresco:webpsupport:2.5.0")

    implementation ("com.squareup.picasso:picasso:2.71828")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}