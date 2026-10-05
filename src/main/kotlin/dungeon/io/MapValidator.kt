package dungeon.io

import com.acemouty.adventure.dungeon.Direction

/**
 * Outcome of validating a [DungeonMapDto].
 * Errors mean the map must not be used; warnings are suspicious but playable.
 */
data class ValidationResult(val issues: List<ValidationIssue>) {
    val errors: List<ValidationIssue> get() = issues.filter { it.severity == Severity.ERROR }
    val warnings: List<ValidationIssue> get() = issues.filter { it.severity == Severity.WARNING }
    val isValid: Boolean get() = errors.isEmpty()
}

object MapValidator {

    fun validate(map: DungeonMapDto): ValidationResult {
        val issues = mutableListOf<ValidationIssue>()

        checkRooms(map, issues)

        // Safe even with duplicate ids: those are already reported above.
        val rooms = map.rooms.associateBy { it.id }

        if (map.startRoom !in rooms) {
            issues.add(StartRoomMissing(map.startRoom))
        }

        checkConnections(map, rooms, issues)
        checkReachability(map, rooms, issues)

        return ValidationResult(issues)
    }

    private fun checkRooms(map: DungeonMapDto, issues: MutableList<ValidationIssue>) {
        if (map.rooms.isEmpty()) issues.add(NoRooms)

        map.rooms.groupBy { it.id }
            .filterValues { it.size > 1 }
            .forEach { (id, dupes) -> issues.add(DuplicateRoomId(id, dupes.size)) }

        map.rooms.groupBy { it.coordinates }
            .filterValues { it.size > 1 }
            .forEach { (c, dupes) ->
                issues.add(SharedCoordinates(x = c.x, y = c.y, roomIds = dupes.map { it.id }))
            }
    }

    private fun checkConnections(
        map: DungeonMapDto,
        rooms: Map<Int, RoomDto>,
        issues: MutableList<ValidationIssue>
    ) {
        map.connections.groupBy { it.from to it.direction }
            .filterValues { it.size > 1 }
            .forEach { (key, dupes) ->
                val (from, direction) = key
                issues.add(DuplicateExit(
                    from = from,
                    direction = direction,
                    destinationIds = dupes.map{ it.to }
                ))
            }

        val edges = map.connections.map { Triple(it.from, it.direction, it.to) }.toSet()

        for (c in map.connections) {
            val from = rooms[c.from]
            val to = rooms[c.to]
            val direction = Direction.fromLabelOrNull(c.direction)

            if (direction == null) issues.add(UnknownDirection(from = c.from, direction = c.direction, to = c.to))
            if (from == null) issues.add(ConnectionFromUnknownRoom(from = c.from, direction = c.direction, to = c.to))
            if (to == null) issues.add(ConnectionToUnknownRoom(from = c.from, direction = c.direction, to = c.to))
            if (from == null || to == null || direction == null) continue

            val (dx, dy) = direction.offset()
            val expected = RoomCoordinatesDto(from.coordinates.x + dx, from.coordinates.y + dy)
            if (to.coordinates != expected) {
                issues.add(CoordinateMismatch(c.from, direction, c.to, expected, to.coordinates))
            }

            if (Triple(c.to, direction.opposite().label, c.from) !in edges) {
                issues.add(OneWayConnection(c.from, direction, c.to))
            }
        }
    }

    /** Breadth-first walk from startRoom; anything not visited can never be entered. */
    private fun checkReachability(map: DungeonMapDto, rooms: Map<Int, RoomDto>, issues: MutableList<ValidationIssue>) {
        if (map.startRoom !in rooms) return

        val exits = map.connections.groupBy({ it.from }, { it.to })
        val seen = mutableSetOf(map.startRoom)
        val queue = ArrayDeque(listOf(map.startRoom))
        while (queue.isNotEmpty()) {
            for (next in exits[queue.removeFirst()].orEmpty()) {
                if (next in rooms && seen.add(next)) queue += next
            }
        }

        (rooms.keys - seen).sorted().forEach {
            issues.add(UnreachableRoom(id = it))
        }
    }

    private fun Direction.offset(): Pair<Int, Int> = when (this) {
        Direction.NORTH -> 0 to 1
        Direction.SOUTH -> 0 to -1
        Direction.EAST -> 1 to 0
        Direction.WEST -> -1 to 0
    }
}