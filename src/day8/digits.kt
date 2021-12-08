package day8

import common.permutations
import common.readLines

fun main() {
    val input = readLines("day8")

    val output = input.map { it.substringAfter(" | ").split(" ") }
    output.flatten().count { it.length in listOf(2, 3, 4, 7) }.let(::println)

    val digitPatterns = listOf(
        "abcefg", "cf", "acdeg", "acdfg", "bdcf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg"
    ).map { it.toSet() }.toSet()

    val permutations = "abcdefg".toList().permutations().toList()

    fun transform(value: String, permutation: List<Char>) = buildSet {
        value.forEach { c -> add(permutation[c - 'a']) }
    }
    val results = input.map { line ->
        val values = line.replace(" | ", " ").split(" ")
        for (p in permutations) {
            if (values.all { transform(it, p) in digitPatterns }) {
                val lineDigits = values.joinToString("") { digitPatterns.indexOf(transform(it, p)).toString() }
                println(lineDigits)
                return@map lineDigits.takeLast(4).toInt()
            }
        }
        error("Undecodable input: $line")
    }
    println(results.sum())
}