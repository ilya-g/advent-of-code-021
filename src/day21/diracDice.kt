package day21

import kotlin.math.pow

data class GameState(val pos1: Int, val score1: Int = 0, val pos2: Int, val score2: Int = 0) {
    private fun advancePos(pos: Int, roll: Int) = ((pos + roll) - 1) % 10 + 1

    fun turn1(roll: Int): GameState {
        val pos = advancePos(pos1, roll)
        return copy(pos1 = pos, score1 = score1 + pos)
    }
    fun turn2(roll: Int): GameState {
        val pos = advancePos(pos2, roll)
        return copy(pos2 = pos, score2 = score2 + pos)
    }

    override fun toString() = "$score1:$score2 at $pos1,$pos2"
}

fun deterministicDie100() = generateSequence(1) { (it % 100) + 1 }

fun playDeterministic(initial: GameState): Int {
    val die = deterministicDie100().iterator()
    var rolls = 0
    fun roll() = (1..3).sumOf { die.next() }.also { rolls += 3 }

    var state = initial
    while(true) {
        state = state.turn1(roll())
        if (state.score1 >= 1000) return state.score2 * rolls

        state = state.turn2(roll())
        if (state.score2 >= 1000) return state.score1 * rolls
    }
}

fun diracDie(sides: Int, rolls: Int): Map<Int, Int> =
    (0 until sides.toDouble().pow(rolls).toInt()).groupingBy { v ->
        generateSequence(v) { it / sides }.take(rolls).sumOf { it % sides + 1 }
    }.eachCount()

fun playDirac(initial: GameState): Long {
    println("Starting game state: $initial")
    var states = mapOf(initial to 1L)
    val endingStates = mutableMapOf<GameState, Long>()
    fun <K> MutableMap<K, Long>.account(key: K, value: Long) { this[key] = (this[key] ?: 0) + value }

    val winScore = 21
    fun GameState.isGoing() = score1 < winScore && score2 < winScore

    val rolls = diracDie(3, 3) // .also(::println)

    fun advanceGame(turn: GameState.(roll: Int) -> GameState) {
        states = buildMap {
            for ((state, stateOutcomes) in states) {
                for ((roll, rollOutcomes) in rolls) {
                    val nextState = state.turn(roll)
                    val outcomes = stateOutcomes * rollOutcomes
                    if (nextState.isGoing()) {
                        this.account(nextState, outcomes)
                    } else {
                        endingStates.account(nextState, outcomes)
                    }
                }
            }
        }
    }

    while (states.isNotEmpty()) {
        advanceGame(GameState::turn1)
//        println(states.size)
//        println(score)
        advanceGame(GameState::turn2)
//        println(states.size)
//        println(score)
    }
    val play1Wins = endingStates.entries.filter { it.key.score1 > it.key.score2 }.sumOf { it.value }
    val play2Wins = endingStates.entries.filter { it.key.score1 < it.key.score2 }.sumOf { it.value }

    println("$play1Wins:$play2Wins")

    return maxOf(play1Wins, play2Wins)
}

fun main() {
    val testInitial = GameState(pos1 = 4, pos2 = 8)
    val realInitial = GameState(pos1 = 3, pos2 = 4)

    println(playDeterministic(testInitial))
    println(playDeterministic(realInitial))
    println(playDirac(testInitial))
    println(playDirac(realInitial))
}
