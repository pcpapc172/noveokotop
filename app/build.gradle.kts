plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}


val appVersionCode = providers.gradleProperty("NOVEO_VERSION_CODE").orNull?.toIntOrNull() ?: 27
val appVersionName = providers.gradleProperty("NOVEO_VERSION_NAME").orNull ?: "0.7.5"

android {
    namespace = "ir.hienob.noveo"
    compileSdk = 35

    defaultConfig {
        applicationId = "ir.hienob.noveo"
        minSdk = 23
        targetSdk = 35
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
        resourceConfigurations += listOf("en", "fa", "ru", "zh")
    }

    signingConfigs {
        create("release") {
            // These properties can be set in local.properties or via command line -P
            val keystoreFile = providers.gradleProperty("NOVEO_KEYSTORE_FILE").orNull?.let { file(it) }
            if (keystoreFile?.exists() == true) {
                storeFile = keystoreFile
                storePassword = providers.gradleProperty("NOVEO_KEYSTORE_PASSWORD").orNull
                keyAlias = providers.gradleProperty("NOVEO_KEY_ALIAS").orNull
                keyPassword = providers.gradleProperty("NOVEO_KEY_PASSWORD").orNull
            } else {
                // Fallback to debug signature if no release keystore is provided
                // This prevents "unsigned" builds that can't be installed
                val debugKeystore = file("debug.keystore")
                if (debugKeystore.exists()) {
                    storeFile = debugKeystore
                    storePassword = "androiddebugkey"
                    keyAlias = "androiddebugkey"
                    keyPassword = "androiddebugkey"
                }
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            val releaseConfig = signingConfigs.getByName("release")
            if (releaseConfig.storeFile != null) {
                signingConfig = releaseConfig
            }
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
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
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE*",
                "META-INF/NOTICE*",
                "META-INF/*.version",
                "kotlin-tooling-metadata.json"
            )
        }
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":core:notifications"))
    implementation(project(":core:ui"))

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")
    implementation("androidx.lifecycle:lifecycle-service:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.navigation:navigation-compose:2.8.7")

    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
    implementation("io.coil-kt.coil3:coil-gif:3.0.4")
    implementation("com.airbnb.android:lottie-compose:6.4.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.livekit:livekit-android:2.18.0")
    implementation("com.github.ajalt:timberkt:1.5.1")

    implementation("androidx.media3:media3-exoplayer:1.5.0")
    implementation("androidx.media3:media3-ui:1.5.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
