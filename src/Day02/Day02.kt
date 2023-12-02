fun main() {
    
    fun part1(input: List<String>): Int {
        val limits = mapOf("red" to 12, "green" to 13, "blue" to 14)
        return input.sumOf { line ->
            val (gameId, subsets) = line.split(": ")
            val possible = subsets.formatGame().all { it.second <= limits[it.first]!! }
            if (possible) gameId.split(" ").last().toInt() else 0
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val (_, subsets) = line.split(": ")
            subsets.formatGame()
                .groupBy { it.first }
                .mapValues { it.value.maxOf(Pair<String, Int>::second) }.values
                .reduce(Int::times)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

fun String.formatGame(): List<Pair<String, Int>> {
    val cubes = this.split("; ")
    return cubes.map { cube ->
        cube.split(", ").map { it.split(" ") }.map { it[1] to it[0].toInt() }
    }.flatten()
}