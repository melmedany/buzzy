
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:+")
    implementation("org.springframework.boot:spring-boot-starter-validation:+")
    implementation("org.passay:passay:+")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:+")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:+")

    implementation("io.jsonwebtoken:jjwt:+")
    implementation("org.springframework.boot:spring-boot-starter-security:+")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server:+")

    implementation(project(":buzzy-common"))

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:+")

    runtimeOnly("org.postgresql:postgresql:+")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:+")

    testImplementation("org.springframework.boot:spring-boot-starter-test:+")
    testImplementation("org.testcontainers:testcontainers:+")
    testImplementation("org.testcontainers:junit-jupiter:+")
    testImplementation("org.testcontainers:postgresql:+")
    testImplementation("org.apache.commons:commons-compress:+")
}


tasks.bootJar {
    enabled = true
}



