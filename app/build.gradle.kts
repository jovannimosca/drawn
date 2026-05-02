plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    alias(libs.plugins.robolectric.junit5.gradle.plugin)
}

android {
    namespace = "com.example.drawn"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.drawn"
        minSdk = 26
        targetSdk = 36
        versionCode = project.property("versionCode").toString().toInt()
        versionName = project.property("version").toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("RELEASE_KEYSTORE_PATH")
            val keystorePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            val keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            val keyPassword = System.getenv("RELEASE_KEY_PASSWORD")

            if (
                !keystorePath.isNullOrEmpty() &&
                !keystorePassword.isNullOrEmpty() &&
                !keyAlias.isNullOrEmpty() &&
                !keyPassword.isNullOrEmpty()
            ) {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            val releaseSigning = signingConfigs.findByName("release")
            signingConfig = if (releaseSigning?.storeFile?.exists() == true) releaseSigning else signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
        jniLibs {
            useLegacyPackaging = false
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
    testImplementation(libs.junit) // JUnit4 for @Rule annotation (Compose UI tests)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.robolectric.junit5)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.room.testing)
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.room.testing)
}

// Enable JUnit 5 for unit tests
tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}

ksp {
    arg("room.generateKotlin", "true")
    arg("room.schemaLocation", "$projectDir/schemas")
}

// KSP registers generated sources via kotlin.sourceSets which AGP 9.0 built-in Kotlin doesn't allow.
// Workaround: register KSP output dirs via android.sourceSets instead.
android.sourceSets {
    getByName("main") {
        java.directories.add("build/generated/ksp/main/kotlin")
    }
    getByName("debug") {
        java.directories.add("build/generated/ksp/debug/kotlin")
        java.directories.add("build/generated/ksp/debug/java")
    }
    getByName("release") {
        java.directories.add("build/generated/ksp/release/kotlin")
        java.directories.add("build/generated/ksp/release/java")
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
//
// Coverage scope: testable business logic (repositories, ViewModels)
// Excluded: infrastructure, generated code, data classes
//
// The following are EXCLUDED from coverage calculation:
// - UI layer (requires Android instrumentation testing)
// - DI/Hilt modules (generated code, not testable)
// - Domain models (data classes with no logic)
// - Database infrastructure (DAOs, entities, converters, migrations)
// - Android framework (Activity, Application)
kover {
    reports {
        filters {
            excludes {
                // Generated code
                classes("*_Factory", "*_HiltModules*", "*_Impl", "*_MembersInjector")
                classes("*Hilt_*", "dagger.hilt.*")
                classes("*.ComposableSingletons*")
                classes("hilt_aggregated_deps.*")
                // Compiler-generated synthetic lambda classes (e.g., CardRepository$observeAllCards$$inlined$map$1)
                // These are NOT real methods - Kotlin compiler creates them for inline Flow transformations
                // They cannot be tested directly and are excluded to get accurate coverage
                classes("com.example.drawn.data.repository.*\$*")
                // UI - requires Android instrumentation
                classes("com.example.drawn.ui.*")
                // DI
                classes("com.example.drawn.di.*")
                // Android framework (auto-generated by compiler, not testable by unit tests)
                classes("com.example.drawn.MainActivity")
                classes("com.example.drawn.MainActivityKt")
                classes("com.example.drawn.BuildConfig")
                classes("com.example.drawn.DrawnApplication")
                // Infrastructure
                classes("com.example.drawn.data.database.*")
                classes("com.example.drawn.domain.model.*")
            }
        }
        verify {
            rule {
                minBound(80)
            }
        }
    }
}
