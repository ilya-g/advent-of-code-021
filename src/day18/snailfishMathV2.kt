package day18.v2

import common.readLines
import kotlin.time.measureTime

typealias NumberTokens = MutableList<Any>
fun NumberTokens.join() = joinToString("")

fun main() {
    val input = readLines("day18").map { NumberTokens(it) }

    println((NumberTokens("[[[[4,3],4],4],[7,[[8,4],9]]]") + NumberTokens("[1,1]")).join())

    val testItems = (1..6).map { NumberTokens("[$it,$it]") }
    println(testItems.take(4).reduce { acc, pair -> acc + pair }.join())
    println(testItems.take(5).reduce { acc, pair -> acc + pair }.join())
    println(testItems.take(6).reduce { acc, pair -> acc + pair }.join())

    input.runningReduce { acc, e -> acc + e }
        //        .onEach(::println)
        .last().also { println(it.join()) }
        .magnitude().let(::println)

    measureTime {
        val sums = sequence {
            for (p1 in input) {
                for (p2 in input) {
                    if (p1 !== p2) yield(p1 + p2)
                }
            }
        }
        println(sums.maxOf { it.magnitude() })
    }.let(::println)

}

fun NumberTokens(s: String): NumberTokens =
    s.mapTo(mutableListOf()) { if (it.isDigit()) it.digitToInt() else it }

operator fun NumberTokens.plus(other: NumberTokens): NumberTokens = mutableListOf<Any>().apply {
    add('[')
    addAll(this@plus)
    add(',')
    addAll(other)
    add(']')
    reduce()
}

fun NumberTokens.reduce() {
    while (true) {
        if (explode()) continue
        if (split()) continue
        break
    }
}


fun NumberTokens.explode(): Boolean {
    var level = 0
    var i = 0
    while(i < this.size) {
        when (val t = this[i]) {
            ']' -> {
                check(level > 0)
                level--
            }
            '[' -> {
                level++
                if (level == 5) {
                    val pair = subList(i, i + 5)
                    val (_, v1, c, v2, br) = pair
                    check(c == ',')
                    check(br == ']')
                    pair.clear()
                    pair.add(0)
                    ((i - 1) downTo 0).firstOrNull { this[it] is Int }?.let { this[it] = this[it] as Int + v1 as Int }
                    (i + 1 until size).firstOrNull { this[it] is Int }?.let { this[it] = this[it] as Int + v2 as Int }
                    level--
                }
            }
        }
        i++
    }
    return false
}


fun NumberTokens.split(): Boolean {
    val i = this.indexOfFirst { it is Int && it >= 10 }
    if (i >= 0) {
        val v = this[i] as Int
        this[i] = '['
        this.addAll(i + 1, listOf(v / 2, ',', v - v / 2, ']'))
        return true
    }
    return false
}

fun magnitude(v1: Long, v2: Long) = 3 * v1 + 2 * v2

fun NumberTokens.magnitude(): Long {
    val argStack = mutableListOf<Long>()
    val opStack = mutableListOf<Char>()
    fun evalOp(op: Char) {
        check(op == ',')
        val arg2 = argStack.removeLast()
        val arg1 = argStack.removeLast()
        argStack.add(magnitude(arg1, arg2))
    }
    for (t in this) {
        when (t) {
            ',' -> {
                while (true) {
                    val op = opStack.lastOrNull()?.takeIf { it == ',' } ?: break
                    opStack.removeLast()
                    evalOp(op)
                }
                opStack.add(t as Char)
            }
            '[' -> opStack.add(t as Char)
            ']' -> while (true) {
                val op = opStack.removeLastOrNull()?.takeUnless { it == '[' } ?: break
                evalOp(op)
            }
            else -> argStack.add((t as Int).toLong())
        }
//        println("$argStack  //  ${opStack.joinToString("")}")
    }
    while (opStack.isNotEmpty()) evalOp(opStack.removeLast())
    return argStack.single()

}
