package com.kjstudios.fampaychallenge.data

import com.kjstudios.fampaychallenge.models.CardGroupList
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("fefcfbeb-5c12-4722-94ad-b8f92caad1ad")
    fun getCardGroupList(): Call<CardGroupList>
}