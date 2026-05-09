plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val desktopPackageVersion = providers.gradleProperty("NOVEO_DESKTOP_PACKAGE_VERSION").orNull ?: "1.0.0"

dependencies {
    implementation(project(":core:ui"))
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20240303")
}

compose.desktop {
    application {
        mainClass = "ir.hienob.noveo.desktop.DesktopMainKt"
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "Noveo"
            packageVersion = desktopPackageVersion
            linux { iconFile.set(project.file("src/main/resources/icon.png")) }
            windows { iconFile.set(project.file("src/main/resources/icon.png")) }
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
    }
}

kotlin {
    jvmToolchain(17)
}
