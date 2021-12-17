package com.example.ryangraphsudoku.common

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object TestDispatcherProvider : DispatcherProvider {
    override fun provideUIContext(): CoroutineContext = Dispatchers.Unconfined

    override fun provideIOContext(): CoroutineContext = Dispatchers.Unconfined
}