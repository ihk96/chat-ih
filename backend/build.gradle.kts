plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "3.5.8"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.2.21"
}

group = "com.inhyuk"
version = "0.0.1-SNAPSHOT"
description = "chat"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // kotest
    testImplementation("io.kotest:kotest-runner-junit5:4.4.3")
    testImplementation("io.kotest:kotest-assertions-core:4.4.3")
//    testImplementation("io.kotest.extensions:kotest-extensions-spring:6.0.7")
    testImplementation("io.kotest:kotest-extensions-spring:4.4.3")

    // mockk
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.ninja-squad:springmockk:4.0.2")

    // langchain
    implementation("dev.langchain4j:langchain4j-open-ai:1.8.0")
    implementation("dev.langchain4j:langchain4j-google-ai-gemini:1.8.0")
    implementation("dev.langchain4j:langchain4j-anthropic:1.8.0")
    implementation("dev.langchain4j:langchain4j-kotlin:1.8.0-beta15")
    implementation("dev.langchain4j:langchain4j-mcp:1.8.0-beta15")
    implementation("dev.langchain4j:langchain4j-http-client-jdk:1.8.0")
    // langchain4j document parsers
    implementation("dev.langchain4j:langchain4j-document-parser-apache-pdfbox:1.8.0-beta15")
    implementation("dev.langchain4j:langchain4j-document-parser-apache-tika:1.8.0-beta15")  // DOCX, XLSX 등
    implementation("dev.langchain4j:langchain4j-document-parser-apache-poi:1.8.0-beta15")  // DOCX, XLSX 등

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        javaParameters = true
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
