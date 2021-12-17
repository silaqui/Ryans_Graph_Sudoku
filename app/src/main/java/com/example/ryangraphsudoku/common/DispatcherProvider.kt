package com.example.ryangraphsudoku.common

import kotlin.coroutines.CoroutineContext

interface DispatcherProvider {

    fun provideUIContext(): CoroutineContext
    fun provideIOContext(): CoroutineContext

}