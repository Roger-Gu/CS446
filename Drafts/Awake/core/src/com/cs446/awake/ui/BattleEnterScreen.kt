package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.cs446.awake.Awake
import com.cs446.awake.model.Board
import com.cs446.awake.utils.*

// First screen of the fight, tap to start the fight.
// TODO: Graphic:
//   * Background Picture:  dragon.jpeg
//   * Text Info:           start-message.png
// Next Screen: BattleScreen
//   * Go to: when player touched
//   * Return: NA
// TODO: Prev Screen: DungeonScreen
class BattleEnterScreen(private val board: Board) : BaseScreen() {

    override fun initialize() {
        Gdx.input.inputProcessor = stage
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragon.jpeg")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth/2, screenHeight/2)

        // Text Info
        val start = BaseActor(0f, 0f, stage)
        start.loadTexture("start-message.png")
        start.centerAtPosition(screenWidth/2, screenHeight)
        start.moveBy(0f, -800f)
    }

    override fun update(delta: Float) {
        // Switch to the next screen when player touched
        if (Gdx.input.justTouched()) Awake.setActiveScreen(BattleScreen(board))
    }
}