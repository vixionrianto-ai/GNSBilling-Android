package com.gns.billing

import android.app.Application
import com.gns.billing.api.RetrofitClient

class GnsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}
