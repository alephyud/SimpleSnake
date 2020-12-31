package com.aimomatest.snake

import com.facebook.ads.AdError
import java.io.Serializable

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun flip() = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    fun isVertical() = this == UP || this == DOWN
}

enum class RunningState {
    PLAYING, AD_PENDING, GAME_OVER, AD_DISMISSED
}

const val SNAKE_STEP = 0.5f

class GameState(public val sizeX: Int, public val sizeY: Int) : Serializable {
    private var leftToGrow: Int = 6
    var runningState: RunningState = RunningState.PLAYING
    var adError: AdError? = null

    var snakeCells = ArrayDeque<Pair<Float, Float>>().apply {
        addFirst(Pair(sizeX.toFloat() / 2, sizeY.toFloat() / 2))
    }
        private set

    var direction: Direction = Direction.RIGHT
        private set

    var nextDirection: Direction = direction

    fun hasSelfCollision(): Boolean {
        val head = snakeCells.first()
        val headX = head.first
        val headY = head.second
        return snakeCells.drop(3).any {
            Math.abs(headX - it.first) < 0.75f && Math.abs(headY - it.second) < 0.75f
        }
    }

    fun hasWallCollision(): Boolean {
        val head = snakeCells.first()
        val headX = head.first
        val headY = head.second
        return headX < 0.25 || headX > sizeX - 1.25 || headY < 0.25 || headY > sizeY - 1.25
    }

    fun advance() {
        if (runningState != RunningState.PLAYING) return;
        if (nextDirection != direction && nextDirection != direction.flip()) {
            direction = nextDirection;
        }
        val head = snakeCells.first()
        var headX = head.first
        var headY = head.second
        when (direction) {
            Direction.UP -> headY -= SNAKE_STEP
            Direction.DOWN -> headY += SNAKE_STEP
            Direction.LEFT -> headX -= SNAKE_STEP
            Direction.RIGHT -> headX += SNAKE_STEP
        }
        snakeCells.addFirst(Pair(headX, headY))
        if (leftToGrow > 0) {
            leftToGrow--
        } else {
            snakeCells.removeLast()
        }
        if (hasSelfCollision() || hasWallCollision()) {
            runningState = RunningState.GAME_OVER
        }
    }
}