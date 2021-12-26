package day24

import common.readLines

enum class OpCode {
    inp,
    add,
    mul,
    div,
    mod,
    eql
}
data class Instr(val opCode: OpCode, val args: List<String>) {
    override fun toString() = "$opCode ${args}"
}

fun parseInstr(s: String): Instr =
    Instr(OpCode.valueOf(s.substringBefore(" ")), s.split(" ").drop(1))

fun Long.toChars(): String = generateSequence(this) { it / 26 }.takeWhile { it > 0 }.map { 'a' + (it % 26).toInt() }.toList().reversed().joinToString("")

fun execute(input: CharSequence, program: List<Instr>, verbose: Boolean = false): Long {
    check(input.all { it in '1'..'9' })
    val regs = Array(4) { 0L }
    fun store(reg: String, value: Long) {
        regs[reg.single() - 'w'] = value
    }
    fun read(regOrValue: String): Long {
        val reg = regOrValue.first()
        return if (regOrValue.length == 1 && reg in 'w'..'z') regs[reg - 'w'] else regOrValue.toLong()
    }
    var inputPos = 0
    for (instr in program) {
        val (opCode, args) = instr
        if (verbose) if (opCode == OpCode.inp) println("input at $inputPos")
        if (verbose) print("$instr: ")
        when (opCode) {
            OpCode.inp -> store(args[0], input[inputPos++].digitToInt().toLong())
            OpCode.add -> store(args[0], read(args[0]) + read(args[1]))
            OpCode.mul ->  store(args[0], read(args[0]) * read(args[1]))
            OpCode.div ->  store(args[0], read(args[0]) / read(args[1]).also { require(it != 0L) })
            OpCode.mod -> store(args[0], read(args[0]).also { require(it >= 0) { "a: $it" } } % read(args[1]).also { require(it > 0)  { "b: $it" }  })
            OpCode.eql -> store(args[0], if (read(args[0]) == read(args[1])) 1 else 0)
        }
        if (verbose) println("${regs.contentToString()}\t${regs.last().toChars()}")
    }
    return regs.last()
}

fun staticAnalyzeNullifyingPairs(program: List<Instr>) = buildList {
    val posStack = mutableListOf<Int>()
    var pos = -1
    for ((opCode, args) in program) {
        if (opCode == OpCode.inp) {
            pos++
            posStack.add(pos)
        }
        if (opCode == OpCode.add && args[0] == "x" && args[1].toIntOrNull()?.let { it < 9 } == true) {
            add(posStack.removeLast() to posStack.removeLast())
        }
    }
    check(posStack.isEmpty())
}

fun dynamicAnalyzeNullifyingPairs(program: List<Instr>, base: String): List<Pair<Int, Int>> = buildList {
    check(execute(base, program) == 0L)
    val s = StringBuilder()
    nextPair@ for (p1 in base.indices) {
        if (this.any { it.second == p1 }) continue
        for (p2 in p1 + 1 until base.length) {
            s.clear().append(base)
            for (v1 in 1..9) {
                s[p1] = v1.digitToChar()
                if (s[p1] == base[p1]) continue
                for (v2 in 1..9) {
                    s[p2] = v2.digitToChar()
                    if (execute(s, program) == 0L) {
                        add(p1 to p2)
                        continue@nextPair
                    }
                }
            }
        }
    }
    sortBy { it.second }
}

fun <T, K : Comparable<K>> List<T>.allMinsBy(selector: (T) -> K): List<T> {
    val min = minOf(selector)
    return filter { selector(it) == min }
}


fun main() {
    val program = readLines("day24").map(::parseInstr) //.onEach(::println)

    // 94815972183576 - one carefully found valid model number
    val r = execute("94815972183576", program, verbose = true)
    check(r == 0L)

    fun results(base: String, pair: Pair<Int, Int>) = sequence {
        val s = StringBuilder(base)
        val (p1, p2) = pair
        for (v1 in 1..9) for (v2 in 1..9) {
            s[p1] = v1.digitToChar()
            s[p2] = v2.digitToChar()
            yield(s.toString() to execute(s, program, verbose = false))
        }
    }
    fun varySolutions(base: String, pair: Pair<Int, Int>) =
        results(base, pair).filter { it.second == 0L }


    fun randomSolution(start: String): String {
        fun Long.digitsBase26(): Int {
            var p = 0
            var v1 = this
            while (v1 > 0) { v1 /= 26; p++ }
            return p
        }
        val allPairs = start.indices.flatMap { p1 -> (p1 + 1 until start.length).map { p2 -> p1 to p2 } }
        var s = start
        do {
            val p = allPairs.random()
            val min = results(s, p).toList().allMinsBy { it.second.digitsBase26() }
                .apply { println("$size ${first()} - ${last()}") }
                .random()
            s = min.first
        } while (min.second != 0L)
        return s
    }

    val s1 = randomSolution(CharArray(14) { (1..9).random().digitToChar() }.concatToString())
    println("Random solution: $s1")
    check(execute(s1, program) == 0L)

    val staticPairs = staticAnalyzeNullifyingPairs(program)
    val pairs = dynamicAnalyzeNullifyingPairs(program, s1)
    println("Statically analyzed pairs:  $staticPairs")
    println("Dynamically analyzed pairs: $pairs")

    val start = "1".repeat(14)

    val solutions = pairs.fold(listOf(start to 0L)) { stage, pair ->
        stage.flatMap { results(it.first, pair) }.allMinsBy { it.second } //.also { println(it) }
    }.map { it.first }

    println(solutions.size)
    println(solutions.maxOrNull()!!)
    println(solutions.minOrNull()!!)

    val base = solutions.random()
    println("Finding maximum")
    pairs.fold(base) { stage, pair, ->
        varySolutions(stage, pair).maxOf { it.first }.also(::println)
    }

    println("Finding minimum")
    pairs.fold(base) { stage, pair, ->
        varySolutions(stage, pair).minOf { it.first }.also(::println)
    }
}

