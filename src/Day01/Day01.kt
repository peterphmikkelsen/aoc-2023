fun main() {
    
    fun part1(input: List<String>): Long {
        val digits = "\\d".toRegex()
        return input.sumOf { line ->
            val numbers = digits.findAll(line).map { it.value }
            "${numbers.first()}${numbers.last()}".toLong()
        }
    }

    fun part2(input: List<String>): Long {
        val digits = "\\d|(?=(one|two|three|four|five|six|seven|eight|nine))".toRegex()
        return input.sumOf { line ->
            val numbers = digits.findAll(line)
                .map { it.groupValues.filter(String::isNotEmpty) }.flatten()
                .map { it.toNumber() }
            "${numbers.first()}${numbers.last()}".toLong()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 281L)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

fun String.toNumber(): Int {
    val map = mapOf("one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9)
    return map[this] ?: this.toInt()
}
