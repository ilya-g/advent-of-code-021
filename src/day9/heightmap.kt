package day9

import common.positionXY.Pos
import common.positionXY.adjacentHV
import common.readLines

fun main() {
    val input = readLines("day9").map { line -> line.map { it.digitToInt() }}

    val ys = input.indices
    val xs = input[0].indices
    val positions: List<Pos> = ys.flatMap { y -> xs.map { x -> Pos(x, y) } }

    fun at(p: Pos): Int = input[p.y][p.x]

    fun Pos.adjacent(): List<Pos> = adjacentHV().filter { it.x in xs && it.y in ys }

    positions.sumOf { p ->
        val c = at(p)
        if (p.adjacent().all { c < at(it) }) c + 1 else 0
    }.let(::println)


    val visited = mutableSetOf<Pos>()
    val basinSizes = mutableListOf<Int>()
    for (p0 in positions) {
        if (!visited.add(p0) || at(p0) == 9) continue
        var currentSize = 1
        val remaining = ArrayDeque(p0.adjacent())
        while (remaining.isNotEmpty()) {
            val p = remaining.removeFirst()
            if (!visited.add(p) || at(p) == 9) continue
            currentSize += 1
            remaining.addAll(p.adjacent().filter { it !in visited })
        }
        basinSizes.add(currentSize)
    }
    basinSizes.also(::println)
        .sortedDescending().take(3).also(::println)
        .fold(1L) { acc, e -> acc * e }.let(::println)
}