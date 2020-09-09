import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "space.sentinel"
version = "1.0-SNAPSHOT"
val kotlinVersion = "1.3.40"

plugins {
    application
    kotlin("jvm") version "1.3.40"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.jetbrains.dokka") version "1.4.0"
}

application {
    mainClassName = "space.sentinel.AppKt"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("io.projectreactor:reactor-core:3.3.9.RELEASE")
    implementation("io.projectreactor.netty:reactor-netty:0.9.9.RELEASE")
    implementation("org.slf4j:slf4j-api:1.7.26")
    implementation("ch.qos.logback:logback-classic:0.9.26")
    implementation("ch.qos.logback:logback-core:0.9.26")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("io.github.config4k:config4k:0.4.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.+")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.+")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.+")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.10.+")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.10.+")
    implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:1.4.1")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.0.2.RELEASE")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.6.2")
    implementation("org.mariadb:r2dbc-mariadb:0.8.3-beta1")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.bytedeco:javacv-platform:1.5.2")
    {
        //        exclude("org.bytedeco", "openblas")
//        exclude("org.bytedeco", "opencv")
//        exclude("org.bytedeco", "ffmpeg")
        exclude("org.bytedeco", "flycapture")
        exclude("org.bytedeco", "videoinput")
        exclude("org.bytedeco", "artoolkitplus")
        exclude("org.bytedeco", "flandmark")
        exclude("org.bytedeco", "leptonica")
        exclude("org.bytedeco", "tesseract")
        exclude("org.bytedeco", "libfreenect")
        exclude("org.bytedeco", "libfreenect2")
        exclude("org.bytedeco", "librealsense")
        exclude("org.bytedeco", "librealsense2")
        exclude("org.bytedeco", "libdc1394")
        exclude("com.google.android", "android")
    }

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.9")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("io.projectreactor:reactor-test:3.3.9.RELEASE")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testImplementation("org.mybatis:mybatis:3.5.5")
    testImplementation("com.jayway.jsonpath:json-path:2.4.0")
    testImplementation("com.jayway.jsonpath:json-path-assert:2.2.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}

repositories {
    jcenter()
    mavenLocal()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava"
        ) {
            artifactId = "SentinelServer"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Sentinel Pi")
                description.set("Motion Detector for Raspberry Pi")
                url.set("https://github.com/314t0n/SentinelPi")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("314t0n")
                        name.set("Hajnal David")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("$buildDir/repos/releases")
            val snapshotsRepoUrl = uri("$buildDir/repos/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

val fatJar = task("fatJar", type = Jar::class) {
    manifest {
        attributes["Implementation-Title"] = "Sentinel Server"
        attributes["Implementation-Version"] = "1.0"
        attributes["Main-Class"] = "space.sentinel.AppKt"
    }
    from(configurations.runtime.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks["jar"] as CopySpec)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register<Jar>("uberJar") {
    archiveClassifier.set("uber")

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
    "test"(Test::class) {
        useJUnitPlatform()
    }
}

tasks.withType<ShadowJar>{
//    minimize()
}
