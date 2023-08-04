package com.laioffer.spotify

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import coil.compose.AsyncImage
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.laioffer.spotify.database.DatabaseDao
import com.laioffer.spotify.datamodel.Album
import com.laioffer.spotify.network.NetworkApi
import com.laioffer.spotify.player.PlayerBar
import com.laioffer.spotify.player.PlayerViewModel
import com.laioffer.spotify.repository.HomeRepository
import com.laioffer.spotify.ui.theme.SpotifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// 使用 @AndroidEntryPoint 注解标记这是一个 Android 入口点，Hilt 需要在这里进行依赖注入
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // 通过 @Inject 注解告诉 Hilt 这个变量需要注入的对象，这个对象会在 Hilt 的容器中查找
    @Inject
    lateinit var api: NetworkApi // 对应 Hilt 中提供的 NetworkApi
    @Inject
    lateinit var databaseDao: DatabaseDao
    //by viewModels()产生当下class 的scope 如果再fregment 就是fregment scope
    //但是这里activity scope 因为是在activity class
    private val playerViewModel: PlayerViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)

        // 寻找 NavHostFragment，并获取 NavController，以便操作导航
        val navHostFragment =supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 设置 Navigation Graph，并和 BottomNavigationView 进行绑定
        navController.setGraph(R.navigation.nav_graph)
        NavigationUI.setupWithNavController(navView, navController)

        // 处理 BottomNavigationView 的选中事件，确保正确导航
        navView.setOnItemSelectedListener{ menuItem ->
            NavigationUI.onNavDestinationSelected(menuItem, navController)
            navController.popBackStack(menuItem.itemId, inclusive = false)
            true
        }
        //找到element，然后使用新的内容
        val playerBar = findViewById<ComposeView>(R.id.player_bar)
        playerBar.apply {
            setContent {
                MaterialTheme(colors = darkColors()) {
                    PlayerBar(
                        playerViewModel
                    )
                }
            }
        }

        // 用于测试网络请求
        GlobalScope.launch(Dispatchers.IO) {
            // 使用注入的 api 对象进行网络请求
            val task = api.getHomeFeed() // 获取任务
            val response = task.execute() // 执行任务
            val sections = response.body() // 从响应中读取信息
            Log.d("Network", sections.toString())
        }

//        // 此代码块在每次应用程序启动时运行 for test
//        GlobalScope.launch {
//
//            // 使用GlobalScope.launch启动一个新的协程，这将在应用程序的全局范围内运行
//            withContext(Dispatchers.IO) {
//                // 切换到IO调度器，因为下面的操作可能涉及I/O操作（例如数据库访问）
//
//                // 创建一个新的Album对象，使用提供的参数
//                val album = Album(
//                    id = 1,
//                    name = "Hexagonal",
//                    year = "2008",
//                    cover = "https://upload.wikimedia.org/wikipedia/en/6/6d/Leessang-Hexagonal_%28cover%29.jpg",
//                    artists = "Lesssang",
//                    description = "Leessang (Korean: 리쌍) was a South Korean hip hop duo, composed of Kang Hee-gun (Gary or Garie) and Gil Seong-joon (Gil)"
//                )
//
//                // 使用databaseDao的favoriteAlbum方法将上面创建的专辑对象添加到数据库
//                // 如果此专辑已存在，则将替换现有记录
//                databaseDao.favoriteAlbum(album)
//            }
//        }


    }
}