package com.laioffer.spotify.ui.home


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.laioffer.spotify.R
import dagger.hilt.android.AndroidEntryPoint




//mvvm 的view（ui）
// 使用 @AndroidEntryPoint 注解，告诉 Hilt 这个类需要依赖注入
@AndroidEntryPoint
class HomeFragment : Fragment() {
    // 通过 viewModels 委托属性获取 ViewModel 实例，Hilt 会自动处理 ViewModel 的创建和生命周期管理
    private val viewModel: HomeViewModel by viewModels()

    // 创建和返回这个 fragment 的视图
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 使用布局填充器将布局 XML 文件转化为 View 对象，这个 View 对象就是这个 fragment 的视图
//        return inflater.inflate(R.layout.fragment_home, container, false)
        return ComposeView(requireContext()).apply {
           //这个函数用来设置ComposeView的内容。
            setContent {
                MaterialTheme(colors = darkColors()){
                    // 加载首页的布局，并传递ViewModel和点击事件的监听器
                    //这是一个自定义的函数，显示主屏幕的内容。这个函数接收一个ViewModel和一个点击事件的回调函数作为参数。
                    HomeScreen(viewModel, onTap = {
                        // 当点击事件发生时，导航到播放列表的页面，并传递被点击的对象
                        val direction = HomeFragmentDirections.actionHomeFragmentToPlaylistFragment(it)
                        //使用生成的方向来进行导航
                        findNavController().navigate(direction)
                        // 在控制台中打印被点击对象的名称
                        Log.d("HomeFragment", "We tapped ${it.name}")

                    })
                }
            }
        }



    }

    // 这个方法在视图创建完成后会被调用，你可以在这里进行一些视图相关的初始化操作
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 如果 ViewModel 的 uiState 表示正在加载，那么开始获取首页数据
        //通过在视图创建时检查 isLoading，你可以确保只有在数据还未加载或加载未完成时才触发加载操作。
        // 这样，即使视图在数据加载期间被多次创建，也只会有一次数据加载请求。
        //只要这个 Activity 或 Fragment 实例存在，它的 ViewModel 实例就会存在，从而保证用户界面数据的连续性。
        if (viewModel.uiState.value.isLoading) {
            viewModel.fetchHomeScreen()
        }
    }
}
