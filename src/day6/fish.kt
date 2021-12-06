package day6

import common.readAll

fun main() {
    val fishDays = readAll("day6").split(',').map { it.toInt() }

    val fishByDay = List(9) { day -> fishDays.count { it == day }.toLong() }.let(::ArrayDeque)
    check(fishByDay.sum() == fishDays.size.toLong())

    fun simulateDay(fishByDay: ArrayDeque<Long>) {
        val zeroDayFishes = fishByDay.removeFirst()
        fishByDay[6] += zeroDayFishes // cycle
        fishByDay.addLast(zeroDayFishes) // offspring
    }

    repeat(80) { simulateDay(fishByDay) }
    println(fishByDay.sum())

    repeat(256 - 80) { simulateDay(fishByDay) }
    println(fishByDay.sum())
}