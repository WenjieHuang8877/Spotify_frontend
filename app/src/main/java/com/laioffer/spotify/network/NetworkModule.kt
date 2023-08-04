package com.laioffer.spotify.network


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

// class -> new -> instance/object 通常
//static
@Module//拿依赖的工厂，
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8080/"
    @Provides // 可以从工厂拿到这个function
    @Singleton // 只有一个
   // @ActivityScoped
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // json serialization
            .client(OkHttpClient()) //http协议 所有互联网终端都需要实现的协议
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): NetworkApi{
        return  retrofit.create(NetworkApi::class.java)
    }
}
