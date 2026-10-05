package dungeon.io

import kotlinx.serialization.json.Json

/** Turns map text into a [DungeonMapDto] model. Knows about JSON; nothing else does. */
class MapParser {
    private val json = Json { ignoreUnknownKeys = false }

    /** @throws MapFormatException if the text is not a properly formed map file. */
    fun parse(text: String): DungeonMapDto =
        try {
            json.decodeFromString<DungeonMapDto>(text)
        } catch (e: IllegalArgumentException) {
            // SerializationException is a sub class of IllegalArgumentException so catch both
            throw MapFormatException("Map file is not valid: ${e.message}", e)
        }
}