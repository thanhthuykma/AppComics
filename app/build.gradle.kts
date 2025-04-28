plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.appcomics"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appcomics"
        minSdk = 30
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
    buildFeatures {
        viewBinding = true;
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0") // Adapter cho RxJava2
    implementation("io.reactivex.rxjava2:rxjava:2.2.21") // RxJava2 core
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1") // RxJava Android support
    //zoom ảnh
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    //Spinkitview
    implementation("com.github.ybq:Android-SpinKit:1.4.0")

    //google
    //implementation("com.google.android.gms:play-services-auth:20.9.0")
    //implementation("com.google.android.gms:play-services-auth-api-phone:18.0.1")


    //Picasso để tải,hiển thị ảnh từ URL từ mạng và xử lý bộ nhớ cache,kích thuowsc hìnhanrh
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    //dot indicator
    implementation("com.tbuonomo:dotsindicator:4.3")

    //Banner
    //implementation ("com.github.denzcoskun:ImageSlideshow:0.1.0")
    //Spots dialog : vòng chấm tròn di chuyển
    //implementation("com.github.d-max:spots-dialog:1.1@aar")

    //google sign-in
    implementation("com.google.android.gms:play-services-auth:20.7.0") // Thêm Google Sign-In SDK
    implementation ("com.google.firebase:firebase-auth:21.1.0")
    //lưu ảnh
    implementation ("com.google.firebase:firebase-storage:20.2.1")

    implementation ("androidx.biometric:biometric:1.2.0-alpha05")
    //captcha
    implementation ("com.google.android.gms:play-services-safetynet:17.0.0")
    //jwt
    implementation ("com.auth0:java-jwt:3.19.2")
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")



    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}