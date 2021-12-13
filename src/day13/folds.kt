package day13

import common.positionXY.Pos
import common.readLines

enum class Axis { x, y }
data class Fold(val axis: Axis, val line: Int)

fun main() {
    val input = readLines("day13").filter { it.isNotEmpty() }.partition { !it.startsWith("fold") }

    val positions = input.first.map { it.split(',').let { (x, y) -> Pos(x.toInt(), y.toInt()) }}.onEach(::println)
    val folds = input.second.map { it.substringAfter("fold along ").split('=').let { (axis, line) -> Fold(Axis.valueOf(axis), line.toInt()) }}.onEach(::println)

    fun List<Pos>.foldAlongX(x: Int) = map {
        when {
            it.x < x -> it
            it.x > x -> it.copy(x = x - (it.x - x))
            else -> error("no points on fold line")
        }
    }
    
    fun List<Pos>.foldAlongY(y: Int) = map {
        when {
            it.y < y -> it
            it.y > y -> it.copy(y = y - (it.y - y))
            else -> error("no points on fold line")
        }
    }

    fun List<Pos>.foldAlong(fold: Fold) = when(fold.axis) {
        Axis.x -> foldAlongX(fold.line)
        Axis.y -> foldAlongY(fold.line)
    }

    println(positions.foldAlong(folds.first()).distinct().count())

    val folded = folds.fold(positions, List<Pos>::foldAlong).toSet()
    val ys = 0..folded.maxOf { it.y }
    val xs = 0..folded.maxOf { it.x }
    for (y in ys) {
        println(xs.joinToString("") { x -> if (Pos(x, y) in folded) "###" else " . " })
    }

}