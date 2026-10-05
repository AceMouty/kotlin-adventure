package dungeon.io

/** The map text could not be read as a map file. */
class MapFormatException(message: String, cause: Throwable? = null) : Exception(message, cause)