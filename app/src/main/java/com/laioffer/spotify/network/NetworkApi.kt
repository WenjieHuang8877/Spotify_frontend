package com.laioffer.spotify.network


import com.laioffer.spotify.datamodel.Playlist
import com.laioffer.spotify.datamodel.Section
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

//声明一个interface
interface NetworkApi {
    //描述： get方法： baseurl后面跟着feed，response是一个list of section,包裹再一个call的wrapper里面, 本质上是一个任务
    @GET("feed")
    fun getHomeFeed(): Call<List<Section>>
    @GET("playlist/{id}")
    fun getPlaylist(@Path("id") id: Int): Call<Playlist>

}