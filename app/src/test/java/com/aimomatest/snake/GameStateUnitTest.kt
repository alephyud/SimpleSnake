package com.aimomatest.snake

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class GameStateUnitTest {
    @Test
    fun noIncorrectCollisions() {
        val state = GameState(20, 20)
        state.advance()
        state.advance()
        state.nextDirection = Direction.UP
        state.advance()
        state.advance()
        assertFalse(state.hasSelfCollision())
        assertFalse(state.hasWallCollision())
    }
}