package com.laioffer.spotify.repository

import com.laioffer.spotify.database.DatabaseDao
import com.laioffer.spotify.datamodel.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

// 定义FavoriteAlbumRepository类，负责与数据库进行交互以管理喜爱的专辑
// 使用@Inject构造函数注入，允许Dagger或其他依赖注入框架自动提供所需的DatabaseDao依赖
class FavoriteAlbumRepository @Inject constructor(private val databaseDao: DatabaseDao) {

    // 函数返回一个Flow<Boolean>，表示专辑是否为喜爱的
    // Flow是一个响应式流，可以在数据更改时通知观察者,这是一个连续的数据，然后scope 是fregmentscope
    // 使用flowOn(Dispatchers.IO)确保数据库查询在IO线程上执行
    fun isFavoriteAlbum(id: Int): Flow<Boolean> =
        databaseDao.isFavoriteAlbum(id).flowOn(Dispatchers.IO)


    suspend fun favoriteAlbum(album: Album) = withContext(Dispatchers.IO) {
        databaseDao.favoriteAlbum(album)
    }


    suspend fun unFavoriteAlbum(album: Album) = withContext(Dispatchers.IO) {
        databaseDao.unFavoriteAlbum(album)
    }
    fun fetchFavoriteAlbums(): Flow<List<Album>> =
        databaseDao.fetchFavoriteAlbums().flowOn(Dispatchers.IO)

}
