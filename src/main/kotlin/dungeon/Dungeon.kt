package com.acemouty.adventure.dungeon

class Dungeon(
    val startRoom: Int,
    rooms: Collection<Room>,
) {
    // Constructor parameters are deliberately not properties:
    // how you index them internally is your decision.

    val rooms: Collection<Room> get() = roomsById.values
    private val roomsById: Map<Int, Room>

    // Your internal storage, built from a private copy of `rooms`.

    init {
        requireUniqueIds(rooms)
        roomsById = rooms
            .map { room -> room.copy(exits = room.exits.toMap())}
            .associateBy { room -> room.id }
        requireStartRoomExists()
        requireExitTargetsExist()
        requireExitsMatchCoordinates()
    }

    /** @throws IllegalArgumentException if two rooms share an id. */
    private fun requireUniqueIds(rooms: Collection<Room>): Unit = TODO()

    /** @throws IllegalArgumentException if startRoom is not one of the rooms. */
    private fun requireStartRoomExists(): Unit = TODO()

    /** @throws IllegalArgumentException if any exit leads to an id that is not one of the rooms. */
    private fun requireExitTargetsExist(): Unit = TODO()

    /** @throws IllegalArgumentException if any exit's destination is not one grid step away in the exit's direction. */
    private fun requireExitsMatchCoordinates(): Unit = TODO()

    /** The room with this id, or null if none exists. */
    fun room(id: Int): Room? = TODO()

    /** All exits leaving this room, keyed by direction. Empty if it has none. */
    fun exitsFrom(id: Int): Map<Direction, Exit>? = TODO()

    /** The exit leaving this room in this direction, or null. */
    fun exit(from: Int, direction: Direction): Exit? = TODO()

    /** Ids of rooms directly reachable from this one. */
    fun neighbors(id: Int): Set<Int>? = TODO()
}

data class Room(
    val id: Int,
    val name: String,
    val coordinates: RoomCoordinates,
    val exits: Map<Direction, Exit>
)

data class Exit(
    val to: Int,
    val locked: Boolean,
    val hidden: Boolean,
    val requires: String?
)

data class RoomCoordinates(val x: Int, val y: Int)

enum class Direction(val label: String) {
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west");

    fun opposite(): Direction =
        when(this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
        }

    companion object {
        fun fromLabelOrNull(label: String): Direction? =
            entries.firstOrNull {
                it.label == label
            }
    }
}
