package com.example.ryangraphsudoku.common

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object ProductionDispatcherProvider : DispatcherProvider {
    override fun provideUIContext(): CoroutineContext = Dispatchers.Main

    override fun provideIOContext(): CoroutineContext = Dispatchers.IO
}