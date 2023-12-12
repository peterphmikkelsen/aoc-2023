# Advent of Code 2023 - Day 5: If You Give A Seed A Fertilizer
Here is a description of the solution to part 1 and 2 of Advent of Code 2023 - Day 5: If You Give A Seed A Fertilizer

## Modeling the Maps
For this puzzle I ended up with two structures:
```kotlin
data class GardenMap(val src: Range, val dest: Range)
```
```kotlin
data class Range(val start: Long, val end: Long)
```
These were mostly created for sanity purposes. I was loosing track of the numbers, so adding a bit of structure is always good.

### Calculating the Next Position
To calculate the next postion from a given position and a map I wrote this function
```kotlin
fun getLocation(location: Long, nextMap: List<GardenMap>): Long {
    var resultLocation = location
    nextMap.forEach { map ->
        if (location < map.src.end && location >= map.src.start) {
            resultLocation = map.src.start + (location - map.dest.start)
            return@forEach
        }
    }
    return resultLocation
}
```
It is really simple in general but the important thing is to note how to calculate the next position. Given a source range $s$ (with start $s_{start}$), a destination range $d$ (with start $d_{start}$) and a location $l$,
```math
s_{start} + (l - d_{start})
```
This is of course _iff_ the location is actually contained in the range. If not, then the number is simply returned.

## Part 1
Part 1 is quite simple (it is part 2 which is a nightmare...). We simply calculate the next position and continously hand this onwards to the next map. This behavior is easily done using a `fold` or `foldRight` function
```kotlin
fun part1(input: MutableList<String>): Long {
    val seeds = input[0].removePrefix("seeds: ").split(" ").map(String::toLong)
    val maps = input.getMaps()
    return seeds.minOf { maps.fold(it, ::getLocation) }
}
```

## Part 2
For part 2 I knew as soon as I saw the input I knew that this would exponetially harder... _*If*_ I wanted to be clever... Which I didn't... So! I just took the same approach and calculated it for each seed,
```kotlin
fun part2(input: MutableList<String>): Long {
    val seeds = input[0].removePrefix("seeds: ")
        .split(" ")
        .map(String::toLong)
        .chunked(2)
        .map { Range(it[0], it[0] + it[1]) }
        .toList()

    val maps = input.getMaps()
    var min = Long.MAX_VALUE
    seeds.forEach {
        for (location in it.start .. it.end) {
            val l = maps.fold(location, ::getLocation)
            if (l < min) min = l
        }
    }
    return min
}
```
A solution which - of course - was extremely slow. It took minutes to run... But! It produced the correct result, which is all I care about.
