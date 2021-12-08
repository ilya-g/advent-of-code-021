package common

fun <T> List<T>.permutations(prefix: List<T> = emptyList()): Sequence<List<T>> =
    when (size) {
        0 -> emptySequence()
        1 -> sequenceOf(prefix + this)
        else -> asSequence().flatMap { e -> (this - e).permutations(prefix + e) }
    }

