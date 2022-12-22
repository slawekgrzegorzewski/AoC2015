plugins {
    id("java")
}

group = "pl.sg"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                useJUnitJupiter()
            }
        }
    }
}
group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:20.1.0")
    implementation("com.google.guava:guava:31.1-jre")

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation ("org.junit.jupiter:junit-jupiter-params:5.7.2")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}