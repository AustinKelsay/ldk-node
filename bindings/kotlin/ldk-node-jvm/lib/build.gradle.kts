import org.gradle.api.tasks.testing.logging.TestExceptionFormat.*
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

// library version is defined in gradle.properties
val libraryVersion: String by project

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.7.10"

    // Apply the java-library plugin for API and implementation separation.
    id("java-library")
    id("maven-publish")
    id("signing")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")

    //// This dependency is exported to consumers, that is to say found on their compile classpath.
    //api("org.apache.commons:commons-math3:3.6.1")

    //// This dependency is used internally, and not exposed to consumers on their own compile classpath.
    //implementation("com.google.guava:guava:31.1-jre")
	// Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("net.java.dev.jna:jna:5.8.0")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()

	testLogging {
        events(PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR)
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
		showStandardStreams = true
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "org.lightningdevkit"
                artifactId = "ldk-node-jvm"
                version = libraryVersion

                from(components["java"])
                pom {
                    name.set("ldk-node-jvm")
                    description.set(
                        "LDK Node, a ready-to-go Lightning node library built using LDK and BDK."
                    )
                    url.set("https://lightningdevkit.org")
                    licenses {
                        license {
                            name.set("APACHE 2.0")
                            url.set("https://github.com/lightningdevkit/ldk-node/blob/main/LICENSE-APACHE")
                        }
                        license {
                            name.set("MIT")
                            url.set("https://github.com/lightningdevkit/ldk-node/blob/main/LICENSE-MIT")
                        }
                    }
                    developers {
                        developers {
                            developer {
                                id.set("tnull")
                                    name.set("Elias Rohrer")
                                    email.set("dev@tnull.de")
                            }
                        }
                    }
                    scm {
                        connection.set("scm:git:github.com/lightningdevkit/ldk-node.git")
                        developerConnection.set("scm:git:ssh://github.com/lightningdevkit/ldk-node.git")
                        url.set("https://github.com/lightningdevkit/ldk-node/tree/main")
                    }
                }
            }
        }
    }
}

signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications)
}
