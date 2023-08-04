package com.laioffer.spotify.ui.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.laioffer.spotify.player.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // 表明这个类可以接收Hilt依赖注入
class PlaylistFragment : Fragment() {
    private val navArgs by navArgs<PlaylistFragmentArgs>() // 通过安全导航参数获取传递给此Fragment的参数
    //使用 by viewModels() 则会为特定Fragment创建或获取ViewModel实例。
    // 这意味着该ViewModel的生命周期将仅与该特定Fragment的生命周期相关联，其他Fragment不会共享此ViewModel实例。
    private val viewModel: PlaylistViewModel by viewModels()

    //使用 by activityViewModels() 会创建或获取与整个Activity的生命周期相关联的ViewModel实例。
    // 因此，Activity中的所有Fragment都可以共享此ViewModel实例。
    //!!
    private val playerViewModel: PlayerViewModel by activityViewModels() // 使用ViewModel委托获取PlayerViewModel的实例，与整个活动共享

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 在此Fragment中使用ComposeView，并设置其内容为PlaylistScreen
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(colors = darkColors()) { // 使用暗色主题
                    PlaylistScreen(
                        playlistViewModel = viewModel,
                        playerViewModel = playerViewModel
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PlaylistFragment", navArgs.album.toString()) // 记录传递给此Fragment的专辑
        viewModel.fetchPlaylist(navArgs.album) // 从ViewModel中获取播放列表，并使用传递的专辑参数
    }
}


