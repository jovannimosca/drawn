plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.example.drawn"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.drawn"
        minSdk = 26
        targetSdk = 36
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
    buildFeatures {
        buildConfig = true
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM (platform import manages all Compose versions)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material.icons.core)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Navigation Compose 3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    // Hilt Navigation Compose
    implementation(libs.hilt.navigation.compose)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Testing
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.room.testing)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}

// Enable JUnit 5 for unit tests
tasks.withType<Test> {
    useJUnitPlatform()
}

ksp {
    arg("room.generateKotlin", "true")
    arg("room.schemaLocation", "$projectDir/schemas")
}

// KSP registers generated sources via kotlin.sourceSets which AGP 9.0 built-in Kotlin doesn't allow.
// Workaround: register KSP output dirs via android.sourceSets instead.
android.sourceSets {
    getByName("main") {
        java.srcDirs("build/generated/ksp/main/kotlin")
    }
    getByName("debug") {
        java.srcDirs("build/generated/ksp/debug/kotlin", "build/generated/ksp/debug/java")
    }
    getByName("release") {
        java.srcDirs("build/generated/ksp/release/kotlin", "build/generated/ksp/release/java")
    }
}

// Ktlint configuration
ktlint {
    android = true
    outputToConsole = true
    ignoreFailures = false
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

// Detekt configuration
detekt {
    config.setFrom(files("$rootDir/detekt.yml"))
    buildUponDefaultConfig = true
    baseline = file("$rootDir/detekt-baseline.xml")
    autoCorrect = false
}

// Kover coverage configuration — 80% minimum threshold (D-69)
kover {
    reports {
        filters {
            excludes {
                classes("*_Factory", "*_HiltModules*", "*_Impl", "*_MembersInjector")
                classes("*Hilt_*", "dagger.hilt.*")
                classes("*.ComposableSingletons*")
                classes("com.example.drawn.ui.*Screen*", "com.example.drawn.ui.*Step*")
                classes("com.example.drawn.ui.*BottomSheet*", "com.example.drawn.ui.*Indicator*")
                classes("com.example.drawn.ui.*Slot*", "com.example.drawn.ui.*Thumbnail*")
                classes("com.example.drawn.ui.*Picker*")
                classes("com.example.drawn.di.*")
                classes("com.example.drawn.MainActivity")
                classes("com.example.drawn.DrawnApplication")
                classes("com.example.drawn.ui.navigation.*")
                classes("com.example.drawn.ui.theme.*")
            }
        }
        verify {
            rule {
                minBound(80)
            }
        }
    }
}
