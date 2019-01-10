plugins {
    java
    jacoco
    id("org.springframework.boot") version "2.1.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    id("org.sonarqube") version "2.6.2"
}

repositories {
    jcenter()
}

rootProject.version = rootProject.file("version.txt").readText().trim()
group = "cz.chalupa.zonky"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

sonarqube {
    properties {
        property("sonar.coverage.exclusions", "**/*Application.java")
    }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    annotationProcessor(group = "org.projectlombok", name = "lombok")
    compileOnly(group = "org.projectlombok", name = "lombok")

    implementation(group = "org.springframework.boot", name = "spring-boot-starter")

    testAnnotationProcessor(group = "org.projectlombok", name = "lombok")
    testCompileOnly(group = "org.projectlombok", name = "lombok")

    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params")
    testImplementation(group = "org.mockito", name = "mockito-core")
    testImplementation(group = "org.mockito", name = "mockito-junit-jupiter")
    testImplementation(group = "org.assertj", name = "assertj-core")

    testRuntimeOnly(group = "org.junit.vintage", name = "junit-vintage-engine")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
    }
}

tasks.withType<JacocoReport> {
    reports {
        html.isEnabled = true
        xml.isEnabled = true
        csv.isEnabled = true
    }
}
