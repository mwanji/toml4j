plugins{
    id("java")
    id("maven-publish")
    id("jacoco")
}

java{
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories{
    mavenCentral()
}

dependencies{
    implementation("com.google.code.gson:gson:2.8.9")

    testImplementation("junit:junit:4.12")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.9.3")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
}

tasks{
    test{
        useJUnitPlatform()
    }

    jacoco{
        toolVersion = "0.8.10"
    }

    jacocoTestReport{
        reports{
            xml.required.set(true)
            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }

    javadoc {
        options {
            (options as? StandardJavadocDocletOptions)?.apply {
                encoding = "UTF-8"

                addBooleanOption("html5", true)

                links("https://docs.oracle.com/javase/8/docs/api/")
            }
        }
    }

    check{
        finalizedBy("jacocoTestReport")
    }

    jar{
        dependsOn("javadoc")
    }
}

publishing{
    publications{
        create<MavenPublication>("maven"){
            groupId = "de.thelooter"
            artifactId = "toml4j"
            version = "0.7.3-SNAPSHOT"

            from(components["java"])

            pom{
                name.set("toml4j")
                description.set("Java library for parsing TOML")

                inceptionYear.set("2013")

                licenses{
                    license{
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("moandji.ezana")
                        name.set("Moandji Ezana")
                        email.set("mwanji@gmail.com")
                    }
                    developer {
                        id.set("thelooter")
                        name.set("Eve Kolb")
                        email.set("evekolb2204@gmail.com")
                    }
                }

                issueManagement{
                    system.set("GitHub")
                    url.set("https://github.com/thelooter/toml4j/issues")
                }

                scm {
                    connection.set("scm:git:git://github.com/thelooter/toml4j.git")
                    developerConnection.set("scm:git:git@github.com:thelooter/toml4j.git")
                    url.set("https://github.com/thelooter/toml4j")
                    tag.set("HEAD")
                }

                ciManagement{
                    system.set("Github Actions")
                    url.set("https://github.com/thelooter/toml4j/actions")
                }
            }
        }
    }
}
