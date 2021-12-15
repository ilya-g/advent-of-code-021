package common.positionXY

data class Pos(val x: Int, val y: Int) {
    override fun toString() = "(x:$x, y:$y)"
}

fun Pos.adjacentHV(): List<Pos> = listOf(
    Pos(x - 1, y),
    Pos(x + 1, y),
    Pos(x, y - 1),
    Pos(x, y + 1),
)