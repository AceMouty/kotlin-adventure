package com.acemouty.adventure.dungeon.io

import com.acemouty.adventure.dungeon.Direction
import com.acemouty.adventure.dungeon.createTestRoomDto
import dungeon.io.ConnectionDto
import dungeon.io.ConnectionFromUnknownRoom
import dungeon.io.ConnectionToUnknownRoom
import dungeon.io.CoordinateMismatch
import dungeon.io.DungeonMapDto
import dungeon.io.DuplicateExit
import dungeon.io.DuplicateRoomId
import dungeon.io.MapValidator
import dungeon.io.NoRooms
import dungeon.io.OneWayConnection
import dungeon.io.RoomCoordinatesDto
import dungeon.io.RoomDto
import dungeon.io.SharedCoordinates
import dungeon.io.StartRoomMissing
import dungeon.io.UnknownDirection
import dungeon.io.UnreachableRoom
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapValidatorTest {

    private fun errorsFor(map: DungeonMapDto) = MapValidator.validate(map).errors

    @Nested
    inner class ValidMaps {
        @Test
        fun `valid line map`() {
            assertTrue(errorsFor(lineMap).isEmpty())
        }

        @Test
        fun `valid single room map`() {
            assertTrue(errorsFor(singleRoomMap).isEmpty())
        }
    }

    @Nested
    inner class RoomRules {
        // UnreachableRoom
        @Test
        fun `a map with no rooms is reported`() {
            val map = lineMap.copy( rooms = emptyList() )
            val errors = errorsFor(map)

            assertContains(errors, NoRooms)
        }

        @Test
        fun `a map with duplicate room ids is reported`() {
            val map = lineMap.withRoom(createTestRoomDto(1))
            val errors = errorsFor(map)

            assertContains(errors, DuplicateRoomId(id = 1, count = 2))
        }

        @Test
        fun `a room with shared coordinates is reported`() {
            val map = lineMap.withRoom(
                createTestRoomDto(
                    id = 100,
                    coordinates = RoomCoordinatesDto(
                        x = 3,
                        y = 5
                    )
                )
            )
            val errors = errorsFor(map)

            assertContains(errors, SharedCoordinates(
                x = 3,
                y = 5,
                roomIds = listOf(0, 100)
            ))
        }

        @Test
        fun `a startRoom id that does not match any available room ids is reported`() {
            val map = lineMap.copy(startRoom = 99)
            val errors = errorsFor(map)

            assertContains(errors, StartRoomMissing(
                startRoom = 99
            ))
        }

        @Test
        fun `a room that is unreachable is reported`() {
            val map = lineMap
                .withoutConnection(from = 1, to = 2)
                .withoutConnection( from = 2, to = 1)

            val errors = errorsFor(map)

            assertContains(errors, UnreachableRoom(2))
        }
    }

    @Nested
    inner class ConnectionRules {
        // OneWayConnection

        @Test
        fun `a connection with a unknown direction is reported`() {
            val map = lineMap.withDirectionChanged(from = 0, to = 1, direction = "north-east")

            val errors = errorsFor(map)

            assertContains(errors, UnknownDirection(
                from = 0,
                direction = "north-east",
                to = 1
            ))
        }

        @Test
        fun `a connection that is a duplicate exit is reported`() {
            val map = lineMap.withConnection(
                ConnectionDto(
                    from = 0,
                    direction = "north",
                    to = 1,
                    locked = false,
                    hidden = false,
                    requires = null
                )
            )

            val errors = errorsFor(map)

            assertContains(errors, DuplicateExit(
                from = 0,
                direction = "north",
                destinationIds = listOf(1, 1)
            ))
        }

        @Test
        fun `a connection from a unknown room is reported`() {
            val map = lineMap.withConnection(
                ConnectionDto(
                    from = 99,
                    direction = "north",
                    to = 1,
                    locked = false,
                    hidden = false,
                    requires = null
                )
            )

            val errors = errorsFor(map)

            assertContains(errors, ConnectionFromUnknownRoom(
                from = 99,
                direction = "north",
                to = 1
            ))
        }

        @Test
        fun `a connection to a unknown room is reported`() {
            val map = lineMap.withConnection(
                ConnectionDto(
                    from = 1,
                    direction = "north",
                    to = 99,
                    locked = false,
                    hidden = false,
                    requires = null
                )
            )
            
            val errors = errorsFor(map)
            
            assertContains(errors, ConnectionToUnknownRoom(
                from = 1,
                direction = "north",
                to = 99
            ))
        }

        @Test
        fun `a connection that lands on the wrong coordinates is reported`() {
            val map = lineMap.withRoomMovedTo(
                id = 2,
                coordinates =
                    RoomCoordinatesDto(4, 7)
            )

            val errors = errorsFor(map)

            assertContains(
                errors,
                CoordinateMismatch(
                    from = 1,
                    direction = Direction.NORTH,
                    to = 2,
                    expected = RoomCoordinatesDto(3, 7),
                    actual = RoomCoordinatesDto(4, 7),
                )
            )
        }
        
        @Test
        fun `a one-way connection is reported`() {
            val map = lineMap
                .withRoom(
                    RoomDto(
                        id = 3,
                        name = "Room 3",
                        coordinates = RoomCoordinatesDto( x = 3, y = 8)
                    )
                )
                .withConnection(
                    ConnectionDto(
                        from = 2,
                        direction = "north",
                        to = 3,
                        locked = false,
                        hidden = false,
                        requires = null
                    )
                )
            val errors = errorsFor(map)
            
            assertContains(errors, OneWayConnection(
                from = 2,
                direction = Direction.NORTH,
                to = 3
            ))
        }
    }

    @Nested
    inner class ValidatorBehavior {
        @Test
        fun `every map error is reported when running validation`() {
            val map = lineMap
                .withStartRoom(99)
                .withDirectionChanged( from = 0, to = 1, direction = "north-east")
            val errors = errorsFor(map)
            
            assertContains(errors, StartRoomMissing(
                startRoom = 99
            ))
            
            assertContains(errors, UnknownDirection(
                from = 0,
                direction = "north-east",
                to = 1
            ))
        }

        @Test
        fun `a map with no errors is valid`() {
            val validationResult = MapValidator.validate(lineMap)
            assertTrue(validationResult.isValid)
        }

        @Test
        fun `a map with errors is invalid`() {
            val map = lineMap.withStartRoom(99)
            val validationResult = MapValidator.validate(map)

            assertFalse(validationResult.isValid)
        }
    }


}