package com.jploran.cineexplorer.data.remote

import com.jploran.cineexplorer.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    fun getRetrofit(): Retrofit {

        // Custom interceptor to add the API key to each request
        val apiKeyInterceptor = Interceptor { chain ->
            val original: Request = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyODJhYzVjOWI2NDY3OGNiZDJmNDBjZWEyNjUyYWU4OCIsInN1YiI6IjY2NDJjMjAyZTg2YmQzMTBiYTE4MmM0MiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.O8k8e_bcLthkotp-cQ66IMYZ24fvZrTePdGUqABTEHs")
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            addInterceptor(apiKeyInterceptor) // Add the API key interceptor
        }.build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}