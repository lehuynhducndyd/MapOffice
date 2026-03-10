@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    id("org.jetbrains.kotlinx.kover")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.material.icons.core)
            implementation(libs.navigation.compose)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.datetime)

            implementation(libs.compose.colorpicker)

            implementation("io.github.vinceglb:filekit-dialogs:0.12.0")
            implementation("io.github.vinceglb:filekit-dialogs-compose:0.12.0")
            implementation("io.github.oneuiproject:icons:1.1.0")
            implementation("dev.chrisbanes.material3:material3-window-size-class-multiplatform:0.5.0")
            implementation("io.github.windedge.table:table-m3:0.2.3")

            implementation("io.github.vinceglb:filekit-core:0.13.0")
            implementation("io.github.vinceglb:filekit-dialogs:0.13.0")
            implementation("io.github.vinceglb:filekit-dialogs-compose:0.13.0")
            implementation("io.github.vinceglb:filekit-coil:0.13.0")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.compose.ui:ui-test:1.10.0")
            implementation("com.willowtreeapps.assertk:assertk:0.28.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
            implementation("app.cash.turbine:turbine:1.2.1")
        }
    }
}
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
android {
    namespace = "com.example.mapoffice"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.mapoffice"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    signingConfigs {
        create("release") {
            // Lấy đường dẫn từ local.properties (nếu code trên máy) hoặc từ biến môi trường (nếu chạy trên GitHub Actions)
            val keystorePath =
                localProperties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")

            // Kiểm tra an toàn (Null safety)
            if (keystorePath != null && file(keystorePath).exists()) {
                storeFile = file(keystorePath)
                storePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
                    ?: System.getenv("KEYSTORE_PASSWORD")
                keyAlias = localProperties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
                keyPassword =
                    localProperties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
            } else {
                // Báo log cho CI/CD biết là đang build bản unsigned
                println("⚠️ Khong tim thay Keystore. Se build ban Unsigned APK.")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true // Bật Proguard/R8
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}


dependencies {
    debugImplementation(libs.compose.uiTooling)
}

kover {
    reports {
        filters {
            excludes {
                // 1. Bỏ qua tất cả các hàm/class có gắn tag @Composable
                annotatedBy("*Composable")

                // 2. Bỏ qua toàn bộ thư mục chứa giao diện (Thay đổi package theo project của bạn)
                packages("com.example.mapoffice.ui_component")
                packages("com.example.mapoffice.theme")
                packages("mapoffice.composeapp.generated.resources")

                // 3. Bỏ qua các file đặc thù của Android/iOS (vì logic chủ yếu nằm ở commonMain)
                classes(
                    "*Activity",
                    "*Fragment",
                    "*Application",
                    "*ViewController*"
                )
            }
        }
    }
}
