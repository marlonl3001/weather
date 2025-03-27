package br.com.mdr.weather.data.remote

import okhttp3.Interceptor
import okhttp3.Response

//Interceptor criado para adicionar a query appid à todas as requisições da WeatherApi
class ApiKeyInterceptor(private val apiKey: String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest = chain.request()
        val currentUrl = currentRequest.url

        val newUrl = currentUrl
            .newBuilder()
            .addQueryParameter("appid", apiKey)
            .addQueryParameter("lang", "pt_br")
            .build()

        val newRequest = currentRequest
            .newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}