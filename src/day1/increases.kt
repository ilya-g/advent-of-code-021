package day1

import kotlin.io.path.*

fun main() {
    val items = Path("src/day1/input.txt").readLines().map { it.toInt() }

    fun Iterable<Int>.countIncreases() = zipWithNext().count { (a, b) -> a < b }
    
    println(items.countIncreases())
    println(items.windowed(3).map { it.sum() }.countIncreases())
}