package com.laioffer.spotify.player


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.laioffer.spotify.datamodel.Album
import com.laioffer.spotify.datamodel.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor( private val exoPlayer: ExoPlayer
): ViewModel(), Player.Listener {
    // 定义了一个MutableStateFlow来表示播放器的UI状态，它是一个热Flow，可以发出状态的更新
    private val _uiState = MutableStateFlow(PlayerUiState())
    // 将MutableStateFlow转换为只读的StateFlow，暴露给外部观察
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    //这段代码使用了Kotlin的Flow库，不断地(1sec)监视ExoPlayer的当前播放位置和总持续时间。
    // 如果ExoPlayer正在播放，它每秒更新一次，反映在UI状态中，并记录在日志中。
    // 通过使用ViewModel的作用域，该监视协程将与ViewModel的生命周期同步，
    // 确保在ViewModel销毁时自动取消监视。
    init {
        // 将当前类添加为ExoPlayer的监听器。这允许当前类接收播放器状态的更新。
        exoPlayer.addListener(this)

        // 在ViewModel的作用域内启动一个协程。
        viewModelScope.launch {
            // 创建一个无限循环的流，用于监视ExoPlayer的播放状态。
            flow {
                while (true) { // 无限循环，持续监控
                    if (exoPlayer.isPlaying) { // 当ExoPlayer正在播放时
                        // 将当前播放位置和持续时间作为流的下一个值发出
                        emit(exoPlayer.currentPosition to exoPlayer.duration)
                    }
                    // 每次迭代后延迟1000毫秒，减少资源使用并平滑更新
                    delay(1000)
                }
            }.collect { // 收集流的值，并对每个新值执行以下操作
                // 将UI状态的当前毫秒和持续毫秒更新为收集到的值
                _uiState.value = uiState.value.copy(currentMs = it.first, durationMs = it.second)
                // 在日志中记录当前的播放位置和持续时间，以便调试
                Log.d("SpotifyPlayerTime", "CurrentMs: ${it.first}, DurationMs: ${it.second}")
            }
        }
    }


    // 加载歌曲并准备播放
    fun load(song: Song, album: Album) {
        // 更新UI状态，包括当前的专辑、歌曲和播放状态
        _uiState.value = PlayerUiState(album = album, song = song, isPlaying = false)
        // 创建MediaItem并设置给ExoPlayer
        val mediaItem = MediaItem.Builder().setUri(song.src).build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare() // 准备播放
    }

    // 控制ExoPlayer播放
    fun play() {
        exoPlayer.play()
    }

    // 控制ExoPlayer暂停
    fun pause() {
        exoPlayer.pause()
    }
    override fun onCleared() {
        // 当 ViewModel 被清除时，从 ExoPlayer 中移除监听器
        exoPlayer.removeListener(this)
        // 调用父类的 onCleared() 方法
        super.onCleared()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        // 当 ExoPlayer 的播放状态改变时调用此方法
        super.onIsPlayingChanged(isPlaying)
        // 记录播放状态
        Log.d("SpotifyPlayer", isPlaying.toString())
        // 更新 UI 状态，将播放状态设置为新的值
        _uiState.value = _uiState.value.copy(
            isPlaying = isPlaying
        )
    }

    override fun onPlayerError(error: PlaybackException) {
        // 当 ExoPlayer 遇到播放错误时调用此方法
        super.onPlayerError(error)
        // 记录错误
        Log.d("spotify", error.toString())
    }
    fun seekTo(positionMs: Long) {
        _uiState.value = uiState.value.copy(
            currentMs = positionMs
        )
        exoPlayer.seekTo(positionMs)
    }



}

// 用于表示播放器的UI状态的数据类
data class PlayerUiState(
    val album: Album? = null, // 当前播放的专辑
    val song: Song? = null,   // 当前播放的歌曲
    val isPlaying: Boolean = false, // 播放状态
    val currentMs: Long = 0,  // 当前播放位置，以毫秒为单位
    val durationMs: Long = 0, // 歌曲总时长，以毫秒为单位
)