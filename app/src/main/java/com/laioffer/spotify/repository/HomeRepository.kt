package com.laioffer.spotify.repository

import android.util.Log
import com.laioffer.spotify.datamodel.Section
import com.laioffer.spotify.network.NetworkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

// 使用 @Inject 注解告诉 Hilt 我们需要一个 networkApi 参数，Hilt 会自动查找和注入一个 NetworkApi 的实例
class HomeRepository @Inject constructor(private val networkApi: NetworkApi) {

    // 使用 suspend 关键字标记这是一个协程函数，可以在后台线程中执行并非阻塞地获取结果
    //加withContext(Dispatchers.IO)就变成了一个可以挂起的call，coroutine知道会再io系统上被suspend
    suspend fun getHomeSections(): List<Section> = withContext(Dispatchers.IO) {
        // 在 IO 线程中进行网络请求，获取首页的 sections 数据
        // 执行网络请求并获取响应体，注意：这里假设网络请求一定成功并且响应体一定存在，
        // 在实际应用中，我们需要处理请求失败和响应体为空的情况
        //!! 操作符用于非空断言，也就是告诉编译器我们确定这个变量或表达式的值不会为 null。如果这个变量或表达式的值确实为 null，那么程序会立即抛出 NullPointerException 异常。
        delay(3000)
        Log.d("Tim", Thread.currentThread().name + "4")
        networkApi.getHomeFeed().execute().body()!!
    }
//    suspend fun getHomeSections(): List<Section> {
//        // 在 IO 线程中进行网络请求，获取首页的 sections 数据
//        // 执行网络请求并获取响应体，注意：这里假设网络请求一定成功并且响应体一定存在，
//        // 在实际应用中，我们需要处理请求失败和响应体为空的情况
//        return withContext(Dispatchers.IO) {
//            networkApi.getHomeFeed().execute().body()!!
//        }
//    }

}