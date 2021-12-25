package day25

import common.readLines

fun main() {
    val input = readLines("day25")

    val map = input.mapTo(mutableListOf()) { it.toCharArray() }

    fun MutableList<CharArray>.move(): Boolean {
        var source = this
        var target = this.mapTo(mutableListOf()) { it.copyOf() }
        var moved = false
        for ((y, r) in source.withIndex()) {
            for (x1 in r.indices) {
                val x2 = (x1 + 1) % r.size
                if (r[x1] == '>' && r[x2] == '.') {
                    target[y][x1] = '.'
                    target[y][x2] = '>'
                    moved = true
                }
            }
        }

        source = target
        target = this
        target.indices.forEach { i -> target[i] = source[i].copyOf() }

        for ((y1, r) in source.withIndex()) {
            val y2 = (y1 + 1) % size
            for (i in r.indices) {
                if (r[i] == 'v' && source[y2][i] == '.') {
                    target[y1][i] = '.'
                    target[y2][i] = 'v'
                    moved = true
                }
            }
        }
        return moved
    }

    map.forEach { println(it.concatToString()) }

    var steps = 0
    do { steps++ } while (map.move())

    println()
    map.forEach { println(it.concatToString()) }
    println(steps)

}