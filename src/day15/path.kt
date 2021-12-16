package day15

import common.positionXY.Pos
import common.positionXY.adjacentHV
import common.readLines
import kotlin.time.measureTimedValue

operator fun <T> List<List<T>>.get(pos: Pos) = this[pos.y][pos.x]
operator fun <T> List<MutableList<T>>.set(pos: Pos, value: T) { this[pos.y][pos.x] = value }

fun main() {
    val input = readLines("day15").map { line -> line.map { it.digitToInt() }}

    val tileYSize = input.size
    val tileXSize = input[0].size

    fun solve(multiplier: Int, printTotals: Boolean = false, drawPath: Boolean = false): Int {
        val ys = 0 until tileYSize * multiplier
        val xs = 0 until tileXSize * multiplier
        val start = Pos(0, 0)
        val end = Pos(xs.last, ys.last)

        fun at(p: Pos): Int {
            val quad = p.y / tileYSize + p.x / tileXSize
            return ((input[p.y % tileYSize][p.x % tileXSize] + quad) - 1) % 9 + 1
        }

        fun Pos.adjacent(): List<Pos> = adjacentHV().filter { it.x in xs && it.y in ys }

        val total = ys.map { xs.mapTo(mutableListOf()) { 0 }}
        val defer = mutableSetOf<Pos>()
        for (y in ys) {
            for (x in xs) {
                val pos = Pos(x, y)
                val adjacent = pos.adjacent()
                val v = at(pos) + (adjacent.map { total[it] }.filter { it > 0 }.minOrNull() ?: 0)
                total[pos] = v
                if (adjacent.any { total[it] > at(it) + v }) defer.add(pos)
            }
        }

        while (defer.isNotEmpty()) {
            val pos = defer.first().also { defer.remove(it) }
            val v = total[pos]
            pos.adjacent().forEach {
                if (total[it] > at(it) + v) {
                    total[it] = at(it) + v
                    defer.add(it)
                }
            }
        }

        if (printTotals) {
            for (y in ys) {
                for (x in xs) print("${total[y][x]}\t")
                println()
            }
        }

        val result = total[end] - at(start)

        fun findPathBack(start: Pos, end: Pos): List<Pos> = buildList {
            var cur = start
            while (cur != end) {
                add(cur)
                cur = cur.adjacent().minByOrNull { total[it] }!!
            }
        }

        val path = findPathBack(end, start).reversed().toSet()

        if (drawPath) {
            println(path)
            for (y in ys) {
                for (x in xs) {
                    val pos = Pos(x, y)
                    val c = if (pos in path) "." else " "
                    print("$c${at(pos)}$c")
                }
                println()
            }
        }
        check(result == path.sumOf { at(it) })
//        println(path.sumOf { at(it) })
        return result
    }

    val result1 = solve(1, drawPath = true, printTotals = true)
    val result5 = measureTimedValue {  solve(5) }.let {
        println(it.duration)
        it.value
    }

    println("Part 1: $result1")
    println("Part 2: $result5")
}