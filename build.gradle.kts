plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.unbroken-dome.xjc") version "2.0.0" // plugin do XJC
}

group = "pl.spinsoft.ksef"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    //implementation("org.springframework.boot:spring-boot-starter-scheduling")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    //implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("org.glassfish.jaxb:jaxb-xjc:4.0.5")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}
xjc {
    xjcVersion.set("3.0")
}

// Dodaj wygenerowany kod jako źródło
/*sourceSets["main"].java {
    srcDir("build/generated/sources/xjc")
}

// Upewnij się, że generowanie następuje przed kompilacją
tasks.named("compileKotlin") {
    dependsOn(tasks.named("xjcGenerate"))
}*/

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
