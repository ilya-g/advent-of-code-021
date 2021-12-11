package day11

import common.readLines

class Octopus(private var energy: Int) {
    fun gainEnergy() { energy++ }
    fun gainEnergyFromNeighborFlash() { if (energy > 0) energy++ }
    fun flashed() = (energy > 9).also { if (it) energy = 0 }
    override fun toString() = "~($energy)~"
}

fun main() {
    val input = readLines("day11").map { row -> row.map { Octopus(it.digitToInt()) }}
    input.forEach(::println)
    val rows = input.indices
    val cols = input[0].indices
    val neighbors = buildMap {
        val dcs = (-1..1).flatMap { dr -> (-1..1).map { dc -> dr to dc }}.filterNot { (dr, dc) -> dr == 0 && dc == 0 }
        for (r in rows) {
            for (c in cols) {
                put(input[r][c], dcs.mapNotNull { (dr, dc) -> input.getOrNull(r + dr)?.getOrNull(c + dc) })
            }
        }
    }
    val all = neighbors.keys

    var totalFlashes = 0
    fun simulateDay(): Int {
        var dayFlashes = 0
        all.forEach { it.gainEnergy() }

        do {
            val before = dayFlashes
            all.forEach {
                if (it.flashed()) {
                    dayFlashes++
                    neighbors[it]!!.forEach { n -> n.gainEnergyFromNeighborFlash() }
                }
            }
        } while (dayFlashes > before)
        return dayFlashes
    }

    for (step in 1..1000) {
        val dayFlashes = simulateDay()
        totalFlashes += dayFlashes
        if (step == 100) println("Flashes after 100 steps: $totalFlashes")
        if (dayFlashes == all.size) {
            println("All flashed at $step")
            if (step > 100) break
        }
    }
}