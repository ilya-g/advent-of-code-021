package day4

import common.readLines

class Board(val rows: List<List<Int>>) {
    private val columns = rows.first().indices
    fun hasWon(crossed: Set<Int>): Boolean =
        rows.any { r -> r.all { it in crossed } } ||
            columns.any { c -> rows.all { r -> r[c] in crossed } }

    fun noncrossedSum(crossed: Set<Int>): Int =
        rows.flatten().filterNot { it in crossed }.sum()

    fun printCrossed(crossed: Set<Int>) {
        rows.forEach { r -> println(r.joinToString { (if (it in crossed) "!" else "") + it }) }
    }
}

fun main() {
    val input = readLines("day4")
    val numbers = input.first().split(",").map { it.toInt() }
    val boards = input.drop(1)
        .chunked(6) { rs -> Board(rs.drop(1).map { it.trim().split(Regex("\\s+")).map { it.toInt() }}) }
        .toMutableSet()

    val crossed = mutableSetOf<Int>()
    val winnerScores = mutableListOf<Int>()
    for (drawn in numbers) {
        crossed.add(drawn)
        val roundWinners = boards.filter { it.hasWon(crossed) }
        winnerScores += roundWinners.map { drawn * it.noncrossedSum(crossed) }
        boards -= roundWinners
    }
    println("First winner: ${winnerScores.first()}")
    println("Last winner: ${winnerScores.last()}")
}