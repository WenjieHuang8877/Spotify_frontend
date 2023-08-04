package com.laioffer.spotify.database

import androidx.room.*
import com.laioffer.spotify.datamodel.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {
    // 插入或替换现有的相册记录。
    // 如果记录与现有记录冲突（基于主键），则将替换现有记录。
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun favoriteAlbum(album: Album)

    // 查询相册是否存在于数据库中，通过ID来判断。
    // 如果存在，则返回true；否则返回false。
    // 结果作为Flow<Boolean>返回，允许观察更改。
    //Flow<Boolean>表示随时间的变化，相册的收藏状态可能会改变。
    // 通过返回一个Flow，调用者可以观察这个状态的变化，并在状态改变时做出响应。
    @Query("SELECT EXISTS(SELECT * FROM Album WHERE id = :id)")
    fun isFavoriteAlbum(id: Int): Flow<Boolean>

    // 从数据库中删除一个指定的相册记录。
    // 需要传入要删除的相册对象。
    @Delete
    fun unFavoriteAlbum(album: Album)

    @Query("select * from Album")
    fun fetchFavoriteAlbums(): Flow<List<Album>>
}