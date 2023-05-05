import java.io.ByteArrayOutputStream

plugins{
    id("java-library")
    id("maven-publish")
    id("jacoco")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}



repositories{
    mavenCentral()
}

dependencies{
    implementation("com.google.code.gson:gson:2.8.9")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
}

tasks{
    java{
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        withJavadocJar()
        withSourcesJar()
    }

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

signing{
    isRequired = !isFork() && isAction()
    sign(publishing.publications)
}

nexusPublishing{
    repositories{
        sonatype{
            username.set(findProperty("SONATYPE_USERNAME") as String?)
            password.set(findProperty("SONATYPE_PASSWORD") as String?)
        }
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
                url.set("https://github.com/thelooter/toml4j")

                inceptionYear.set("2013")

                licenses{
                    license{
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
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

    repositories{
        maven{
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials{
                username = findProperty("SONATYPE_USERNAME") as String?
                password = findProperty("SONATYPE_PASSWORD") as String?
            }
        }
    }
}

fun isFork(): Boolean {
    return run("git", "config", "--get", "remote.origin.url").contains("thelooter/toml4j")
}

fun isAction(): Boolean {
    return System.getenv("CI") != null
}


fun run(vararg cmd: String): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine(*cmd)
        standardOutput = stdout
    }
    return stdout.toString().trim()
}