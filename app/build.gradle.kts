plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
}

android {
    namespace = "com.huanyu.wuthelper"
    compileSdk = 34
    sourceSets{
        getByName("main"){
            jniLibs.srcDirs("libs")
        }
    }
    buildFeatures {
        viewBinding = true
    }
    defaultConfig {
        applicationId = "com.huanyu.wuthelper"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf("room.schemaLocation" to "$projectDir/schemas")
                )
            }
        }
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(files("libs\\AMap3DMap_10.0.700_AMapNavi_10.0.700_AMapSearch_9.7.2_AMapLocation_6.4.5_20240508.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
    // OkHttp
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    // Kotlin Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation ("com.google.code.gson:gson:2.8.6")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation ("org.jsoup:jsoup:1.16.1")
    implementation("com.alibaba:fastjson:2.0.28")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    //Google推荐的EasyPermission库
    implementation("pub.devrel:easypermissions:3.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0") // ViewModel扩展
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.0") // LiveData扩展
    implementation("androidx.room:room-runtime:2.5.0")
    implementation("androidx.room:room-ktx:2.5.0") // Room KTX for coroutine support
    kapt("androidx.room:room-compiler:2.5.0")
    implementation ("org.bouncycastle:bcprov-jdk15on:1.68")
    implementation ("io.noties.markwon:core:4.6.2")
    implementation ("io.noties.markwon:image:4.6.2") // 支持图片解析
    implementation ("io.noties.markwon:image-glide:4.6.2")
    implementation ("io.noties.markwon:html:4.6.2")
    implementation ("androidx.work:work-runtime-ktx:2.7.1")
}