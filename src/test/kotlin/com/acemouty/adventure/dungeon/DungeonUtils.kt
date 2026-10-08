package com.acemouty.adventure.dungeon

import dungeon.io.ConnectionDto
import dungeon.io.DungeonMapDto
import dungeon.io.MapValidator
import dungeon.io.RoomCoordinatesDto
import dungeon.io.RoomDto
import dungeon.io.toDungeon

/** Builds a Dungeon through the real validator and mapper. Fails if the map is invalid. */
fun dungeonFrom(dto: DungeonMapDto): Dungeon {
    val result = MapValidator.validate(dto)
    check(result.isValid) { "Test map is invalid: ${result.errors.map{ it.message }}" }
    return dto.toDungeon()
}

/** Three rooms in a line: 0 <-> 1 <-> 2, going north. */
val lineMap = DungeonMapDto(
    startRoom = 0,
    rooms = listOf(
        RoomDto(0, "Room 0", RoomCoordinatesDto(3, 5)),
        RoomDto(1, "Room 1", RoomCoordinatesDto(3, 6)),
        RoomDto(2, "Room 2", RoomCoordinatesDto(3, 7)),
    ),
    connections = listOf(
        ConnectionDto(0, "north", 1),
        ConnectionDto(1, "south", 0),
        ConnectionDto(1, "north", 2),
        ConnectionDto(2, "south", 1),
    ),
)

val singleRoomMap = DungeonMapDto(
    startRoom = 0,
    rooms = listOf(RoomDto(0, "Room 0", RoomCoordinatesDto(0, 0))),
    connections = emptyList(),
)

fun createTestExit(
    to: Int = 1,
    locked: Boolean = false,
    hidden: Boolean = false,
    requires: String? = null
) =
    Exit(
        to = to,
        locked = locked,
        hidden = hidden,
        requires = requires
    )