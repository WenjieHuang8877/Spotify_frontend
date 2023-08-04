package com.laioffer.spotify.player
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.laioffer.spotify.R
import com.laioffer.spotify.ui.theme.TransparentBlack

// 定义播放条
@Composable
fun PlayerBar(viewModel: PlayerViewModel) {
    // 通过viewModel收集UI状态
    val uiState by viewModel.uiState.collectAsState()
    // 判断是否显示播放条，当专辑和歌曲都不为空时显示
    val isVisible = uiState.album != null && uiState.song != null

    // 控制播放条的动画可见性
    AnimatedVisibility(isVisible) {
        // 显示具体内容
        PlayerBarContent(uiState = uiState,
            togglePlay = {
                // 根据播放状态切换播放和暂停
                if (uiState.isPlaying) {
                    viewModel.pause()
                } else {
                    viewModel.play()
                }
            },
            seekTo = {
                viewModel.seekTo(it)

            }
        )
    }
}

// 定义播放条的具体内容
@Composable
private fun PlayerBarContent(
    uiState: PlayerUiState,
    togglePlay: () -> Unit, // 控制播放和暂停的切换
    seekTo: (Long) -> Unit // 歌曲进度的控制
) {
    // 主布局为列
    Column(
        modifier = Modifier
            .fillMaxWidth() // 填充最大宽度
            .padding(horizontal = 8.dp) // 水平方向内边距
            .clip(RoundedCornerShape(8.dp)) // 圆角效果
            .background(TransparentBlack) // 背景色
    ) {
        // 内部行布局
        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp, top = 8.dp, bottom = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp), // 水平元素间隔
            verticalAlignment = Alignment.CenterVertically, // 垂直居中对齐
        ) {
            // 异步加载图片，显示专辑封面
            AsyncImage(
                model = uiState.album?.cover,
                contentDescription = null,
                modifier = Modifier
                    .width(60.dp)
                    .aspectRatio(1.0f)
                    .clip(RoundedCornerShape(8.dp)), // 封面的圆角效果
                contentScale = ContentScale.FillBounds
            )

            // 显示歌曲名称和歌词
            Column(modifier = Modifier.weight(1.0f)) {
                Text(
                    text = uiState.song?.name ?: "",
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                )
                Text(
                    text = uiState.song?.lyric ?: "",
                    style = MaterialTheme.typography.caption,
                    color = Color.White,
                )
            }

            // 控制播放和暂停的图标按钮
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        togglePlay()
                    },
                painter = painterResource(
                    id = if (uiState.isPlaying) {
                        R.drawable.ic_pause_24 // 播放中显示暂停图标
                    } else {
                        R.drawable.ic_play_arrow_24 // 暂停中显示播放图标
                    }
                ),
                tint = Color.White,
                contentDescription = ""
            )
        }
        SeekBar(
            uiState.currentMs.toFloat(),
            uiState.durationMs.toFloat()
        ) {
            seekTo(it)
        }

    }
}

// 定义一个用于展示和控制音乐播放进度的SeekBar组件
@Composable
private fun SeekBar(
    currentMs: Float, // 当前播放的时间位置（毫秒）
    durationValue: Float, // 歌曲总时长（毫秒）
    seekTo: (Long) -> Unit // 跳转到特定时间位置的回调函数
) {
    // 用于存储滑块位置的可观察状态变量
    //用于在重新组合过程中保持状态。它允许你在Composable函数内部存储和维护状态，而不会在每次重新组合时丢失。
    var seekBarPosition by remember { mutableStateOf(0f) }
    // 用于表示用户是否正在拖动滑块的可观察状态变量
    var seeking by remember { mutableStateOf(false) }

    // 如果用户没有拖动滑块，将滑块位置设置为当前播放位置
    if (!seeking) {
        seekBarPosition = currentMs
    }

    // Slider组件用于显示和控制滑块
    Slider(
        modifier = Modifier.height(24.dp), // 控制滑块高度
        value = seekBarPosition, // 滑块的当前位置
        valueRange = 0f..durationValue, // 滑块的有效范围（从0到歌曲总时长）
        onValueChange = {
            // 当用户拖动滑块时，设置seeking为true并更新滑块位置
            seeking = true
            seekBarPosition = it
        },
        onValueChangeFinished = {
            // 当用户完成拖动时，调用seekTo回调，并将seeking设置为false
            seekTo(seekBarPosition.toLong())
            seeking = false
        },
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent, // 滑块的颜色
            inactiveTrackColor = Color.LightGray, // 未激活轨迹的颜色
            activeTrackColor = Color.Green // 激活轨迹的颜色
        )
    )
}

