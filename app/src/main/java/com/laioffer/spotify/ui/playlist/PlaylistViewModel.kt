package com.laioffer.spotify.ui.playlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laioffer.spotify.datamodel.Album
import com.laioffer.spotify.datamodel.Song
import com.laioffer.spotify.repository.FavoriteAlbumRepository
import com.laioffer.spotify.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 使用 HiltViewModel 注解，使得 Hilt 可以提供这个 ViewModel
// Hilt 是一个依赖注入库，它可以简化 Android 项目中的依赖注入
@HiltViewModel
class PlaylistViewModel @Inject constructor(
    // 通过构造函数注入，Hilt 会提供一个 PlaylistRepository 的实例
    private val playlistRepository: PlaylistRepository,
    private val favoriteAlbumRepository: FavoriteAlbumRepository

) : ViewModel() {
    // 创建一个 MutableStateFlow 来保存界面状态
    // MutableStateFlow 是一个热数据流，可以发出状态更新，并且可以修改其值
    private val _uiState = MutableStateFlow(
        PlaylistUiState(
            Album.empty()
        )
    )

    // 为外部提供一个只读的 StateFlow
    // 这样可以保证外部只能观察状态，不能修改状态
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    // 根据提供的专辑信息，从仓库获取播放列表并更新界面状态
    fun fetchPlaylist(album: Album) {
        // 先更新专辑信息
        _uiState.value = _uiState.value.copy(album = album)

        // 启动协程，在后台获取播放列表
        //这个scope是一个lifecycle的范围，它的范围和viewmodel的范围一致
        viewModelScope.launch {
            // 从仓库获取播放列表
            val playlist = playlistRepository.getPlaylist(album.id)
            // 更新界面状态
            _uiState.value = _uiState.value.copy(playlist = playlist.songs)
            // 打印当前状态到控制台
            Log.d("PlaylistViewModel", _uiState.value.toString())
        }

        // 使用ViewModel的作用域启动一个新的协程
        // 这确保了协程的生命周期与ViewModel的生命周期绑定
        // 当ViewModel被清理时，与其关联的协程也会自动取消
        viewModelScope.launch {
            // 调用favoriteAlbumRepository的isFavoriteAlbum方法，检查专辑是否为喜爱的
            // 这返回一个Flow<Boolean>，表示是否为喜爱的专辑
            // colect是一个suspend function
            favoriteAlbumRepository.isFavoriteAlbum(album.id).collect {
                // 使用collect操作符收集Flow的每个值
                // 当isFavoriteAlbum发出新值时，这个代码块会被调用
                // 每次值更改时，都会更新_uiState，这是一个可观察的UI状态对象
                _uiState.value = _uiState.value.copy(
                    isFavorite = it // 将isFavorite字段更新为Flow发出的新值
                )
            }
        }
    }
    fun toggleFavorite(isFavorite: Boolean) {
        val album = _uiState.value.album
        viewModelScope.launch {
            if (isFavorite) {
                favoriteAlbumRepository.favoriteAlbum(album)
            } else {
                favoriteAlbumRepository.unFavoriteAlbum(album)
            }
        }
    }


}

// 界面状态的数据类
// 包括一个专辑信息，是否为喜欢的专辑，以及播放列表
data class PlaylistUiState(
    val album: Album,
    val isFavorite: Boolean = false,
    val playlist: List<Song> = emptyList()
)
