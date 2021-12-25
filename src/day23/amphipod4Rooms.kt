package day23

import common.readLines
import kotlin.math.absoluteValue
import kotlin.time.*

enum class Family {
    A, B, C, D;
    companion object {
        val entries = values().toList()
        val costs = entries.map { f -> (1..f.ordinal).fold(1) { acc, _ -> acc * 10 } }
        fun ofOrNull(char: Char): Family? = entries.getOrNull(char - 'A')
    }
}
val Family.cost get() = Family.costs[ordinal]


data class Room(val belongsTo: Family, val occupants: List<Family>) {
    val position get() = belongsTo.ordinal * 2 + 2
    fun topmostOccupant(): Family = occupants.last()
    fun allAre(family: Family) = occupants.all { it == family }
    fun with(occupant: Family) = copy(occupants = occupants + occupant)
    fun withoutTopmost() = copy(occupants = occupants.dropLast(1))
}


data class State(
    val rooms: List<Room>,
    val hallway: List<Family?>,
    val roomSize: Int
) {
    fun roomOf(family: Family): Room = rooms[family.ordinal]
    val Room.stepsToLeave get() = roomSize - occupants.size + 1
    val Room.stepsToEnter get() = roomSize - occupants.size
}


fun parseInput(input: List<String>): State {
    val hallway = input[1].drop(1).dropLast(1).map { Family.ofOrNull(it) }
    val roomSize = input.size - 3
    val rooms = Family.entries.map { f ->
        Room(f, List(roomSize) { d ->
            val y = 1 + roomSize - d
            val x = f.ordinal * 2 + 3
            Family.ofOrNull(input[y][x])
        }.filterNotNull())
    }
    return State(rooms, hallway, roomSize)
}

fun isValidHallwayIndex(pos: Int) = pos !in (2..8 step 2)

fun State.hallwayIsFreeBetween(start: Int, end: Int, excludeStart: Boolean): Boolean {
    val skip = if (excludeStart) 1 else 0
    val range = if (start < end) (start + skip)..end else end..(start - skip)
    return range.all { hallway[it] == null }
}


fun <T> List<T>.replaceAt(index: Int, value: T) = mapIndexed { i, v -> if (i == index) value else v }

fun State.next(): Sequence<Pair<State, Int>> = sequence {
    // from hallway to room
    for ((p, f) in hallway.withIndex()) {
        if (f == null) continue
        val target = roomOf(f)
        if (target.allAre(f) && hallwayIsFreeBetween(p, target.position, excludeStart = true)) {
            val cost = f.cost * ((p - target.position).absoluteValue + target.stepsToEnter)
            yield(copy(rooms = rooms.replaceAt(f.ordinal, target.with(f)), hallway = hallway.replaceAt(p, null)) to cost)
        }
    }
    for (rf in Family.entries) {
        val room = roomOf(rf)
        if (room.allAre(rf)) continue // empty or all in place
        val o = room.topmostOccupant()
        val target = roomOf(o)
        if (target.allAre(o) && hallwayIsFreeBetween(room.position, target.position, excludeStart = false)) {
            // from room to room
            val cost = o.cost * ((room.position - target.position).absoluteValue + (room.stepsToLeave + target.stepsToEnter))
            yield(copy(rooms = rooms.replaceAt(o.ordinal, target.with(o)).replaceAt(rf.ordinal, room.withoutTopmost())) to cost)
        } else {
            // from room to hallway
            val positions = (room.position downTo 0).takeWhile { hallway[it] == null }.filter { isValidHallwayIndex(it) } +
                    (room.position until hallway.size).takeWhile { hallway[it] == null }.filter { isValidHallwayIndex(it) }
            positions.forEach { p ->
                val cost = o.cost * ((room.position - p).absoluteValue + room.stepsToLeave)
                yield(copy(rooms = rooms.replaceAt(rf.ordinal, room.withoutTopmost()), hallway = hallway.replaceAt(p, o)) to cost)
            }
        }
    }
}

fun State.dump() {
    println("#".repeat(hallway.size + 2))
    println("#${hallway.joinToString("") { it?.name ?: "." }}#")
    for (n in roomSize - 1 downTo 0) {
        println(rooms.joinToString("#", prefix = "###", postfix = "###") { it.occupants.getOrElse(n) { "." }.toString() })
    }
    println("#".repeat(hallway.size + 2))
}

fun debugPath(initial: State, steps: List<Int>) {
    var current = initial
    var totalCost = 0
    for (v in steps) {
        val (next, cost) = current.next().elementAt(v)
        current = next
        totalCost += cost
        current.dump()
        println("$v: $cost")
        println()
    }
    current.next().forEachIndexed { i, n ->
        n.first.dump()
        println("$i: ${totalCost + n.second}")
    }
}

fun solve(initial: State, verbose: Boolean = false): Int {
    val solved = initial.copy(rooms = Family.entries.map { f -> Room(f, List(initial.roomSize) { f }) }, hallway = initial.hallway.map { null })

    val visited = mutableSetOf<State>()
    val frontier = mutableSetOf(initial to 0)
    while (frontier.isNotEmpty()) {
        val pair = frontier.minByOrNull { it.second }!!
        frontier.remove(pair)
        val (node, cost) = pair

        if (verbose) {
            node.dump()
            println(cost)
        }

        if (node == solved) {
            println("Visited states: ${visited.size}")
            return cost
        }

        if (!visited.add(node)) continue
        for ((n, c) in node.next()) {
            frontier.add(n to cost + c)
        }
    }
    error("Search exhausted, no solution found")
}

fun main() {
    val input = readLines("day23")

    val initial = parseInput(input)

//    debugPath(initial, listOf(15, 2, 2, 0, 0, 0, 0, 0, 0))
//    return

    initial.dump()
    measureTime {
        println(solve(initial))
    }.let(::println)

    val input2 = input.toMutableList().apply {
        add(3, "  #D#C#B#A#")
        add(4, "  #D#B#A#C#")
    }
    val initial2 = parseInput(input2)
    initial2.dump()
    measureTime {
        println(solve(initial2))
    }.let(::println)
}
