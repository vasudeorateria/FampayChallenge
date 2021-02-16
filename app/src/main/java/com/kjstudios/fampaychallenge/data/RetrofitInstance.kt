package com.kjstudios.fampaychallenge.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {

    companion object {
        private const val BASE_URL = "https://run.mocky.io/v3/"

        val retrofit: ApiInterface = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

}