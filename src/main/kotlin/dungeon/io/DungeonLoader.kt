package dungeon.io

import com.acemouty.adventure.dungeon.Dungeon

/** The outcome of trying to load a named map. */
sealed interface LoadResult {
    /** The map loaded. Warnings are problems that don't prevent play. */
    data class Success(val dungeon: Dungeon, val warnings: List<ValidationIssue>) : LoadResult

    /** No map with this name exists. */
    data class NotFound(val name: String) : LoadResult

    /** The file exists but could not be read as a map file. */
    data class Malformed(val name: String, val reason: String) : LoadResult

    /** The file was read, but its contents break the map rules. */
    data class Invalid(val name: String, val errors: List<ValidationIssue>) : LoadResult
}

/** Loads a named map: read, parse, validate, convert. */
class DungeonLoader(
    private val source: MapSource,
    private val parser: MapParser = MapParser(),
) {
    /** Never throws for a bad or missing map; every outcome is a [LoadResult]. */
    fun load(name: String): LoadResult {
        val text = source.read(name)
            ?: return LoadResult.NotFound(name)

        val dungeonMapDto = try {
            parser.parse(text)
        } catch (e: MapFormatException) {
            return LoadResult.Malformed(name, e.message ?: "unknown format error")
        }

        val validationResult = MapValidator.validate(dungeonMapDto)
        if (!validationResult.isValid) {
            return LoadResult.Invalid(name, validationResult.errors)
        }

        return LoadResult.Success(dungeonMapDto.toDungeon(), validationResult.warnings)
    }
}