fun main() {
    
    fun part1(input: MutableList<String>): Long {
        val seeds = input[0].removePrefix("seeds: ").split(" ").map(String::toLong)
        val maps = input.getMaps()
        return seeds.minOf { maps.fold(it, ::getLocation) }
    }
    
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

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test").toMutableList()
//    check(part1(testInput) == 35L)
//    check(part2(testInput) == 46L)

    val input = readInput("Day05").toMutableList()
//    part1(input).println()
    part2(input).println()
}

fun MutableList<String>.getMaps(): Array<MutableList<GardenMap>> {
    this.removeAt(0)
    this.removeAt(0)

    val maps = Array(7) { mutableListOf<GardenMap>() }

    var index = 0
    for (line in this) {
        if (line.isEmpty()) {
            index++
            continue
        }
        if (line.none { it.isDigit() }) continue

        val (srcRangeStart, dstRangeStart, rangeLength) = line.split(" ").map(String::toLong)
        maps[index].add(GardenMap(Range(srcRangeStart, srcRangeStart + rangeLength), Range(dstRangeStart, dstRangeStart + rangeLength)))
    }
    return maps
}

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

data class GardenMap(val src: Range, val dest: Range)
data class Range(val start: Long, val end: Long)