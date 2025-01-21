package com.sf.cwms.api

import com.sf.cwms.CWmsApp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object ApiServiceGenerator {
    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder ()
    private var builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(CWmsApp.api_url)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient.build())
    private var retrofit: Retrofit = builder.build()
    fun <S> createService(serviceClass: Class<S>?): S {
        return retrofit.create<S>(serviceClass)
    }

    fun rebuild() {
        builder = Retrofit.Builder()
            .baseUrl(CWmsApp.api_url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
        retrofit = builder.build()
    }
}