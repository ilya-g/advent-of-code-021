package day19

import common.permutations
import common.readAll
import kotlin.math.absoluteValue
import kotlin.time.measureTime

data class Pos3D(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String = "$x,$y,$z"
}

operator fun Pos3D.minus(other: Pos3D) = Pos3D(
    this.x - other.x,
    this.y - other.y,
    this.z - other.z,
)
operator fun Pos3D.plus(other: Pos3D) = Pos3D(
    this.x + other.x,
    this.y + other.y,
    this.z + other.z,
)
fun Pos3D.distance() = x.absoluteValue + y.absoluteValue + z.absoluteValue


data class RotationMatrix(val c: List<List<Int>>) {
    init {
        require(c.size == 3 && c.all { it.size == 3 })
    }
    override fun toString(): String = c.joinToString("\n")
}

fun dotProduct(line: List<Int>, p: Pos3D): Int = line[0] * p.x + line[1] * p.y + line[2] * p.z
fun scalarProduct(line: List<Int>, s: Int) = line.map { it * s }

operator fun RotationMatrix.times(p: Pos3D): Pos3D = Pos3D(
    dotProduct(c[0], p),
    dotProduct(c[1], p),
    dotProduct(c[2], p),
)

val rotations = buildList {
    val unitRows = listOf(
        1, 0, 0,
        0, 1, 0,
        0, 0, 1,
    ).chunked(3)

    val signs = (0..7).map { List(3) { n -> if (it and (1 shl n) != 0) -1 else 1 } }
    for (rows in unitRows.permutations()) {
        for (s in signs) {
            add(RotationMatrix(rows.zip(s) { r, rs -> scalarProduct(r, rs) }))
        }
    }
}.filter { it.det() == 1 }

fun RotationMatrix.det() = (0..2).sumOf { c0 ->
    fun det(a: Int, b: Int, c: Int, d: Int) = a * d - c * b
    val c1 = (c0 + 1) % 3
    val c2 = (c0 + 2) % 3
    c[0][c0] * det(c[1][c1], c[1][c2], c[2][c1], c[2][c2])
}

fun distanceSet(s: Collection<Pos3D>): Set<Int> = buildSet {
    for ((i1, s1) in s.withIndex()) {
        for ((i2, s2) in s.withIndex()) {
            if (i1 != i2)
                add((s2 - s1).distance())
        }
    }
}

fun findOrientationAndOrigin(s0: Set<Pos3D>, s1: Set<Pos3D>): Pair<RotationMatrix, Pos3D>? {
    val ds0 = s0.sortedByDescending { it.distance() }.map { p0 -> s0.map { it - p0 }.let { ds -> Triple(p0, ds, ds.map { it.distance() }.toSet()) } }
    val ds1 = s1.sortedByDescending { it.distance() }.map { p1 -> s1.map { it - p1 }.let { ds -> Triple(p1, ds, ds.map { it.distance() }.toSet()) } }
    for ((p0, dps0, dist0) in ds0) {
        for ((p1, dps1, dist1) in ds1) {
            val commonDistances = dist0 intersect dist1
            if (commonDistances.size < 12) continue
            for (r in rotations) {
                val dps1r = dps1.map { r * it }.toSet()
                val common = dps0 intersect dps1r
                if (common.size >= 12) {
//                    common.forEach { println((p0 + it).distance()) }
                    return r to (p0 - r * p1)
                }
            }
        }
    }
    return null
}

data class Region(val scanners: List<Pos3D>, val beacons: Set<Pos3D>, val distances: Set<Int> = distanceSet(beacons), val oriented: Boolean = false)

fun main() = measureTime {
    val regions = readAll("day19").split("\n\n", "\r\n\r\n")
        .map { s -> Region(listOf(Pos3D(0,0,0)),
            s.lines().drop(1).map {
                it.split(",").map(String::toInt).let { (x, y, z) -> Pos3D(x, y, z) }
            }.toSet())
        }.toMutableList()


    outer@ while (regions.size > 1) {
        println(regions.map { it.beacons.size })
        for ((i0, reg0) in regions.withIndex()) {
            for (i1 in i0 + 1 until regions.size) {
                if (i0 == i1) continue
                val reg1 = regions[i1]
                if ((reg0.distances intersect reg1.distances).size < 66) continue
                val (r, o1) = findOrientationAndOrigin(reg0.beacons, reg1.beacons) ?: continue
                val combined = Region(
                    beacons = reg0.beacons union reg1.beacons.map { p -> r * p + o1 },
                    scanners = reg0.scanners + reg1.scanners.map { p -> r * p + o1 },
                    distances = reg0.distances union reg1.distances
                )
                regions.removeAt(i1)
                regions.removeAt(i0)
                regions.add(combined)
                continue@outer
            }
        }
    }

    val (scanners, beacons) = regions.single()
    println("Scanner locations: $scanners")

    println("Total beacons: ${beacons.size}")
    val maxDistance = scanners.flatMap { s1 -> scanners.map { s2 -> s2 - s1 } }.maxOf { it.distance() }
    println("Max scanner distance: $maxDistance")


//    println(rotations.size)
//    rotations.forEach { println("$it\n") }

//    val points = listOf(
//        Pos3D(5,6,-4),
//        Pos3D(8,0,7),
//    )

//    rotations.map { r -> r * points[0] }.distinct().let { println(it.size) }
//    for (r in rotations) {
//        for (p in points) {
//            println(r * p)
//        }
//        println()
//    }
}.let { println(it) }