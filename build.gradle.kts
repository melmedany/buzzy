plugins {
    java
    id("org.springframework.boot") version "latest.release"
    id("io.spring.dependency-management") version "latest.release"
}

allprojects {
    group = "io.buzzy"
    version = "0.0.1"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    tasks.test {
        useJUnitPlatform()
        reports {
            html.required = false
        }
    }
}

tasks.jar {
    enabled = false
}

tasks.bootJar {
    enabled = false
}