@file:Suppress("UnstableApiUsage")

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.io.IOException
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

//apply plugin: "com.google.gms.google-services"
//apply plugin: "com.google.firebase.crashlytics"
//apply plugin: "com.google.firebase.firebase-perf"

val baseVersion = "1.5.5"
val verName = "${baseVersion}.${getGitHash()}"
val verCode = getGitCommitCount().toInt() + 432

val localProperties = loadLocalProperties(project)
val mStorePassword = localProperties.getProperty("storePassword")
val mKeyAlias = localProperties.getProperty("keyAlias")
val mKeyPassword = localProperties.getProperty("keyPassword")

android {
    compileSdk = 34

    sourceSets.create("google") {
        java.setSrcDirs(listOf("src/google/java", "src/main/java"))
    }

    sourceSets.create("official") {
        java.setSrcDirs(listOf("src/official/java", "src/main/java"))
    }

    defaultConfig {
        applicationId = "luyao.direct"
        minSdk = 21
        targetSdk = 34
        versionCode = verCode
        versionName = verName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a")
        }

        flavorDimensions += "default"
        productFlavors {
            create("official") {
                manifestPlaceholders["CHANNEL"] = "official"
            }
            create("google") {
                manifestPlaceholders["CHANNEL"] = "google"
            }
        }

//        javaCompileOptions {
//            annotationProcessorOptions {
//                arguments += mapOf(
//                    "room.schemaLocation" to "$projectDir/schemas",
//                    "room.incremental" to "true"
//                )
//            }
//        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../key/luyao.jks")
            storePassword = mStorePassword
            keyAlias = mKeyAlias
            keyPassword = mKeyPassword
        }
        create("release") {
            storeFile = file("../key/luyao.jks")
            storePassword = mStorePassword
            keyAlias = mKeyAlias
            keyPassword = mKeyPassword
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "_debug"
            resValue("string", "app_name", "@string/app_name_debug")
            resValue("string", "package_name", "@string/package_name_debug")
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            resValue("string", "app_name", "@string/app_name_release")
            resValue("string", "package_name", "@string/package_name_release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        viewBinding = true
//        compose true
    }

//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.4.4"
//    }
    namespace = "luyao.direct"

    applicationVariants.all {
        val variant = this
        val time = SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(Date())
        variant.outputs.map {
            it as com.android.build.gradle.internal.api.BaseVariantOutputImpl
        }.forEach {
            it.outputFileName =
                "Direct_${variant.flavorName}_${variant.versionName}_" + time + "_" + getGitBranch() + "_" + getGitHash() + ".apk"
        }
    }
}

dependencies {
    implementation(project(":luyao_view"))
    implementation(project(":luyao_ktx"))
    implementation("androidx.preference:preference-ktx:1.2.0")

    val roomVersion = "2.5.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.work:work-runtime-ktx:2.8.1")

    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

//    implementation("com.sackcentury:shinebutton:1.0.0")
    implementation("com.github.promeg:tinypinyin:2.0.3")
    "officialImplementation"("com.github.azhon:AppUpdate:3.0.6")
    implementation("androidx.browser:browser:1.5.0")
    implementation("androidx.dynamicanimation:dynamicanimation-ktx:1.0.0-alpha03")

    implementation("com.google.dagger:hilt-android:2.46.1")
    kapt("com.google.dagger:hilt-compiler:2.46.1")

//    implementation "androidx.asynclayoutinflater:asynclayoutinflater:1.0.0"

    implementation("com.drakeet.about:about:2.5.2")
    implementation("com.jaredrummler:colorpicker:1.1.0")
    implementation("com.github.thegrizzlylabs:sardine-android:0.8")
    implementation("com.github.tingyik90:snackprogressbar:6.4.2")
    implementation("com.github.princekin-f:EasyFloat:2.0.4")
//    implementation("com.airbnb.android:lottie:5.2.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.github.topjohnwu.libsu:core:5.0.3")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
    implementation("de.psdev.licensesdialog:licensesdialog:2.2.0")

//    def composeBom = platform("androidx.compose:compose-bom:2023.03.00")
//    implementation composeBom
//    androidTestImplementation composeBom
//    implementation "androidx.compose.material3:material3"
//    implementation "androidx.compose.ui:ui-tooling-preview"
//    debugImplementation "androidx.compose.ui:ui-tooling"
//    implementation "androidx.activity:activity-compose:1.7.0"
//    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1"
//    implementation "androidx.compose.runtime:runtime-livedata"
}

fun getGitBranch(): String {
    return "git symbolic-ref --short -q HEAD".runCommand()
}

fun getGitHash(): String {
    return "git rev-parse --short HEAD".runCommand()
}

fun getGitCommitCount(): String {
    return "git rev-list --count HEAD".runCommand()
}

fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String = ProcessBuilder(split("\\s(?=(?:[^'\"`]*(['\"`])[^'\"`]*\\1)*[^'\"`]*$)".toRegex()))
    .directory(workingDir)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start()
    .apply { waitFor(timeoutAmount, timeoutUnit) }
    .run {
        val error = errorStream.bufferedReader().readText().trim()
        if (error.isNotEmpty()) {
//            throw IOException(error)
        }
        inputStream.bufferedReader().readText().trim()
    }

fun loadLocalProperties(project: Project): java.util.Properties {
    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    }
    return properties
}
