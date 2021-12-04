package day3

import common.readLines

fun main() {
    val values = readLines("day3")

    val gamma = commonBitsValue(values, strategy(moreOnes = '1', moreZeroes = '0', tie = '-'))
    val epsilon = commonBitsValue(values, strategy(moreOnes = '0', moreZeroes = '1', tie = '-'))
    println(gamma * epsilon)

    val oxy = commonBitsFiltered(values, strategy(moreOnes = '1', moreZeroes = '0', tie = '1'))
    val co2 = commonBitsFiltered(values, strategy(moreOnes = '0', moreZeroes = '1', tie = '0'))
    println(oxy * co2)
}

enum class Outcome { MoreOnes, MoreZeroes, Tie }
typealias OutcomeStrategy = (Outcome) -> Char
fun strategy(moreOnes: Char, moreZeroes: Char, tie: Char): OutcomeStrategy =
    listOf(moreOnes, moreZeroes, tie).let { choices -> { r: Outcome -> choices[r.ordinal] } }

fun List<String>.bitAt(position: Int): Outcome {
    val ones = count { it[position] == '1' }
    val zeroes = size - ones
    return when {
        ones > zeroes -> Outcome.MoreOnes
        ones < zeroes -> Outcome.MoreZeroes
        else -> Outcome.Tie
    }
}

fun commonBitsValue(values: List<String>, outcomeStrategy: OutcomeStrategy): Int = buildString {
    repeat(values.first().length) { pos ->
        append(values.bitAt(pos).let(outcomeStrategy))
    }
}.toInt(radix = 2)

fun commonBitsFiltered(values: List<String>, outcomeStrategy: OutcomeStrategy): Int {
    @Suppress("NAME_SHADOWING")
    val values = values.toMutableList()
    var pos = 0
    while (values.size > 1) {
        val criteriaBit = values.bitAt(pos).let(outcomeStrategy)
        values.retainAll { it[pos] == criteriaBit }
        pos++
    }
    return values.single().toInt(radix = 2)
}


