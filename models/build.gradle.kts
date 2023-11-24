import com.android.build.gradle.internal.tasks.factory.dependsOn
import java.util.UUID

plugins {
    id("com.android.library") apply true
}

android {
    namespace = "org.vosk.models"
    compileSdk = 34

    defaultConfig {
        minSdk = 34
        //targetSdk = 34
    }
    buildFeatures {
        buildConfig = false
    }
    sourceSets {
        getByName("main").assets.srcDirs("$buildDir/generated/assets")
    }
}

tasks.register("genUUID") {
    fun uuid() : String = UUID.randomUUID().toString()
    fun odir() : File = file("$buildDir/generated/assets/model-en-us")
    fun ofile() : File = file("${odir()}/uuid")

    doLast {
        mkdir(odir())
        ofile().writeText(uuid())
    }
}

tasks.preBuild.dependsOn("genUUID")
