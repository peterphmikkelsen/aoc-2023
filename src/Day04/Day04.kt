import kotlin.math.pow

fun main() {

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val card = it.removePrefix("Card\\s+\\d+:\\s".toRegex()).getCard()
            points(card)
        }
    }
    
    fun part2(input: List<String>): Int {
        val cards = mutableListOf<Card>()
        input.forEachIndexed { idx, line ->
            val card = line.removePrefix("Card\\s+\\d+:\\s".toRegex()).getCard(idx)
            cards.add(card)
        }
        return calculateWins(cards).values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

fun calculateWins(cards: List<Card>): Map<Card, Int> {
    val initialWins = calculateInitialWins(cards)
    val nbOfEachCard = cards.associateWith { 0 }.toMutableMap()
    recursiveWins(cards, nbOfEachCard, initialWins)
    return nbOfEachCard
}

fun calculateInitialWins(cards: List<Card>): Map<Card, List<Card>> {
    val initialCopies = mutableMapOf<Card, List<Card>>()
    for (i in cards.indices) {
        val currentWins = mutableListOf<Card>()
        val currentCard = cards[i]
        val nbOfCards = currentCard.winning.intersect(currentCard.given).size
        for (j in i + 1 until i + 1 + nbOfCards) {
            currentWins.add(cards[j])
        }
        initialCopies[currentCard] = currentWins
    }
    return initialCopies
}

fun recursiveWins(cards: List<Card>, nbOfEachCard: MutableMap<Card, Int>, map: Map<Card, List<Card>>) {
    cards.forEach {
        nbOfEachCard[it] = nbOfEachCard[it]!! + 1
        recursiveWins(map[it]!!, nbOfEachCard, map)
    }
}

fun points(card: Card): Int {
    val intersection = card.winning.intersect(card.given)
    if (intersection.isEmpty()) return 0
    return 2.0.pow(intersection.size - 1).toInt()
}

fun String.getCard(idx: Int = 0): Card {
    val (a, b) = this.split(" | ").map { card -> card.split("\\s+".toRegex()).filter { it.isNotBlank() }.map { it.toInt() } }
    return Card(idx + 1, a, b)
}

data class Card(val number: Int = 0, val winning: List<Int>, val given: List<Int>) {
    override fun toString(): String {
        return "Card $number"
    }
}
