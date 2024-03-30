import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "ru.dmitriyt"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("androidx.compose.material:material-icons-extended:1.6.4")

    implementation(project(":logger"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.3")

    implementation(platform("io.insert-koin:koin-bom:3.5.3"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-compose")

    val coilVersion = "3.0.0-alpha06"
    implementation("io.coil-kt.coil3:coil-core:$coilVersion")
    implementation("io.coil-kt.coil3:coil-compose:$coilVersion")

    val imageLoaderVersion = "1.7.8"
    implementation("io.github.qdsfdhvh:image-loader:$imageLoaderVersion")
    implementation("io.github.qdsfdhvh:image-loader-extension-imageio:$imageLoaderVersion")

    val voyagerVersion = "1.0.0"
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
    implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")
    implementation("cafe.adriel.voyager:voyager-koin:$voyagerVersion")

    implementation("androidx.datastore:datastore-preferences-core:1.0.0")
    implementation("com.darkrockstudios:mpfilepicker:3.1.0")
    implementation("com.drewnoakes:metadata-extractor:2.18.0")
}

compose.desktop {
    application {
        mainClass = "ru.dmitriyt.gallery.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "gallery2"
            packageVersion = "1.0.0"
        }
    }
}
