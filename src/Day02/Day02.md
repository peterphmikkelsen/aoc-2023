# Advent of Code 2023 - Day 2: Cube Conundrum
Here is a description of the solution to part 1 and 2 of Advent of Code 2023 - Day 2: Cube Conundrum

## Part 1
Part 1 in just about figuring out whether or not any of the "draws" that you see are impossible. Here impossible means that the draw reveals more cubes of a certain color than can be there.
The total amount of cubes for each color is given in the puzzle as `12 red cubes`, `13 green cubes`, and `14 blue cubes`. We can then simply check for each draw if any of them are impossible:
```kotlin
fun part1(input: List<String>): Int {
    val limits = mapOf("red" to 12, "green" to 13, "blue" to 14)
    return input.sumOf { line ->
        val (gameId, subsets) = line.split(": ")
        val possible = subsets.formatGame().all { it.second <= limits[it.first]!! }
        if (possible) gameId.split(" ").last().toInt() else 0
    }
}
```
Note that `formatGame` is a helper function that takes the original input line and converts it into a single list of pairs of draws (color, amount)
```kotlin
fun String.formatGame(): List<Pair<String, Int>> {
    val cubes = this.split("; ")
    return cubes.map { cube ->
        cube.split(", ").map { it.split(" ") }.map { it[1] to it[0].toInt() }
    }.flatten()
}
```

## Part 2
Part 2 is also fairly straight foward. We have to find the maximum value for each color for all draws in each game
```kotlin
fun part2(input: List<String>): Int {
    return input.sumOf { line ->
        val (_, subsets) = line.split(": ")
        subsets.formatGame()
            .groupBy { it.first }
            .mapValues { it.value.maxOf(Pair<String, Int>::second) }.values
            .reduce(Int::times)
    }
}
```
Here I take the list of draws and group them by the color, this results in a map like this
```
{"red" -> [("red", 2), ("red", 4)], "green" -> [("green", 3)], "blue" -> [("blue", 1), ("blue", 5)]}
```
I then map all the values to the maximum of the amount in every draw-pair
```
{"red" -> 4, "green" -> 3, "blue" -> 5}
```
It is then as simple as multiplying each value in the map. 
