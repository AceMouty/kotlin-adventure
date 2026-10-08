import dungeon.io.ClasspathMapSource
import dungeon.io.DungeonLoader
import dungeon.io.LoadResult

fun main() {
    println("Kotlin Adventure")
    val loader = DungeonLoader(ClasspathMapSource())

    for (name in listOf("single_room_map", "cross_map", "line_map", "loop_map", "loop_fork_map")) {
        when (val result = loader.loadDungeon(name)) {
            is LoadResult.Success -> {
                val dungeon = result.dungeon
                val exitCount = dungeon.rooms.sumOf { it.exits.size }
                println("$name: ${dungeon.rooms.size} rooms, $exitCount exits")
                result.warnings.forEach { println("  WARNING ${it.message}") }
            }
            is LoadResult.NotFound -> println("$name: not found")
            is LoadResult.Malformed -> println("$name: ${result.reason}")
            is LoadResult.Invalid -> {
                println("$name: invalid")
                result.errors.forEach { println("  ERROR ${it.message}") }
            }
        }
    }
}