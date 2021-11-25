package com.importsejong.korwriting.network

import com.importsejong.korwriting.network.data.ocrdata
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File
import java.util.concurrent.TimeUnit

interface networkInterface {
    @Multipart
    @POST("v2/vision/text/ocr")
    fun ocr(
        // @Header("Content-Type") type :String,
        @Header("Authorization") rest_api_key: String = "",
        @Part image : MultipartBody.Part
    ): Call<ocrdata>

    companion object{
        private const val rest_api_key = "KakaoAK daf1f2ea535740044cc45bb025b6b041"

        fun create() : networkInterface {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .build()
                return@Interceptor it.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS)
                .writeTimeout(100,TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(networkInterface::class.java)
        }
    }

}