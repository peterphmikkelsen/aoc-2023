fun main() {

    fun part1(input: List<String>): Int {
        val digits = "\\d+".toRegex()
        val times = digits.findAll(input[0]).map { it.value.toLong() }.toList()
        val distances = digits.findAll(input[1]).map { it.value.toLong() }.toList()
        return times.zip(distances).map { (time, distance) -> amountOfWinningConfigurations(time, distance) }.reduce(Int::times)
    }

    fun part2(input: List<String>): Int {
        val time = input[0].replace("\\s+".toRegex(), "").removePrefix("Time:").toLong()
        val distance = input[1].replace("\\s+".toRegex(), "").removePrefix("Distance:").toLong()
        return amountOfWinningConfigurations(time, distance)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()

}

fun amountOfWinningConfigurations(time: Long, distance: Long): Int {
    return (1 until time).map { it * (time - it) }.filter { it > distance }.size
}