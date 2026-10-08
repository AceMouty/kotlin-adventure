package com.acemouty.adventure.dungeon

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DungeonTest {
    private val dungeon = dungeonFrom(lineMap)
    private val unknownRoomId = 100

    @Test
    fun `findRoom returns the room with that id`() {
        assertEquals(1, dungeon.findRoom(1)?.id)
    }

    @Test
    fun `findRoom returns null for an unknown id`() {
        assertNull(dungeon.findRoom(unknownRoomId))
    }

    @Test
    fun `exitsFrom returns every exit of the room`() {
        val expected = mapOf(
            Direction.NORTH to createTestExit()
        )
        assertEquals(expected, dungeon.exitsFrom(0))
    }

    @Test
    fun `exitsFrom returns null for an unknown id`() {
        assertNull(dungeon.exitsFrom(unknownRoomId))
    }

    @Test
    fun `exit returns the exit in that direction`() {
        val expected = createTestExit()

        assertEquals(expected, dungeon.exit(
            from = 0,
            direction = Direction.NORTH
        ))
    }

    @Test
    fun `exit returns null when the room has no exit that way`() {
        assertNull(dungeon.exit(
            from = 0,
            direction = Direction.SOUTH
        ))
    }

    @Test
    fun `exit returns null for an unknown room`() {
        assertNull(dungeon.exit(
            from = unknownRoomId,
            direction = Direction.NORTH
        ))
    }

    @Test
    fun `neighbors returns the ids the exits lead to`() {
        assertEquals(setOf(1), dungeon.neighbors(0))
    }

    @Test
    fun `neighbors returns null for an unknown id`() {
        assertNull(dungeon.neighbors(unknownRoomId))
    }

    @Test
    fun `a room with no exits has empty exits and no neighbors`() {
        val dungeon = dungeonFrom(singleRoomMap)

        assertNull(dungeon.exitsFrom(1))
        assertNull(dungeon.neighbors(1))
    }
}