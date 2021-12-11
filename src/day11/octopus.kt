package day11

import common.readLines

fun main() {
    val initial = readLines("day11").map { it.map { it.digitToInt() }}
    initial.forEach(::println)

    val state = initial.map { it.toMutableList() }

    var totalFlashes = 0
    fun simulateDay(): Int {
        var dayFlashes = 0
        state.forEach { r ->
            r.indices.forEach { c ->
                r[c]++
            }
        }

        do {
            val before = dayFlashes
            state.indices.forEach { r ->
                state[r].indices.forEach { c ->
                    if (state[r][c] > 9) {
                        state[r][c] = 0
                        dayFlashes++
                        for (dr in (-1..1)) {
                            for (dc in (-1..1)) {
                                if (dr == 0 && dc == 0) continue
                                if (r + dr !in state.indices || c + dc !in state[r].indices) continue
                                if (state[r + dr][c + dc] > 0)
                                    state[r + dr][c + dc]++
                            }
                        }
                    }
                }
            }
        } while (dayFlashes > before)
        return dayFlashes
    }
    val total = state.size * state[0].size
    for (step in 1..1000) {
        val dayFlashes = simulateDay()
        totalFlashes += dayFlashes
        if (step == 100) println("Flashes after 100 steps: $totalFlashes")
        if (dayFlashes == total) {
            println("All flashed at $step")
            if (step > 100) break
        }
    }
}