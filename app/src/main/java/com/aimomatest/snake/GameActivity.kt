package com.aimomatest.snake

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAd
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class GameActivity : AppCompatActivity() {
    private lateinit var gameCanvas: GameCanvasView

    private var gameState = GameState(20, 20)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE)
        AudienceNetworkAds.initialize(this)

        setContentView(R.layout.activity_game)

        // Set up the user interaction to manually show or hide the system UI.
        gameCanvas = findViewById(R.id.game_canvas)
        gameCanvas.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                when (gameState.runningState) {
                    RunningState.PLAYING -> {
                        /*
                        Turn the snake by clicking the top / bottom or left / right
                        half of the screen
                        */
                        gameState.nextDirection = if (gameState.direction.isVertical()) {
                            if (event.x < v.width / 2) Direction.LEFT else Direction.RIGHT
                        } else {
                            if (event.y < v.height / 2) Direction.UP else Direction.DOWN
                        }
                    }
                    RunningState.GAME_OVER, RunningState.AD_DISMISSED -> {
                        gameState = GameState(20, 20)
                        gameCanvas.post { gameCanvas.connectToState(gameState) }
                    }
                }
            }
            true
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                gameState.advance()
                gameCanvas.invalidate()
                if (gameState.runningState == RunningState.GAME_OVER) {
                    InterstitialAd(this@GameActivity, "212964457058052_212965810391250").also {
                        it.loadAd(it.buildLoadAdConfig()
                                      .withAdListener(GameInterstitialAdListener(it) { error ->
                                          gameState.runningState = RunningState.AD_DISMISSED
                                          gameState.adError = error
                                      }).build())
                    }
                    gameState.runningState = RunningState.AD_PENDING
                }
            }
        }, 200L, 200L)
    }

    override fun onResume() {
        super.onResume()
        gameCanvas.post { gameCanvas.connectToState(gameState) }
    }
}