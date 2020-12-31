package com.aimomatest.snake

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.min

/**
 * The game's main canvas, which shows the snake, the field and text overlays
 */
class GameCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var state: GameState? = null
    private var cellCanvas: CellCanvas? = null

    class CellCanvas(
        view: View,
        private val cellsX: Int,
        private val cellsY: Int,
    ) {
        private val w = view.width.toFloat()
        private val h = view.height.toFloat()
        private val cellSizeX = w / cellsX
        private val cellSizeY = h / cellsY
        private val cellSize = min(cellSizeX, cellSizeY)

        private val paddingX = if (cellSizeX > cellSizeY) (w - cellSize * cellsX) / 2 else 0f
        private val paddingY = if (cellSizeX < cellSizeY) (h - cellSize * cellsY) / 2 else 0f

        private fun getX(cellX: Float) = paddingX + cellSize * cellX
        private fun getY(cellY: Float) = paddingY + cellSize * cellY

        private fun View.colorArray(vararg resourceIds: Int) =
            resourceIds.map { resources.getColor(it) }.toIntArray()

        private val bgColor = view.resources.getColor(R.color.light_blue_900)
        private val snakeColors = view.colorArray(R.color.purple_200, R.color.purple_500)
        private val textColor = view.resources.getColor(R.color.white)

        private val baseTextPaint = Paint().apply {
            color = textColor
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        private val largeTextPaint = Paint(baseTextPaint).apply { textSize = w / 12 }
        private val smallTextPaint = Paint(baseTextPaint).apply { textSize = w / 40 }

        private fun drawCircle(canvas: Canvas, cellX: Float, cellY: Float, colors: IntArray) {
            val paint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                shader =
                    LinearGradient(getX(cellX),
                                   getY(cellY),
                                   getX(cellX + 1f),
                                   getY(cellY + 1f),
                                   colors,
                                   null,
                                   Shader.TileMode.CLAMP)
            }
            canvas.drawCircle(getX(cellX + 0.5f), getY(cellY + 0.5f), cellSize / 2, paint)
        }

        private fun drawTextOverlays(canvas: Canvas, state: GameState) {
            val x = canvas.width * 0.5F
            val largeY = canvas.width * 0.4F
            val smallY = canvas.width * 0.55F
            if (state.runningState != RunningState.PLAYING) {
                canvas.drawText("Game over".toUpperCase(Locale.ROOT), x, largeY, largeTextPaint)
            }
            if (state.runningState == RunningState.AD_PENDING) {
                canvas.drawText("Loading ad...", x, smallY, smallTextPaint)
            } else if (state.runningState == RunningState.AD_DISMISSED) {
                state.adError?.also {
                    canvas.drawText("Ad error: ${it.errorMessage}", x, smallY, smallTextPaint)
                }
            }
        }

        fun drawGameState(canvas: Canvas, state: GameState) {
            val fieldRect =
                RectF(getX(0f), getY(0f), getX(cellsX.toFloat()), getY(cellsY.toFloat()))
            val fieldPaint = Paint().apply { style = Paint.Style.FILL; color = bgColor }
            canvas.drawRect(fieldRect, fieldPaint)
            state.snakeCells.asReversed().forEach {
                drawCircle(canvas, it.first, it.second, snakeColors)
            }
            drawTextOverlays(canvas, state)
        }
    }

    fun connectToState(state: GameState) {
        this.state = state
        this.cellCanvas = CellCanvas(this, state.sizeX, state.sizeY)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            state?.also { cellCanvas?.drawGameState(canvas, it) }
        }
    }
}
