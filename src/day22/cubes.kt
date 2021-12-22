package day22

import common.readLines

data class Pos3D(val x: Int, val y: Int, val z: Int)

val IntRange.end: Int get() = last + 1 // exclusive

class Cuboid(val xs: IntRange, val ys: IntRange, val zs: IntRange) {
    val volume: Long = (xs.end - xs.first).toLong() * (ys.end - ys.first) * (zs.end - zs.first)
    fun isEmpty() = xs.isEmpty() || ys.isEmpty() || zs.isEmpty()

    infix fun intersect(other: Cuboid): Cuboid? =
        Cuboid(
            maxOf(this.xs.first, other.xs.first)..minOf(this.xs.last, other.xs.last),
            maxOf(this.ys.first, other.ys.first)..minOf(this.ys.last, other.ys.last),
            maxOf(this.zs.first, other.zs.first)..minOf(this.zs.last, other.zs.last),
        ).takeUnless { it.isEmpty() }

    operator fun contains(other: Cuboid) =
        other.xs.first in xs && other.xs.last in xs &&
        other.ys.first in ys && other.ys.last in ys &&
        other.zs.first in zs && other.zs.last in zs

    infix fun union(other: Cuboid): List<Cuboid> {
        val i = intersect(other) ?: return listOf(this, other)
        val x1 = minOf(this.xs.first, other.xs.first)
        val x2 = i.xs.first
        val x3 = i.xs.end
        val x4 = maxOf(this.xs.end, other.xs.end)
        val y1 = minOf(this.ys.first, other.ys.first)
        val y2 = i.ys.first
        val y3 = i.ys.end
        val y4 = maxOf(this.ys.end, other.ys.end)
        val z1 = minOf(this.zs.first, other.zs.first)
        val z2 = i.zs.first
        val z3 = i.zs.end
        val z4 = maxOf(this.zs.end, other.zs.end)
        val union = buildList<Cuboid> {
            for ((xs, xe) in listOf(x1, x2, x3, x4).zipWithNext()) {
                for ((ys, ye) in listOf(y1, y2, y3, y4).zipWithNext()) {
                    for ((zs, ze) in listOf(z1, z2, z3, z4).zipWithNext()) {
                        add(Cuboid(xs until xe, ys until ye, zs until ze))
                    }
                }
            }
            removeAll { it.isEmpty() }
            retainAll { it in other || it in this@Cuboid }
            check(filter { it in this@Cuboid }.sumOf { it.volume } == this@Cuboid.volume)
            check(filter { it in other }.sumOf { it.volume } == other.volume)
            check(sumOf { it.volume } == this@Cuboid.volume + other.volume - i.volume)
        }
        return union
    }

}

fun main() {
    val input = readLines("day22")

    val numberRegex = Regex("-?\\d+")
    val instructions = input.map { s ->
        numberRegex.findAll(s).map { it.value.toInt() }.toList().let { c -> Cuboid(c[0]..c[1], c[2]..c[3], c[4]..c[5]) } to
        (s.substringBefore(' ') == "on")
    }

    val cells = mutableSetOf<Pos3D>()

    val r = -50..50

    for ((c, state) in instructions) {
        if (c.xs.first !in r && c.xs.last !in r) continue
        if (c.ys.first !in r && c.ys.last !in r) continue
        if (c.zs.first !in r && c.zs.last !in r) continue

        for (x in c.xs) for (y in c.ys) for (z in c.zs) {
            val p = Pos3D(x, y, z)
            if (state) cells.add(p) else cells.remove(p)
        }
    }
    println(cells.size)
    println()


    val cubes = ArrayDeque<Cuboid>()
    var counter = 0
    for ((c, state) in instructions) {
//        if (c.xs.first !in r && c.xs.last !in r) continue
//        if (c.ys.first !in r && c.ys.last !in r) continue
//        if (c.zs.first !in r && c.zs.last !in r) continue
        val cubesToAnalyze = ArrayDeque<Cuboid>()
        cubesToAnalyze.add(c)

        val nonIntersecting = mutableListOf<Cuboid>()
        outer@ while (cubesToAnalyze.isNotEmpty()) {
            val c1 = cubesToAnalyze.removeFirst()

            for (c0 in cubes.asReversed()) {
                if (c0.intersect(c1) != null) {
                    cubes.remove(c0)
                    val u = c0 union c1
                    val (uc1, u0) = u.partition { it in c1 }
                    val (i, u1) = uc1.partition { it in c0 }
                    cubes.addAll(u0)
                    if (state) nonIntersecting.addAll(i)
                    cubesToAnalyze.addAll(u1)
                    continue@outer
                }
            }
            if (state) nonIntersecting.add(c1)
        }
        cubes += nonIntersecting
//        println(cubes.sumOf { it.volume })
        counter++
        if (counter > 200 && counter % 40 == 0) println("Wait for it...")
    }

    println(cubes.sumOf { it.volume })
}