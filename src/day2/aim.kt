package day2

import common.readLines

enum class Command {
    forward, down, up
}

fun main() {
    val commands = readLines("day2")
        .map { it.split(' ').let { (a, b) -> Command.valueOf(a) to b.toInt() } }

    run part1@ {
        var x = 0
        var depth = 0
        for ((command, value) in commands) {
            when (command) {
                Command.forward -> x += value
                Command.down -> depth += value
                Command.up -> depth -= value
            }
        }
        println("x: $x, depth: $depth; ${x * depth}")
    }

    run part2@ {
        var x = 0
        var depth = 0
        var aim = 0
        for ((command, value) in commands) {
            when (command) {
                Command.forward -> {
                    x += value
                    depth += aim * value
                }
                Command.down -> aim += value
                Command.up -> aim -= value
            }
        }
        println("x: $x, depth: $depth; ${x * depth}")
    }
}