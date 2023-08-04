package com.laioffer.spotify

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


// 使用 @HiltAndroidApp 注解标记应用。这个注解用来初始化 Hilt 依赖注入框架。
//标记了你的应用作为Hilt的依赖注入的开始点。
@HiltAndroidApp
class MainApplication : Application() {
    // 这个类通常用来初始化一些应用级别的资源，如外部库和单例对象等。
    // Hilt 已经自动完成了大部分工作，所以我们不需要在这个类中写太多代码。
    // 请确保在 AndroidManifest.xml 文件中将这个 Application 类设置为你的应用的 application 类。
}