# Advent of Code 2023 - Day 4: Scratchcards
Here is a description of the solution to part 1 and 2 of Advent of Code 2023 - Day 4: Scratchcards

## Modeling a Card
A card consists of three parts - the card number, winning numbers and given numbers
```kotlin
data class Card(val number: Int = 0, val winning: List<Int>, val given: List<Int>)
```
Note that the card number isn't used for part one.

## Part 1
Part one is extremely simple. I decided to use (set) intersection to calculate the shared values between the winning and given numbers
```kotlin
fun part1(input: List<String>): Int {
    return input.sumOf {
        val card = it.removePrefix("Card\\s+\\d+:\\s".toRegex()).getCard()
        points(card)
    }
}

// ...

fun points(card: Card): Int {
    val intersection = card.winning.intersect(card.given)
    if (intersection.isEmpty()) return 0
    return 2.0.pow(intersection.size - 1).toInt()
}
```

## Part 2
Okay... I hate puzzles with recursiveness (is that a word?)! It took me waaaaay too long to figure this one out. The "trick" I ended up using was basically the fact that I don't need to calculate all the actual copies. All I need is the first initial set of copies
```
{Card 1 -> [Card 2, Card 3, Card 4, Card 5], Card 2 -> [Card 3, Card 4], Card 3 -> [Card 4, Card 5], Card 4 -> [Card 5], Card 5 -> [], Card 6 -> []}
```
And from here recursively calculate the value for each card in the initial set of copies, ending up with something like this
```kotlin
fun part2(input: List<String>): Int {
    val cards = mutableListOf<Card>()
    input.forEachIndexed { idx, line ->
        val card = line.removePrefix("Card\\s+\\d+:\\s".toRegex()).getCard(idx)
        cards.add(card)
    }
    return calculateWins(cards).values.sum()
}

// ...

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
```