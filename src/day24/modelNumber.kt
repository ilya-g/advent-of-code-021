package day24

import common.readLines
import java.lang.StringBuilder

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

fun execute(input: CharSequence, program: List<Instr>, verbose: Boolean = true): Long {
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

fun analyzeNullifyingPairs(program: List<Instr>) = buildList {
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
}.sortedBy { it.second }


fun main() {
    val program = readLines("day24").map(::parseInstr) //.onEach(::println)

    // 94815972183576 - one carefully found valid model number
    val r = execute("96819994293996", program)
    check(r == 0L)

    val pairs = analyzeNullifyingPairs(program)

    val base = "94815972183576"
    fun solutions(base: String, p1: Int, p2: Int) = sequence {
        val s = StringBuilder(base)
        for (v1 in 1..9) for (v2 in 1..9) {
            s[p1] = v1.digitToChar()
            s[p2] = v2.digitToChar()
            val result = execute(s, program, verbose = false)
            if (result == 0L)
                yield(s.toString() to result)
        }
    }


    println("Finding maximum")
    pairs.fold(base) { stage, (p1, p2), ->
        solutions(stage, p1, p2).maxOf { it.first }.also(::println)
    }

    println("Finding minimum")
    pairs.fold(base) { stage, (p1, p2), ->
        solutions(stage, p1, p2).minOf { it.first }.also(::println)
    }


}

