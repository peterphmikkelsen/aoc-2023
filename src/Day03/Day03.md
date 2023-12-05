# Advent of Code 2023 - Day 3: Gear Ratios
Here is a description of the solution to part 1 and 2 of Advent of Code 2023 - Day 3: Gear Ratios

## Modeling the Schematic
For this puzzle it is really important (at least for me) to model the input correctly. Trying to keep track of everything when using a simple 2D array is more trouble than it is worth (since speed doesn't matter, yay!).

So before showing the solution I will walk through my model.

I started by defining a node. A node contains two things: The its position and the character it has (i.e. a digit, special character or dot). The general structure looks like this:
```kotlin
sealed interface Node {
    val position: Position
    val char: Char
    
    // ...

    data class Digit(override val position: Position, override val char: Char): Node

    data class Special(override val position: Position, override val char: Char): Node

    data class Dot(override val position: Position): Node {
        override val char get() = '.'
    }
}
```
Not very complicated, but very powerful.

I then defined the schematic based on nodes. Intuitively, a `Schematic` is just a collection of `Node`s.
```kotlin
data class Schematic(val nodes: Collection<Node>, val maxX: Int, val maxY: Int) {
    constructor(input: List<String>): this(input.mapIndexed { i, line ->
        line.mapIndexed { j, c ->
            val position = Position(j, i)
            when (c) {
                '.' -> Node.Dot(position)
                '&', '+', '-', '#', '@', '$', '*', '/', '%', '=' -> Node.Special(position, c)
                else -> Node.Digit(position, c)
            }
        }
    }.flatten(), input[0].length,  input.size)
    
    // ...
}
```
A schematic is in charge of a couple of things. Firstly, the collections of nodes (they are created in the constructor which takes the initial input and converts it); secondly, the bounds of the schematic in coordinates and thirdly, all logic to get specific `Node`(s).
As an example:
```kotlin
inline fun <reified R> getAllNodes(): List<R> {
    return nodes.filterIsInstance<R>()
}
```
Which can then be called like so:
```kotlin
schematic.getAllNodes<Node.Special>()
```

The schematic is also in charge of providing a way to calculate all neighboring nodes from another node as well as getting a whole `Part` from a single `Node.Digit`. What is a part you ask? Well it is simply a number... or more specifically a collection of digits.
```kotlin
data class Part(val components: List<Node.Digit>) {
    fun getAsNumber(): Int {
        return components.map { it.char }.joinToString("").toInt()
    }
}
```
With this we have a very nice model for this puzzle and we can move on to solutions.

## Part 1
With the schematic modeled nicely the algorithm for part one was pretty straight forward:
1. Find all special characters.
2. Check all it's neighbors and see if it touches a digit.
3. For each neighbor which is a digit construct the full number by looking left and right until a non-digit.
4. Sum up all the numbers

There are of course subleties in here which aren't described in the solution. For instance: "How will you handle numbers being touched by multiple special characters?" - Here the solution is simple (at least if you are lazy like me), simply use a set.
The solution ended up being very readable:
```kotlin
fun part1(input: List<String>): Int {
    val numbers = mutableSetOf<Part>()

    val schematic = Schematic(input)
    schematic.getAllNodes<Node.Special>().forEach {
        val neighbors = schematic.getNeighborsOf(it)
        neighbors.getAllNodes<Node.Digit>().forEach { digit ->  
            numbers.add(schematic.getWholePart(digit))
        }
    }
    return numbers.sumOf(Part::getAsNumber)
}
```

## Part 2
For part two the solution is very similar.
1. Find all _potential_ gears.
2. Check all it's neighbors and see if it touches a digit.
    - For each neighbor which is a digit construct the _part_.
    - Add each part to set.
3. Filter set of parts which do not contain exactly two parts.
4. Convert part to number.
5. Sum up all the numbers

Again, note the laziness with using a set (actually it ends up being a set of sets) to handle duplicated parts.

The solution ends up looking like this:
```kotlin
fun part2(input: List<String>): Int {
    val numbers = mutableSetOf<Set<Part>>()

    val schematic = Schematic(input)
    schematic.getPotentialGears().forEach {
        val neighbors = schematic.getNeighborsOf(it)

        val parts = mutableSetOf<Part>()
        neighbors.getAllNodes<Node.Digit>().forEach { digit ->
            parts.add(schematic.getWholePart(digit))
        }
        numbers.add(parts)
    }
    return numbers.map { it.map(Part::getAsNumber) }
        .filter { parts -> parts.size == 2 }
        .sumOf { it.reduce(Int::times) }
}
```