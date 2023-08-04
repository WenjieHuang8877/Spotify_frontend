package com.laioffer.spotify.database
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// 使用@Module注解标记这个对象是一个Dagger模块，用于提供依赖项的实例。
@Module
// 使用@InstallIn注解指示这个模块应该在SingletonComponent中安装，意味着提供的依赖项将具有单例作用域。
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // 使用@Provides注解标记这个函数用于向Dagger提供AppDatabase的实例。
    // @Singleton注解确保在整个应用程序生命周期内只提供一个实例。
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        // 使用Room的databaseBuilder方法创建AppDatabase的实例。
        // 这个方法需要应用程序上下文，数据库类的Java类对象，和一个数据库名。
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "spotify_db"
        ).build() // 构建并返回数据库实例。
    }

    // 使用@Provides注解标记这个函数用于向Dagger提供DatabaseDao的实例。
    // 通过调用之前提供的AppDatabase实例的databaseDao方法来创建。
    // @Singleton注解确保在整个应用程序生命周期内只提供一个实例。
    @Provides
    @Singleton
    fun provideDatabaseDao(database: AppDatabase): DatabaseDao {
        return database.databaseDao() // 返回DatabaseDao实例。
    }
}
