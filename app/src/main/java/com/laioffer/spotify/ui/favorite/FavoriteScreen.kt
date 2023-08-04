package com.laioffer.spotify.ui.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.laioffer.spotify.R
import com.laioffer.spotify.datamodel.Album

// 定义了一个Composable函数，表示收藏专辑的屏幕
// 它接收一个ViewModel和一个点击回调函数，该回调函数用于处理专辑点击事件
@Composable
fun FavoriteScreen(viewModel: FavoriteViewModel, onTap: (Album) -> Unit) {
    // 通过viewModel获取UI状态，并通过collectAsState将Flow转换为State
    val uiState by viewModel.uiState.collectAsState()

    // 调用FavoriteScreenContent来渲染屏幕内容，传递专辑列表和点击回调
    FavoriteScreenContent(uiState.albums, onTap)
}

// 私有Composable函数，用于渲染收藏屏幕的内容
// 接收一个专辑列表和一个点击回调函数
@Composable
private fun FavoriteScreenContent(albums: List<Album>, onTap: (Album) -> Unit) {
    // 使用LazyColumn来显示可滚动的列，其中包括标题和专辑列表
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        // 显示标题文本
        item {
            Text(
                stringResource(id = R.string.menu_favorite),
                style = MaterialTheme.typography.h4,
                color = Color.White
            )
        }

        // 添加间隔
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 遍历专辑列表，为每个专辑调用FavoriteAlbumRow来渲染行
        items(albums) { album ->
            FavoriteAlbumRow(
                album = album,
                onTap = onTap
            )
        }
    }
}

// 私有Composable函数，用于渲染每个收藏专辑的行
// 接收一个专辑对象和一个点击回调函数
@Composable
private fun FavoriteAlbumRow(album: Album, onTap: (Album) -> Unit) {
    // 使用Row来布局专辑的封面和详情
    Row(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .clickable { onTap(album) }, // 点击时调用回调
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 使用AsyncImage加载和显示专辑封面
        AsyncImage(
            model = album.cover,
            contentDescription = null,
            modifier = Modifier
                .width(60.dp)
                .aspectRatio(1.0f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.FillBounds
        )

        // 使用Column来垂直排列专辑名称和详情
        Column(
            modifier = Modifier
                .weight(1.0f)
                .padding(start = 8.dp)
        ) {
            // 显示专辑名称
            Text(
                text = album.name,
                style = MaterialTheme.typography.body2,
                color = Color.White,
            )

            // 显示专辑的艺术家和年份
            Text(
                text = stringResource(id = R.string.album_info, album.artists, album.year),
                style = MaterialTheme.typography.caption,
                color = Color.Gray,
            )
        }
    }
}
