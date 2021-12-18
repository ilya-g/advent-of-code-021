package day18

import common.readLines

sealed interface PN
data class P(val first: PN, val second: PN) : PN {
    override fun toString() = "[$first,$second]"
}
data class N(val value: Int) : PN {
    override fun toString() = value.toString()
}

operator fun PN.plus(other: PN): PN = P(this, other).reduce()

fun PN.reduce(): PN {
    var v = this //.also { println("i: $it")}
    while (true) {
        v = v.explode() // ?.also { println("e: $it")}
            ?: v.split() // ?.also { println("s: $it")}
            ?: break
    }
    return v //.also { println("r: $it") }
}


fun PN.explode(): PN? = explode(depth = 1)?.first

fun PN.explode(depth: Int): Triple<PN, Int, Int>? = when (this) {
    is N -> null
    is P -> {
        if (depth == 5) {
            Triple(N(0), (first as N).value, (second as N).value)
        } else {
            first.explode(depth + 1)?.let { (fe, v1, v2) -> Triple(P(fe, second.appendLeft(v2)), v1, 0) } ?:
            second.explode(depth + 1)?.let { (se, v1, v2) -> Triple(P(first.appendRight(v1), se), 0, v2) }
        }
    }
}

fun PN.appendRight(value: Int): PN = if (value == 0) this else when(this) {
    is N -> N(this.value + value)
    is P -> P(first, second.appendRight(value))
}
fun PN.appendLeft(value: Int): PN = if (value == 0) this else when(this) {
    is N -> N(this.value + value)
    is P -> P(first.appendLeft(value), second)
}

fun PN.split(): PN? = when(this) {
    is N ->
        if (value >= 10) P(N(value / 2), N(value - value / 2)) else null
    is P -> {
        first.split()?.let { P(it, second) } ?:
        second.split()?.let { P(first, it) }
    }
}

fun PN(s: String): PN {
    // from AoC2020/day18
    val argStack = mutableListOf<PN>()
    val opStack = mutableListOf<Char>()
    fun evalOp(op: Char) {
        check(op == ',')
        val arg2 = argStack.removeLast()
        val arg1 = argStack.removeLast()
        argStack.add(P(arg1, arg2))
    }
    for (t in s) {
        when (t) {
            ',' -> {
                while (true) {
                    val op = opStack.lastOrNull()?.takeIf { it == ',' } ?: break
                    opStack.removeLast()
                    evalOp(op)
                }
                opStack.add(t)
            }
            '[' -> opStack.add(t)
            ']' -> while (true) {
                val op = opStack.removeLastOrNull()?.takeUnless { it == '[' } ?: break
                evalOp(op)
            }
            else -> argStack.add(N(t.digitToInt()))
        }
//        println("$argStack  //  ${opStack.joinToString("")}")
    }
    while (opStack.isNotEmpty()) evalOp(opStack.removeLast())
    return argStack.single()
}

fun PN.magnitude(): Long = when (this) {
    is N -> value.toLong()
    is P -> 3 * first.magnitude() + 2 * second.magnitude()
}

fun main() {
    val input = readLines("day18").map { s ->
        PN(s).also { check(it.toString() == s) }
    }

    println(PN("[[[[4,3],4],4],[7,[[8,4],9]]]") + PN("[1,1]"))
    val testItems = (1..6).map { P(N(it), N(it)) as PN }
    println(testItems.take(4).reduce { acc, pair -> acc + pair })
    println(testItems.take(5).reduce { acc, pair -> acc + pair })
    println(testItems.take(6).reduce { acc, pair -> acc + pair })

    input.runningReduce { acc, pn -> acc + pn }
//        .onEach(::println)
        .last().magnitude().let(::println)

    val sums = sequence {
        for (p1 in input) {
            for (p2 in input) {
                if (p1 !== p2) yield(p1 + p2)
            }
        }
    }
    println(sums.maxOf { it.magnitude() })
}