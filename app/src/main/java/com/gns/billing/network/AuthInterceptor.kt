package com.gns.billing.network

import com.gns.billing.session.SessionProvider
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val request = chain.request()
        val requestBuilder = request.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("X-Requested-With", "XMLHttpRequest")

        // Tambahkan token jika tersedia dan bukan untuk endpoint login
        val token = SessionProvider.token
        if (!token.isNullOrBlank() && !request.url.toString().contains("login")) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }

}
