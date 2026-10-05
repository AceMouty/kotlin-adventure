package dungeon.io

/** Where map text comes from. */
interface MapSource {
    /** The raw text of the named map, or null if no such map exists. */
    fun read(name: String): String?
}