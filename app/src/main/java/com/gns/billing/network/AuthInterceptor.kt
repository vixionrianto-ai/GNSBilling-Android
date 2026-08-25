package com.gns.billing.network

import android.content.Context
import com.gns.billing.session.SessionEvents
import com.gns.billing.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context.applicationContext)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("X-Requested-With", "XMLHttpRequest")

        val token = sessionManager.getToken()
        if (!token.isNullOrBlank() && !request.url.encodedPath.endsWith("/login")) {
            builder.header("Authorization", "Bearer $token")
        }

        val response = chain.proceed(builder.build())

        if (response.code == 401 && !request.url.encodedPath.endsWith("/login")) {
            response.close()
            sessionManager.logout()
            SessionEvents.emitTimeout()
            return response
        }

        return response
    }
}
