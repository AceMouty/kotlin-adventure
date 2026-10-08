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

fun createTestRoomDto(
    id: Int = 1,
    name: String = "Test Room $id",
    coordinates: RoomCoordinatesDto = RoomCoordinatesDto(0, 0)
) =
    RoomDto(
        id = id,
        name = name,
        coordinates = coordinates
    )