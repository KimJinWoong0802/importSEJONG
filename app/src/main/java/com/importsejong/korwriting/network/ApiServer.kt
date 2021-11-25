package com.importsejong.korwriting.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiServer :BaseNetwork(){

    override fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    companion object {
        private var _network: networkInterface? = null

        val network : networkInterface

            get() {
                if (_network == null) {
                    val apiServer = ApiServer()
                    _network = apiServer.retrofit.create(networkInterface::class.java)
                }
                return _network!!
            }
    }
}