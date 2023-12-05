fun main() {

    // 1. Find all special characters and their index.
    // 2. Check all it's neighbors and see if it touches a number
    // 3. For each neighbor which is a number construct the full digit by looking left and right until a "." or index out of bounds
    // 4. Sum up all the numbers
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

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()

}

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
    
    operator fun get(position: Position): Node {
        return nodes.find { it.position == position }!!
    }
    
    inline fun <reified R> getAllNodes(): List<R> {
        return nodes.getAllNodes<R>()
    }
    
    fun getPotentialGears(): List<Node.Special> {
        return getAllNodes<Node.Special>().filter { it.char == '*' }
    }
    
    fun getNeighborsOf(node: Node): List<Node> {
        val positions = node.getNeighborPositions(maxX, maxY)
        return nodes.filter { n -> positions.any { it == n.position } }
    }
    
    fun getWholePart(node: Node.Digit): Part {
        val number = mutableListOf(node)
        
        var rightIndex = node.position.x + 1
        while (rightIndex.isInBounds(maxX) && this[Position(rightIndex, node.position.y)].char.isDigit()) {
            number.add(this[Position(rightIndex, node.position.y)] as Node.Digit)
            rightIndex++
        }
        
        var leftIndex = node.position.x - 1
        while (leftIndex.isInBounds(maxX) && this[Position(leftIndex, node.position.y)].char.isDigit()) {
            number.add(0, this[Position(leftIndex, node.position.y)] as Node.Digit)
            leftIndex--
        }
        
        return Part(number)
    }
}

sealed interface Node {
    val position: Position
    val char: Char
    
    fun getNeighborPositions(maxX: Int, maxY: Int): List<Position> {
        val neighbors = listOf(
            this.position.x + 1 to this.position.y,
            this.position.x - 1 to this.position.y,
            this.position.x + 1 to this.position.y + 1,
            this.position.x - 1 to this.position.y - 1,
            this.position.x to this.position.y + 1,
            this.position.x to this.position.y - 1,
            this.position.x + 1 to this.position.y - 1,
            this.position.x - 1 to this.position.y + 1,
            ).map { Position(it.first, it.second) }
        return neighbors.filter { (it.x.isInBounds(maxX)) && (it.y.isInBounds(maxY)) }
    }
    
    data class Digit(override val position: Position, override val char: Char): Node
    
    data class Special(override val position: Position, override val char: Char): Node
    
    data class Dot(override val position: Position): Node {
        override val char get() = '.'
    }
}
data class Position(val x: Int, val y: Int)
data class Part(val components: List<Node.Digit>) {
    fun getAsNumber(): Int {
        return components.map { it.char }.joinToString("").toInt()
    }
}

inline fun <reified R> Collection<Node>.getAllNodes(): List<R> {
    return this.filterIsInstance<R>()
}

private fun Int.isInBounds(max: Int) = this in 0 until max
