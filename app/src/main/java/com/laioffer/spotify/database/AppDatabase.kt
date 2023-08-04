package com.laioffer.spotify.database



import androidx.room.Database
import androidx.room.RoomDatabase
import com.laioffer.spotify.datamodel.Album

//入口
// 定义AppDatabase作为抽象类，并扩展RoomDatabase。
// 使用@Database注解来标记该类作为Room数据库，声明实体和版本信息。
@Database(entities = [Album::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // 定义一个抽象方法，该方法没有具体的实现。
    // 当你请求一个DatabaseDao的实例时，Room会为你提供正确的实现。
    // 这允许你在应用程序中访问存储在数据库中的Album对象。
    abstract fun databaseDao(): DatabaseDao
}