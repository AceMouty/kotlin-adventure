package dungeon.io

class ClasspathMapSource(
    private val baseDir: String = "/maps"
) : MapSource {
    override fun read(name: String): String? {
        val path = "$baseDir/$name.json"
        val fileString = ClasspathMapSource::class.java.getResourceAsStream(path)
            ?.bufferedReader()?.use { it.readText() }
        return fileString
    }
}