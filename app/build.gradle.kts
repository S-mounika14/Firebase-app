plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myfirebaseapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myfirebaseapp"
        minSdk = 24
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")


    //implementation ("com.google.firebase:firebase-database:21.0.0")

    implementation (platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation ("com.google.firebase:firebase-auth:23.2.1")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.google.firebase:firebase-firestore:24.11.0")


    //implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-database")

    // Google Sign-In
    implementation ("com.google.android.gms:play-services-auth:21.3.0")





    // Testing libraries
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test:core:1.6.1")
    androidTestImplementation ("androidx.test.ext:junit:1.2.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.6.1")


}