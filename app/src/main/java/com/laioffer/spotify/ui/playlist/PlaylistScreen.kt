
package com.laioffer.spotify.ui.playlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.laioffer.spotify.R
import com.laioffer.spotify.datamodel.Album
import com.laioffer.spotify.datamodel.Song
import com.laioffer.spotify.player.PlayerUiState
import com.laioffer.spotify.player.PlayerViewModel

// 顶级 Composable 函数，接收一个 PlaylistViewModel 实例作为参数，然后展示整个播放列表页面。

@Composable
fun PlaylistScreen(
    playlistViewModel: PlaylistViewModel, // 播放列表的ViewModel
    playerViewModel: PlayerViewModel      // 播放器的ViewModel
) {
    val playlistUiState by playlistViewModel.uiState.collectAsState() // 收集播放列表状态
    val playerUiState by playerViewModel.uiState.collectAsState()     // 收集播放器状态

    // 组合播放列表屏幕内容
    PlaylistScreenContent(
        playlistUiState = playlistUiState,
        playerUiState = playerUiState,
        onTapFavorite = { // 点击收藏时的操作
            Log.d("PlaylistScreen", "Tap favorite $it")
            playlistViewModel.toggleFavorite(it)
        },
        onTapSong = { // 点击歌曲时的操作
            playerViewModel.load(it, playlistUiState.album)
            playerViewModel.play()
        }
    )
}

@Composable
private fun PlaylistScreenContent(
    playlistUiState: PlaylistUiState,  // 播放列表的状态
    playerUiState: PlayerUiState,      // 播放器的状态
    onTapFavorite: (Boolean) -> Unit,  // 处理点击收藏的函数
    onTapSong: (Song) -> Unit          // 处理点击歌曲的函数
) {
    Column(
        modifier = Modifier
            .padding(16.dp), // 设置边距
    ) {
        // 显示专辑封面和收藏图标
        Cover(
            album = playlistUiState.album,
            isFavorite = playlistUiState.isFavorite,
            onTapFavorite = onTapFavorite
        )
        // 显示播放列表的头部
        PlaylistHeader(album = playlistUiState.album)
        // 显示播放列表的内容
        PlaylistContent(
            playlist = playlistUiState.playlist,
            currentSong = playerUiState.song,
            onTapSong = onTapSong
        )
    }
}

@Composable
private fun PlaylistHeader(album: Album) { // 专辑头部信息
    Column {
        // 专辑名称
        Text(
            text = album.name,
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(top = 16.dp)
        )
        // 专辑艺人和年份
        Text(
            text = stringResource(id = R.string.album_info, album.artists, album.year),
            style = MaterialTheme.typography.body2,
            color = Color.LightGray,
        )
    }
}

@Composable
private fun PlaylistContent(
    playlist: List<Song>,    // 歌曲列表
    currentSong: Song?,      // 当前播放的歌曲
    onTapSong: (Song) -> Unit // 点击歌曲时的操作
) {
    val state = rememberLazyListState()
    LazyColumn(state = state) { // 懒加载列表
        items(playlist) { song ->
            // 显示每一首歌曲
            Song(
                song,
                currentSong == song,
                onTapSong
            )
        }

        item {
            Spacer(modifier = Modifier.height(40.dp)) // 添加间距
        }
    }
}

// 此处的其他函数注释类似
@Composable
private fun Song(
    song: Song,
    isPlaying: Boolean,
    onTapSong: (Song) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { onTapSong(song) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                text = song.name,
                style = MaterialTheme.typography.body2,
                color = if (isPlaying) {
                    Color.Green
                } else {
                    Color.White
                }
            )
            Text(
                text = song.lyric,
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = song.length,
            style = MaterialTheme.typography.caption,
            color = Color.LightGray
        )
    }
}

@Composable
private fun Cover(
    album: Album,
    isFavorite: Boolean,
    onTapFavorite: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onTapFavorite(!isFavorite) },
                painter = painterResource(
                    id = if (isFavorite) {
                        R.drawable.ic_favorite_24
                    } else {
                        R.drawable.ic_unfavorite_24
                    }
                ),
                tint = if (isFavorite) {
                    Color.Green
                } else {
                    Color.Gray
                },
                contentDescription = ""
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1.0f)
                    .align(Alignment.Center)
            ) {
                // Vinyl background
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(id = R.drawable.vinyl_background),
                    contentDescription = null
                )

                AsyncImage(
                    model = album.cover,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .aspectRatio(1.0f)
                        .align(Alignment.Center)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds
                )
            }

        }
        Text(
            text = album.description,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.caption,
            color = Color.Gray,
        )
    }
}

