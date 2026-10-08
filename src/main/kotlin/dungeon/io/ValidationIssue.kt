package dungeon.io

import com.acemouty.adventure.dungeon.Direction

enum class Severity { ERROR, WARNING }

/** One problem found in a map file. */
sealed interface ValidationIssue {
    val severity: Severity
    val message: String
}

data object NoRooms : ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() = "No rooms found; a map must have at least one room"
}

data class DuplicateRoomId(val id: Int, val count: Int) : ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() = "Room id $id is used $count times; room ids must be unique"
}

data class SharedCoordinates(val x: Int, val y: Int, val roomIds: List<Int>): ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() =
        "Rooms ${roomIds.joinToString(", ")} share coordinates ($x, $y); each room needs its own unique position"
}

data class StartRoomMissing(val startRoom: Int) : ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() =
        "Start room $startRoom does not exist; startRoom must be the id of a room in the map"
}

data class UnknownDirection(val from: Int, val direction: String, val to: Int) : ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() =
        "Connection $from -$direction-> $to has unknown direction '$direction'; " +
                "expected one of ${Direction.entries.joinToString(", ") { it.label }}"
}

data class DuplicateExit(val from: Int, val direction: String, val destinationIds: List<Int>) : ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() =
        "Room $from has ${destinationIds.size} $direction exits (to rooms ${destinationIds.joinToString(", ")}); " +
                "a room can have only one exit per direction"
}

data class ConnectionFromUnknownRoom(val from: Int, val direction: String, val to: Int) : ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() =
        "Connection $from -$direction-> $to starts from room $from, which does not exist"
}

data class ConnectionToUnknownRoom(val from: Int, val direction: String, val to: Int) : ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() =
        "Connection $from -$direction-> $to leads to room $to, which does not exist"
}

data class CoordinateMismatch(
    val from: Int,
    val direction: Direction,
    val to: Int,
    val expected: RoomCoordinatesDto,
    val actual: RoomCoordinatesDto
) : ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() =
        "Connection $from -${direction.label}-> $to should reach (${expected.x}, ${expected.y}), " +
                "but room $to is at (${actual.x}, ${actual.y})"
}

data class OneWayConnection(val from: Int, val direction: Direction, val to: Int) : ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() =
        "Connection $from -${direction.label}-> $to is one-way: " +
                "room $to has no ${direction.opposite().label} exit back to $from"
}

data class UnreachableRoom(val id: Int) : ValidationIssue {
    override val severity = Severity.ERROR
    override val message get() = "Room $id cannot be reached from the start room"
}