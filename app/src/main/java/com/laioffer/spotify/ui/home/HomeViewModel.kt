package com.laioffer.spotify.ui.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laioffer.spotify.datamodel.Section
import com.laioffer.spotify.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//: 代表extends
// 使用 @HiltViewModel 注解来告诉 Hilt 这是一个需要注入的 ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    // 使用 @Inject 注解来告诉 Hilt 我们需要一个 HomeRepository 的实例
    private val repository:HomeRepository
) : ViewModel() {
    // 定义一个 MutableStateFlow，用来存储和更新 UI 的状态
    private val _uiState = MutableStateFlow(HomeUiState(feed = emptyList(), isLoading = true))
    // 提供一个只读的 StateFlow，供外部观察 UI 的状态
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // 定义一个方法来获取首页的数据
    fun fetchHomeScreen() {
        // 使用 viewModelScope 来启动一个新的协程
        //main
        Log.d("Tim", Thread.currentThread().name + "1")
        viewModelScope.launch {
            // main
            Log.d("Tim", Thread.currentThread().name + "2")
            // 调用 repository 的方法来获取数据
            val sections = repository.getHomeSections()
            //main
            Log.d("Tim", Thread.currentThread().name + "3")
            // 更新 _uiState 的值
            _uiState.value = HomeUiState(feed = sections, isLoading = false)
            // 在日志中打印 _uiState 的当前值
            Log.d("HomeViewModel", _uiState.value.toString())
        }
        //main
        Log.d("Tim", Thread.currentThread().name + "5")
    }
}

// 定义一个数据类来存储 UI 的状态
// 使用 data 关键字来让 Kotlin 自动生成 equals()，hashCode() 和 toString() 方法
// 同时还会生成 copy() 方法，可以方便地复制对象并修改部分属性
data class HomeUiState(
    val feed: List<Section>,  // 用于存储首页的 sections 数据
    val isLoading:Boolean     // 用于表示是否正在加载数据
)
