
dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api:+")
    compileOnly("jakarta.validation:jakarta.validation-api:+")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:+")
    compileOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:+")
    compileOnly("org.apache.commons:commons-lang3:+")
    compileOnly("org.springframework:spring-webmvc:+")
    compileOnly("org.springframework.kafka:spring-kafka:+")
    compileOnly("org.springframework.security:spring-security-core:+")
    compileOnly("org.springframework.boot:spring-boot-starter-oauth2-resource-server:+")
    compileOnly("org.slf4j:slf4j-api:+")
}

tasks.bootJar {
    enabled = false
}



