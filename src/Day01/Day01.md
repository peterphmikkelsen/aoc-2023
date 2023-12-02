# Advent of Code 2023 - Day 1: Trebuchet?!
Here is a description of the solution to part 1 and 2 of Advent of Code 2023 - Day 1: Trebuchet?!

## Part 1
Part 1 is just about finding the first and last digit in a string. The input if of course a series of strings, so we find both for each of them.
When I see "find x in string"-tasks my brain immmediatly go to regular expressions. So, that is exactly what I did in this first part.

For finding purely digits it is very straight forward. We can simply use the regular expression `\d`. This will match all _single_ digits in the given string.
The solution then becomes very easy:

```kotlin
fun part1(input: List<String>): Long {
    val digits = "\\d".toRegex()
    return input.sumOf { line ->
        val numbers = digits.findAll(line).map { it.value }
        "${numbers.first()}${numbers.last()}".toLong()
    }
}
```
I realize that there are more efficient ways of combining two numbers as strings and converting them to an `Int`, but to be honest I don't care much about effiecieny in AoC.

## Part 2
Part 2 is similar to part 1 (duh, they always are) _but_ now we also have to consider strings like `one`, `two`, etc.
Like I said in part 1, my brain almost always go to regular expressions, and I wanted to keep that here. This turned out to be much more tricky than I had expected. For the people who solved this task they immediatly see what the problem is, but let's go through it.

I started with the _exact_ same solution as in part 1 just with a different regular expression.
```
\d|(one|two|three|four|five|six|seven|eight|nine)
```
But as I not-so-quickly realized (yes, this took me a while to figure out) the digit-words can overlap. How annoying. Anyway, luckily regular expressions has something called [_positive lookahead_](https://www.regular-expressions.info/lookaround.html). I won't go into detail about how they work, but the key point is that is allows me to create two matches for overlapping digit-words.
Take the string `2oneight` as an example. With my naive solution this would yield me 21, 2 and `one` converted to it's digit representation and then concatinated.
But with postive lookahead
```
\d|(?=(one|two|three|four|five|six|seven|eight|nine))
```
the expression will match any of the digit-words even if they directly follow each other. So in my example string above I correctly create match-groups for 2, `one` and `eight`.
The "unfortunate" thing is that this doesn't actually include the lookahead part in the match, so in order to get around this I need to look directly at the match groups which luckily in Kotlin also contain the match itself.
So the solution ends up looking something like this:
```kotlin
fun part2(input: List<String>): Long {
    val digits = "\\d|(?=(one|two|three|four|five|six|seven|eight|nine))".toRegex()
    return input.sumOf { line ->
        val numbers = digits.findAll(line)
            .map { it.groupValues.filter(String::isNotEmpty) }.flatten()
            .map { it.toNumber() }
        "${numbers.first()}${numbers.last()}".toLong()
    }
}
```
I will be the first to admit that this is terrible code, but it does get the job done.

What I essentially do is again to find all matches in the input, I then look at the match groups of the result and remove (filter) the ones where the match (value) is empty or the entire match group is null.
I then take the value (e.g. `one`, `two`, etc.) and convert it to it's corresponding digit with the help of a simple helper function:
```kotlin
fun String.toNumber(): Int {
    val map = mapOf("one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9)
    return map[this] ?: this.toInt()
}
```
If the value is already a digit then we simply take it, however if the value is word-digit we then convert it into it's digit.


## After Thoughts
After having given this solution more thought and also learning about the awesome functions in the Kotlin Standard Library `findAnyOf` and `findLastAnyOf` I realized that there is much simpler way to tackle this.
One could simply look for the first and last occurences of the collection in the string
```python
["1", "2", "3", ..., "one", "two", "three", ...]
```
The code would then become something like this:
```kotlin
fun part2(input: List<String>): Long {
    val matchGroup = listOf("1", "2", "3", ..., "one", "two", "three", ...)
    return input.sumOf { line ->
        val firstNumber = line.findAnyOf(matchGroup)!!.second.toNumber()
        val lastNumber = line.findLastAnyOf(matchGroup)!!.second.toNumber()
        "$firstNumber$lastNumber".toLong()
    }
}
```
The line count is not that much less but it is _much_ more readable.