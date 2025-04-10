plugins {
    id("com.gradle.develocity") version "3.19.2"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "2.2.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "develocity-runner-utilization"

develocity {
    buildScan {
        val acceptTOSProp = "acceptGradleTOS"
        if (extra.properties.sets(acceptTOSProp)) {
            termsOfUseUrl = "https://gradle.com/terms-of-service"
            termsOfUseAgree = "yes"
        }
        publishing.onlyIf { true }

        obfuscation {
            ipAddresses { addresses -> addresses.map { _ -> "0.0.0.0" } }
        }
    }
}

fun Map<String, Any>.sets(key: String) : Boolean {
    val value = this.getOrDefault(key, "false").toString()
    return value.isBlank() || value.toBoolean()
}