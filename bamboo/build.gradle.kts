
/**
 *作为 application 运行时需要配置 plugin application applicationId versionCode versionName isDebuggable
 *作为 library 运行时仅需配置 minSdk targetSdk
 *  */

plugins {
//    id("com.android.application")
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    `maven-publish`
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {


//        applicationId = "com.yuu.android.component.bamboo"
//        versionCode = 1
//        versionName = "1.0.0"

        minSdk = 21
        targetSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildTypes {
        getByName("release") {
//            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
//            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies{
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")
    //链接[https://github.com/eclipse/paho.mqtt.android]
    api("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0"){
        isTransitive = true
    }
    api("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1"){
        isTransitive = true
    }
    //协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")


}

afterEvaluate {
    publishing{
        publications {
           create<MavenPublication>("release"){
               group = "com.yuu.android.component"
               artifactId = "Bamboo"
               version = "0.0.4"

               afterEvaluate { artifact(tasks.getByName("bundleReleaseAar")) }
           }
        }
    }
}