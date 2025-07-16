import org.gradle.internal.declarativedsl.parsing.main

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.unbroken-dome.xjc") version "2.0.0"
  //  id("org.openapi.generator") version "7.14.0"
}

group = "pl.spinsoft.ksef"
version = "0.0.1-SNAPSHOT"

//java {
//    toolchain {
//        languageVersion = JavaLanguageVersion.of(21)
//    }
//}
kotlin {
    jvmToolchain(21) // Or your target JVM version
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
   // implementation("org.springframework.boot:spring-boot-starter-webflux")

    //implementation("org.springframework.boot:spring-boot-starter-scheduling")

    //mapowanie rest response do java
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")


    implementation("jakarta.xml.bind:jakarta.xml.bind-api")
    implementation ("org.glassfish.jaxb:jaxb-runtime")


    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")




    //openapi dependencies
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25")
    implementation ("org.jetbrains.kotlin:kotlin-reflect:1.9.25")
    implementation ("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation ("com.squareup.moshi:moshi-adapters:1.15.1")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation ("io.kotlintest:kotlintest-runner-junit5:3.4.2")

}


xjc {
    xjcVersion.set("3.0")
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

configurations.all {
    exclude(group = "ch.qos.logback")
    exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
}

//openApiGenerate {
//    generatorName.set("kotlin")  // lub "kotlin-spring"
//
//    inputSpec.set("$projectDir/src/main/resources/static/KSeF-online.yaml")
//    outputDir.set("$buildDir/generated")
//
//    configOptions.set(
//        mapOf(
//            "modelMutable" to "true",
//            "generateModels" to "all",
//            "interfaceOnly" to "false",
//            "additionalModelTypeAnnotations" to "@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true)"
//        )
//    )
//}
