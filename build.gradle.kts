import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    kotlin("plugin.jpa") version "2.2.21"

    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("dev.detekt") version "2.0.0-alpha.1"
    id("jacoco")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "CRM"

java {
    toolchain {
        // project Kotlin/JVM target and compile release are set to 21
        // use JDK 21 to match the environment and avoid auto-provisioning toolchains
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

configurations.matching { it.name == "detekt" }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("2.2.20")
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    implementation("io.swagger.core.v3:swagger-models:2.2.17")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("com.google.api-client:google-api-client:2.8.1")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    runtimeOnly("org.postgresql:postgresql")

    implementation("io.mcarle:konvert-api:3.2.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.14.9")
    testImplementation("com.ninja-squad:springmockk:5.0.0")
    testImplementation("io.rest-assured:rest-assured:5.5.6")
    testImplementation("io.rest-assured:kotlin-extensions:5.5.6")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xannotation-default-target=param-property"
        )
    }
}

tasks.withType<JavaCompile> {
    options.release.set(21)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    maxParallelForks = maxOf(1, Runtime.getRuntime().availableProcessors() / 2)
}

tasks.named<Test>("test") {
    description = "Runs fast local checks (unit + architecture), excludes integration tests"
    useJUnitPlatform {
        excludeTags("integration")
    }
}

val integrationTest by tasks.registering(Test::class) {
    description = "Runs integration tests only"
    group = "verification"
    testClassesDirs = tasks.named<Test>("test").get().testClassesDirs
    classpath = tasks.named<Test>("test").get().classpath
    shouldRunAfter(tasks.named("test"))
    useJUnitPlatform {
        includeTags("integration")
    }
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}

tasks.named("check") {
    dependsOn(integrationTest)
}

tasks.matching { task -> task.name in setOf("detekt", "detektMain", "detektTest") }.configureEach {
    if (this is org.gradle.api.tasks.SourceTask) {
        if (name == "detekt") {
            setSource(files("src/main/kotlin", "src/test/kotlin"))
        }
        include("**/*.kt", "**/*.kts")
        exclude("**/build/**", "**/generated/**")
    }
}

tasks.register("detektFast") {
    group = "verification"
    description = "Runs a faster Detekt for local feedback"
    dependsOn("detektMain")
}

tasks.register("lint") {
    group = "verification"
    description = "Runs lint checks via full detekt"
    dependsOn("detekt")
}
