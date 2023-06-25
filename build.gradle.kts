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

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("io.github.bonigarcia:webdrivermanager:5.0.3")
    implementation("org.seleniumhq.selenium:selenium-java:3.141.59")
    implementation("org.seleniumhq.selenium:selenium-api:3.141.59")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("net.lightbody.bmp:browsermob-core:2.1.5")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
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
