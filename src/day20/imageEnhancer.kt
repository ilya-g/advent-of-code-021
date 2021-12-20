package day20

import common.readLines

operator fun Char.times(n: Int) = CharArray(n) { this }.concatToString()

fun main() {
    val input = readLines("day20")

    val algorithm = input.first()
    var image = input.drop(2)
//    image.forEach(::println)

    var e = '.'

    for (n in 1..50) {
        // enlarge
        val enlargedSize = 2 + image.first().length + 2
        val empty = e * enlargedSize
        val enlargedImage = listOf(empty, empty) + image.map { e * 2 + it + e * 2 } + listOf(empty, empty)

        // enhance
        e = if (e == '.') algorithm.first() else algorithm.last()
        val enhancedImage = MutableList(enlargedImage.size) { e * enlargedSize }
        for (r in 1..enlargedImage.lastIndex - 1) {
            enhancedImage[r] = buildString {
                append(e)
                for (c in 1..enlargedImage[r].lastIndex - 1) {
                    val points = (r - 1..r + 1).joinToString("") { r -> enlargedImage[r].substring(c - 1, c + 2) }
                        .replace('#', '1')
                        .replace('.', '0')
                    val index = points.toInt(2)
                    append(algorithm[index])
                }
                append(e)
            }
        }

        // crop
        image = enhancedImage.dropWhile { it.all { c -> c == e } }.dropLastWhile { it.all { c -> c == e } }
        val colRange = image.minOf { it.indexOfFirst { c -> c != e } }..image.maxOf { it.indexOfLast { c -> c != e } }
        image = image.map { it.substring(colRange) }
//        image.forEach(::println)
        if (n == 2 || n == 50)
            println(image.sumOf { l -> l.count { it == '#' }})
    }
    image.forEach(::println)

}