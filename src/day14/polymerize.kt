package day14

import common.readLines

fun main() {
    val input = readLines("day14")
    val start = input.first()
    val rules = input.drop(2)
        .map { it.split(" -> ").let { (segment, result) -> segment.let { segment[0] to segment[1] } to result.single() } }
        .toMap()


    fun CharSequence.longPolymerize(): CharSequence = buildString {
        val s = this@longPolymerize
        for (p in 0..s.length - 2) {
            append(s[p])
            append(rules[s[p] to s[p + 1]]!!)
        }
        append(s.last())
    }

    run {
        val result = (1..10).asSequence().scan(start as CharSequence) { acc, _ -> acc.longPolymerize() }
            .onEach { r -> println(r.groupingBy { it }.eachCount()) }
            .last()

        val counts = result.groupingBy { it }.eachCount().also(::println)
        val min = counts.values.minOrNull()!!
        val max = counts.values.maxOrNull()!!
        println(max - min)
    }


    fun <K> MutableMap<K, Long>.accumulate(key: K, value: Long) = apply { compute(key) { _, c -> (c ?: 0) + value }}

    fun Map<Pair<Char, Char>, Long>.compactPolymerize(): Map<Pair<Char, Char>, Long> = buildMap {
        for ((p, c) in this@compactPolymerize) {
            val i = rules[p]!!
            accumulate(p.first to i, c)
            accumulate(i to p.second, c)
        }
    }

    val lastChar = start.last()

    fun Map<Pair<Char, Char>, Long>.eachCount(): Map<Char, Long> =
        entries.groupingBy { it.key.first }
            .foldTo(mutableMapOf(), 0L) { acc, element -> acc + element.value }
            .accumulate(lastChar, 1L)

    run {
        val start2 = start.zipWithNext().groupingBy { it }.eachCount().mapValues { it.value.toLong() }

        val result = (1..40).asSequence().scan(start2) { acc, _ -> acc.compactPolymerize() }
            .onEach { r -> println(r.eachCount()) }
            .last()

        val counts = result.eachCount().also(::println)
        val min = counts.values.minOrNull()!!
        val max = counts.values.maxOrNull()!!
        println(max - min)
    }

}