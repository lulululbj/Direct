@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("maven-publish")
}

//apply {
//    from("maven-publish.gradle")
//}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }


    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        viewBinding = true
    }

    kotlin {
        sourceSets.main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
        sourceSets.test {
            kotlin.srcDir("build/generated/ksp/test/kotlin")
        }
    }
    namespace = "luyao.ktx"
}

dependencies {
    // google
    api("androidx.core:core-ktx:1.12.0")
    api("androidx.appcompat:appcompat:1.6.1")
    api("com.google.android.material:material:1.10.0")
    api("androidx.navigation:navigation-fragment-ktx:2.7.5")
    api("androidx.navigation:navigation-ui-ktx:2.7.5")
    api("androidx.recyclerview:recyclerview:1.3.2")
    api("androidx.biometric:biometric-ktx:1.2.0-alpha05")
    api("androidx.webkit:webkit:1.8.0")
    api("androidx.constraintlayout:constraintlayout:2.1.4")
    api("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // third
    api ("com.tencent:mmkv:1.3.0")
    api ("com.drakeet.multitype:multitype:4.3.0")
//    api ("com.geyifeng.immersionbar:immersionbar:3.2.2")
    api ("com.guolindev.permissionx:permissionx:1.7.1")
    api("com.github.getActivity:XXPermissions:18.5")
    api ("com.squareup.retrofit2:retrofit:2.9.0")
//    api ("com.squareup.retrofit2:converter-gson:2.9.0")
    api ("com.squareup.okhttp3:logging-interceptor:4.11.0")
    api("com.squareup.moshi:moshi:1.15.0")
    api("com.squareup.retrofit2:converter-moshi:2.9.0")
    api("com.hi-dhl:binding:1.2.0")
    api("com.github.bumptech.glide:glide:4.15.1")
    api("com.afollestad.material-dialogs:core:3.3.0")
    api("com.afollestad.material-dialogs:input:3.3.0")
    api("com.github.getActivity:MultiLanguages:8.0")
    api("com.github.getActivity:Toaster:12.3")
    api("com.google.android.flexbox:flexbox:3.0.0")
    api("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    api("de.hdodenhof:circleimageview:3.1.0")
    api("com.github.thegrizzlylabs:sardine-android:0.8")
//    api("com.google.code.gson:gson:2.9.0")
//    api("de.hdodenhof:circleimageview:3.1.0")

}