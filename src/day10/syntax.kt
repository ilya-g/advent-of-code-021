package day10

import common.readLines

data class SyntaxPair(val open: Char, val close: Char, val points: Int)
val pairs = listOf(
    SyntaxPair('(', ')', 3),
    SyntaxPair('[', ']', 57),
    SyntaxPair('{', '}', 1197),
    SyntaxPair('<', '>', 25137),
)

fun main() {
    val input = readLines("day10")


    fun String.corruptedPoints(): Int {
        val stack = ArrayDeque<SyntaxPair>()
        for (c in this) {
            val open = pairs.firstOrNull { it.open == c }
            if (open != null) { stack.addLast(open) } else {
                val close = stack.removeLastOrNull()
                if (close?.close != c) {
                    println("illegal $c in $this")
                    return pairs.single { it.close == c }.points
                }
            }
        }
        return 0
    }

    fun String.incompletePoints(): Long? {
        val stack = ArrayDeque<SyntaxPair>()
        for (c in this) {
            val open = pairs.firstOrNull { it.open == c }
            if (open != null) { stack.addLast(open) } else {
                val close = stack.removeLastOrNull()
                if (close?.close != c) {
//                    println("illegal $c in $this")
                    return null
                }
            }
        }
        return stack.toList().asReversed().fold(0L) { acc, pair -> acc * 5 + (1 + pairs.indexOf(pair)) }
            .also {
                println("line $this is completed with ${stack.toList().asReversed().joinToString("") { it.close.toString() }} worth $it points")
            }
    }

    println(input.sumOf { it.corruptedPoints() })
    println(input.mapNotNull { it.incompletePoints() }.sorted().let { rank -> rank[rank.size / 2]})
}