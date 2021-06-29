import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.1"
    id("com.google.cloud.tools.jib") version "3.1.1"
    id("jacoco")
    id("org.sonarqube") version "3.1.1"
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.spring") version "1.5.0"
    kotlin("plugin.jpa") version "1.5.0"

}

apply(plugin = "io.spring.dependency-management")

jacoco {
    toolVersion = "0.8.7"
}

tasks.withType(JacocoReport::class.java).all {
    reports {
        xml.isEnabled = true
        xml.destination = File("$buildDir/reports/jacoco/report.xml")
    }
}

group = "no.edgeworks"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    val mockkVersion = "1.10.6"
    val h2Version = "1.4.196"
    val flywayVersion = "7.8.2"
    val azureKeyvaultVersion = "3.6.0"

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.azure.spring:azure-spring-boot-starter-keyvault-secrets:${azureKeyvaultVersion}")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.flywaydb:flyway-core:${flywayVersion}")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2:${h2Version}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.flywaydb:flyway-core:${flywayVersion}")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testRuntimeOnly("com.h2database:h2:${h2Version}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    to {
        image = "ewtestcontainerregistry.azurecr.io/${rootProject.name}:${version}"
    }
}
