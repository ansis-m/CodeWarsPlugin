plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.3.1")
    type.set("IU") // Target IDE Platform
    //version.set("2022.1.4")
    //type.set("IC") // Target IDE Platform //need to reset also jdk


    //plugins.set(listOf("PythonCore:221.6008.17")) //for development only, to be removed in production - for the free intellij
    plugins.set(listOf("Pythonid:223.8214.52", "org.jetbrains.plugins.go:223.8214.52", "org.jetbrains.plugins.ruby:223.8214.52", "com.jetbrains.php:223.8214.64")) //for development only, to be removed in production - for ultimate
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.compilerArgs.add("-Xlint:unchecked")
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
