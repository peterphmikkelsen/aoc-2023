# Advent of Code 2023 - Day 6: Wait For It
Here is a description of the solution to part 1 and 2 of Advent of Code 2023 - Day 6: Wait For It

## Calculating the Amount of Winning Configurations
First of all, what is going on... Day 5 was a nightmare, but this one was solved in 10 minutes???

Oh well... To calculate the amount of winning configurations from a given time and distance:
```kotlin
fun amountOfWinningConfigurations(time: Long, distance: Long): Int {
    return (1 until time).map { it * (time - it) }.filter { it > distance }.size
}
```

## Part 1
Using this we can solve part one very simply
```kotlin
fun part1(input: List<String>): Int {
    val digits = "\\d+".toRegex()
    val times = digits.findAll(input[0]).map { it.value.toLong() }.toList()
    val distances = digits.findAll(input[1]).map { it.value.toLong() }.toList()
    return times.zip(distances).map { (time, distance) -> amountOfWinningConfigurations(time, distance) }.reduce(Int::times)
}
```
Basically, find all times and distances, zip them together and for each of them calculate the amount of winning configurations... Done.

## Part 2
Here it basically the same, we simply remove all whitespaces and take the combined numbers as time and distance and again calculate the amount of winning configurations
```kotlin
fun part2(input: List<String>): Int {
    val time = input[0].replace("\\s+".toRegex(), "").removePrefix("Time:").toLong()
    val distance = input[1].replace("\\s+".toRegex(), "").removePrefix("Distance:").toLong()
    return amountOfWinningConfigurations(time, distance)
}
```
Granted, it is slow but not horribly slow (mine ran in less than a second).
