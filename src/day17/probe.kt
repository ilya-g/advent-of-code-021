package day17

import common.positionXY.Pos
import common.readLines
import kotlin.math.*

typealias Velocity = Pos
data class Target(val xrange: IntRange, val yrange: IntRange)

val targetRegex = Regex("""target area: x=(-?\d+)..(-?\d+), y=(-?\d+)..(-?\d+)""")
fun parseTarget(input: String): Target =
    targetRegex.matchEntire(input)!!.destructured
        .let { (x1, x2, y1, y2) -> Target(x1.toInt()..x2.toInt(), y1.toInt()..y2.toInt()) }

fun maxHeightIfHitsTarget(target: Target, initial: Velocity): Int? {
    var p = Pos(0, 0)
    var v = initial
    var maxY = Int.MIN_VALUE
    while (p.x < target.xrange.last && p.y > target.yrange.first) {
        p = Pos(p.x + v.x, p.y + v.y)
        v = v.run { copy(x = x - x.sign, y = y - 1) }
        maxY = maxOf(maxY, p.y)
        if (p.x in target.xrange && p.y in target.yrange) return maxY
    }
    return null
}

fun hitVelocities(target: Target): Sequence<Pair<Velocity, Int>> = sequence {
    // x_min = (1..vx).sum() = (vx)*(vx-1)/2 => vx_min ~= sqrt(2*x_min)
    val vxMin = floor(sqrt(2.0 * target.xrange.first)).toInt()
    val vxMax = target.xrange.last

    for (x in vxMin..vxMax) {
        for (y in target.yrange.first..1000) {
            val v = Velocity(x, y)
            maxHeightIfHitsTarget(target, v)?.let { maxH -> yield(v to maxH) }
        }
    }
}

fun main() {
    val targets = readLines("day17").map(::parseTarget)

    for (t in targets) {
        println(t)
        val vs = hitVelocities(t).toList() //.onEach(::println)
        vs.maxByOrNull { (_, h) -> h }.let(::println)
        vs.count().let(::println)
    }
}