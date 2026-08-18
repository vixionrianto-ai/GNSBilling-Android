plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.gns.billing"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.gns.billing"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "2.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
}

dependencies {
}
