package com.perception.userinformation.ApiCalling


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query



interface WebAPIInterface {


    @GET("api/users?")
    fun UserDetials(@Query("page") pageindex: String, @Header("Content-Type") content: String): Call<ResponseBody>


}
