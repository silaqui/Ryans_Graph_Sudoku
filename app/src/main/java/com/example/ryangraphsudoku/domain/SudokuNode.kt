package com.example.ryangraphsudoku.domain

import java.io.Serializable

data class SudokuNode(
    val x: Int,
    val y: Int,
    var color: Int = 0,
    val readOnly: Boolean = true,
) : Serializable {
    override fun hashCode(): Int = getHash(x, y)
}

internal fun getHash(x: Int, y: Int): Int {
    val newX = x * 100
    return "$newX$y".toInt()
}