package day12

import common.readLines

fun main() {
    val input = readLines("day12")

    val endpoints = input.flatMap { it.split("-").let { (a, b) -> listOf(a to b, b to a) } }
        .groupBy(keySelector = { it.first }, valueTransform = { it.second })

    println(endpoints)

    fun paths(prefix: List<String>, visitedTwice: String?): Sequence<List<String>> = sequence {
        val last = prefix.last()
        if (last == "end") {
            yield(prefix)
            return@sequence
        }
        val next = endpoints[last].orEmpty()
        for (n in next) {
            if (n == "start") continue
            if (n.all { it.isLowerCase() } && prefix.contains(n)) {
                if (visitedTwice != null) continue
                yieldAll(paths(prefix + n, n))
            } else {
                yieldAll(paths(prefix + n, visitedTwice))
            }
        }
    }

    paths(listOf("start"), "no-visit-twice")
        .onEach(::println)
        .count().let(::println)


    paths(listOf("start"), null)
//        .onEach(::println)
        .count().let(::println)
}