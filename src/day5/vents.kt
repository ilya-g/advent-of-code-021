package day5

import common.positionXY.Pos
import common.readLines
import kotlin.math.sign

data class Line(val start: Pos, val end: Pos) {
    private fun bounds(x1: Int, x2: Int) = minOf(x1, x2)..maxOf(x1, x2)
    fun points() = sequence {
        val xr = bounds(start.x, end.x)
        val yr = bounds(start.y, end.y)
        val xs = (end.x - start.x).sign
        val ys = (end.y - start.y).sign
        var p = start
        while (p.x in xr && p.y in yr) {
            yield(p)
            p = Pos(p.x + xs, p.y + ys)
        }
    }
}
fun Line(string: String): Line {
    fun pos(string: String) = string.split(',').let { (x, y) -> Pos(x.toInt(), y.toInt()) }
    val (s, e) = string.split(" -> ")
    return Line(pos(s), pos(e))
}

fun main() {
    val lines = readLines("day5").map(::Line)

    val hvLines = lines.filter { (s, e) -> s.x == e.x || s.y == e.y }
    val points1 = hvLines.asSequence().flatMap { it.points() }.groupingBy { it }.eachCount() //.onEach(::println)
    println(points1.count { it.value > 1 })

    val points2 = lines.asSequence().flatMap { it.points() }.groupingBy { it }.eachCount() //.onEach(::println)
    println(points2.count { it.value > 1 })
//    for (y in 0..15) {
//        for (x in 0..15) {
//            print(points2[Pos(x, y)]?.toString() ?: ".")
//        }
//        println()
//    }
}