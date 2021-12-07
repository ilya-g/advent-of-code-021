package day7

import common.readAll
import kotlin.math.absoluteValue

fun main() {
    val positions = readAll("day7").split(',').map { it.toInt() }

    val min = positions.minOrNull()!!
    val max = positions.maxOrNull()!!

    (min..max).minOf { p -> positions.sumOf { (it - p).absoluteValue }}.let(::println)

    (min..max).minOf { p -> positions.sumOf { (1..(it - p).absoluteValue).fastSum() }}.let(::println)
    (min..max).minOf { p -> positions.sumOf { (1..(it - p).absoluteValue).sum() }}.let(::println)
}

fun IntRange.fastSum(): Long = if (isEmpty()) 0 else (endInclusive + start).toLong() * (endInclusive - start + 1) / 2