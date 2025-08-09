val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kmongo_version: String by project
val commons_codec_version: String by project

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.kfouri"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-core-jvm:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-call-logging-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-auth-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-netty-jvm:${ktor_version}")
    implementation("ch.qos.logback:logback-classic:${logback_version}")
    testImplementation("io.ktor:ktor-server-tests-jvm:${ktor_version}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${kotlin_version}")


    implementation("org.litote.kmongo:kmongo:${kmongo_version}")
    implementation("org.litote.kmongo:kmongo-coroutine:${kmongo_version}")

    implementation("commons-codec:commons-codec:$commons_codec_version")

    implementation("org.jetbrains.exposed:exposed-core:0.57.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.57.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.57.0")

    implementation("com.sun.mail:jakarta.mail:2.0.1")

    implementation("org.jetbrains.exposed:exposed-java-time:0.48.0")
    implementation ("mysql:mysql-connector-java:8.0.29")

}
