package day10

import common.readLines

data class SyntaxPair(val open: Char, val close: Char, val corruptedPoints: Int, val incompletePoints: Int)
val pairs = listOf(
    SyntaxPair('(', ')', 3, 1),
    SyntaxPair('[', ']', 57, 2),
    SyntaxPair('{', '}', 1197, 3),
    SyntaxPair('<', '>', 25137, 4),
)
sealed interface SyntaxError
object None : SyntaxError {
    override fun toString() = "None"
}
class Corrupted(val invalidPair: SyntaxPair) : SyntaxError {
    override fun toString() = "Illegal closing char ${invalidPair.close} worth ${invalidPair.corruptedPoints}"
}
class Incomplete(val remaining: List<SyntaxPair>) : SyntaxError {
    val totalPoints = remaining.fold(0L) { acc, pair -> acc * 5 + pair.incompletePoints }
    override fun toString() = "missing ${remaining.joinToString("") { it.close.toString() }} worth $totalPoints points"
}

fun main() {
    val input = readLines("day10")

    fun String.error(): SyntaxError {
        val stack = mutableListOf<SyntaxPair>()
        for (c in this) {
            val open = pairs.singleOrNull { it.open == c }
            if (open != null) { stack.add(open) } else {
                val close = stack.removeLastOrNull()
                if (close?.close != c) {
                    return Corrupted(pairs.single { it.close == c })
                }
            }
        }
        return if (stack.isEmpty()) None else Incomplete(stack.asReversed())
    }

    val inputErrors = input.map { line -> line.error().also {
        println("line $line, problem: $it")
    } }

    println(inputErrors.filterIsInstance<Corrupted>().sumOf { it.invalidPair.corruptedPoints })
    println(inputErrors.filterIsInstance<Incomplete>().map { it.totalPoints }
        .sorted().let { rank -> rank[rank.size / 2]})
}