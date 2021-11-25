package com.importsejong.korwriting

import com.importsejong.korwriting.network.data.ocrdata
import com.importsejong.korwriting.network.networkInterface
import okhttp3.MultipartBody
import retrofit2.Callback

object Dao {
    fun ocr(rest_api_key: String,image : MultipartBody.Part, callback: Callback<ocrdata>){
        val api =  networkInterface.create()
        api.ocr( rest_api_key,image).enqueue(callback)
    }
}
//type :String,
//type,