
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:+")
    implementation("org.springframework.boot:spring-boot-starter-validation:+")
    implementation("org.passay:passay:+")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:+")

    implementation("org.springframework.kafka:spring-kafka:+")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:+")

    implementation("org.springframework.boot:spring-boot-starter-security:+")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server:+")

    implementation("org.springframework.boot:spring-boot-starter-data-redis:+")
    implementation("redis.clients:jedis:+")

    implementation(project(":buzzy-common"))
    implementation("org.mapstruct:mapstruct:+")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:+")
    annotationProcessor("org.mapstruct:mapstruct-processor:+")

    runtimeOnly("org.postgresql:postgresql:+")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:+")

    testImplementation("org.springframework.boot:spring-boot-starter-test:+")
    testImplementation("org.springframework.security:spring-security-test:+")
    testImplementation("org.testcontainers:testcontainers:+")
    testImplementation("org.testcontainers:junit-jupiter:+")
    testImplementation("org.testcontainers:postgresql:+")
    testImplementation("com.redis.testcontainers:testcontainers-redis:+")
    testImplementation("org.apache.commons:commons-compress:+")
}


tasks.bootJar {
    enabled = true
}



