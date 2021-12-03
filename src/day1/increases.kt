package day1

import common.readLines

fun main() {
    val items = readLines("day1").map { it.toInt() }

    fun Iterable<Int>.countIncreases() = zipWithNext().count { (a, b) -> a < b }
    
    println(items.countIncreases())
    println(items.windowed(3).map { it.sum() }.countIncreases())
}