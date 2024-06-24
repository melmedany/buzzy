
dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api:+")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:+")
    compileOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:+")
    compileOnly("org.apache.commons:commons-lang3:+")
    compileOnly("org.springframework:spring-web:+")
    compileOnly("org.springframework.security:spring-security-core:+")

}

tasks.bootJar {
    enabled = false
}



