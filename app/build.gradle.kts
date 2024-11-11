plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.moviemate2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.moviemate2"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
        viewBinding =true
    }
}


repositories{
    google()
    mavenCentral()
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.google.android.gms:play-services-auth:20.5.0")
    implementation("com.razorpay:checkout:1.6.40")
    implementation(libs.androidx.activity)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
}




