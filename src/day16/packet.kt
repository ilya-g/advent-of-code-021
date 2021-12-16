package day16

import common.readAll

sealed interface Packet {
    val version: Int
    data class Literal(override val version: Int, val value: Long) : Packet
    data class Operator(override val version: Int, val type: Int, val subpackets: List<Packet>) : Packet
}

class BitStream(val input: String) {
    private var cursor = 0
    private var buffer = 0
    private var bufferBits = 0
    val bitCursor get() = cursor * 4 - bufferBits

    fun readBits(bits: Int): Int {
        require(bits < 28)
        while (bufferBits < bits) {
            buffer = buffer.shl(4) or input[cursor++].digitToInt(16)
            bufferBits += 4
        }
        bufferBits -= bits
        return buffer.shr(bufferBits).and(1.shl(bits) - 1)
    }
    fun readVLE(): Long {
        var value = 0L
        var parts = 0
        do {
            val part = readBits(5)
            val last = part and 0b10000 == 0
            value = (value shl 4) or (part and 0b1111).toLong()
            parts++
        } while (!last)
        if (parts > Long.SIZE_BITS / 4) println("WARN: literal exceeds Long")
        return value
    }
}

fun parsePacket(input: String): Packet = parsePacket(BitStream(input))

fun parsePacket(input: BitStream): Packet {
    val version = input.readBits(3)
    val type = input.readBits(3)
    if (type == 4) {
        return Packet.Literal(version, input.readVLE())
    } else {
        val subpackets = buildList {
            val lengthType = input.readBits(1)
            if (lengthType == 0) {
                val length = input.readBits(15)
                val currentPos = input.bitCursor
                while (length > input.bitCursor - currentPos) {
                    add(parsePacket(input))
                }
                if (length < input.bitCursor - currentPos) error("sub-packets length is more than specified $length")
            } else {
                val packets = input.readBits(11)
                repeat(packets) {
                    add(parsePacket(input))
                }
            }
        }
        if (type >= 5) check(subpackets.size == 2)
        return Packet.Operator(version, type, subpackets)
    }
}

fun Packet.sumVersions(): Int = version + if (this is Packet.Operator) subpackets.sumOf { it.sumVersions() } else 0
fun Packet.evaluate(): Long = when (this) {
    is Packet.Literal -> value
    is Packet.Operator -> {
        val subvalues = subpackets.map { it.evaluate() }
        fun List<Long>.evalBool(f: (Long, Long) -> Boolean): Long = let { (a, b) -> if (f(a, b)) 1L else 0L }
        when (type) {
            0 -> subvalues.sum()
            1 -> subvalues.fold(1L, Long::times)
            2 -> subvalues.minOrNull()!!
            3 -> subvalues.maxOrNull()!!
            5 -> subvalues.evalBool { a, b -> a > b }
            6 -> subvalues.evalBool { a, b -> a < b }
            7 -> subvalues.evalBool { a, b -> a == b }
            else -> error("Unsupported operator $type")
        }
    }
}
fun main() {
    val input = readAll("day16")

    val top = parsePacket(input)

    println(top.sumVersions())
    println(top.evaluate())
}