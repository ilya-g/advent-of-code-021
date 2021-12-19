package day19

import common.permutations
import common.readAll
import kotlin.math.absoluteValue

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
    // TODO: find how to exclude mirroring matrices
    val signs = (0..7).map { List(3) { n -> if (it and (1 shl n) != 0) -1 else 1 } }
    for (rows in unitRows.permutations()) {
        for (s in signs) {
            add(RotationMatrix(rows.zip(s) { r, rs -> scalarProduct(r, rs) }))
        }
    }
}
//fun RotationMatrix.isMirror() = c.map { it.sum() }.count { it < 0 } % 2 == 1


fun findOrientationAndOrigin(s0: Set<Pos3D>, s1: Set<Pos3D>): Pair<RotationMatrix, Pos3D>? = sequence {
    val ds0 = s0.map { p0 -> p0 to s0.map { it - p0 } }
    val ds1 = s1.map { p1 -> p1 to s1.map { it - p1 } }
    for (r in rotations) {
        val ds1r = ds1.map { (p, dps) -> p to dps.map { r * it }.toSet() }
        for ((p0, dps0) in ds0) {
            for ((p1, dps1r) in ds1r) {
                val common = dps0 intersect dps1r
                if (common.size >= 12) {
//                        both.forEach { println(p0 + it) }
                    val p00 = p0 + common.first()
                    val p10 = r * p1 + common.first()
                    yield(r to (p00 - p10))
                }
            }
        }
    }
}.firstOrNull()


fun main() {
    val scans = readAll("day19").split("\n\n", "\r\n\r\n")
        .map { s -> listOf(Pos3D(0,0,0)) to
            s.lines().drop(1).map {
                it.split(",").map(String::toInt).let { (x, y, z) -> Pos3D(x, y, z) }
            }.toSet()
        }.toMutableList()



    outer@ while (scans.size > 1) {
        println(scans.map { it.second.size })
        for (i0 in scans.indices) {
            val s0 = scans[i0].second
            for (i1 in i0 + 1 until scans.size) {
                val s1 = scans[i1].second
                if (i0 == i1) continue
                val (r, o1) = findOrientationAndOrigin(s0, s1) ?: continue
                val union = s0 union s1.map { p -> r * p + o1 }
                val scanPos = scans[i0].first + scans[i1].first.map { p -> r * p + o1 }
                scans.removeAt(i1)
                scans.removeAt(i0)
                scans.add(scanPos to union)
                continue@outer
            }
        }
    }

    val (scanners, points) = scans.single()
    println("Scanner locations: $scanners")

    println("Total beacons: ${points.size}")
    val maxDistance = scanners.flatMap { s1 -> scanners.map { s2 -> s2 - s1 } }.maxOf { it.distance() }
    println("Max scanner distance: $maxDistance")


//    println(rotations.size)
//    rotations.forEach { println("${it.isMirror()}\n$it\n") }

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
}