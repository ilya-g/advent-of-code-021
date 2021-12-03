package common
import kotlin.io.path.*

fun readLines(dayName: String) = Path("src/$dayName/input.txt").readLines()
fun readAll(dayName: String) = Path("src/$dayName/input.txt").readText()