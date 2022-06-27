package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.cs446.awake.Awake
import com.cs446.awake.model.Board
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.InputHandler

// Temporary enter screen, can change to loading screen before rendering
//     battleScreen in the future
class EnterScreen(private val board: Board) : BaseScreen() {

    override fun initialize() {
        Gdx.input.inputProcessor = stage

        val wid = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()


        val bgImg = Texture("dragon.jpeg")
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragon.jpeg")
        background.setSize(wid, (wid / bgImg.width * bgImg.height))
        background.centerAtPosition(wid/2, height/2)

        val start = BaseActor(0f, 0f, stage)
        start.loadTexture("start-message.png")
        start.centerAtPosition(wid/2, height)
        start.moveBy(0f, -800f)
    }

    override fun update(delta: Float) {
        if (Gdx.input.justTouched()) Awake.setActiveScreen(BattleScreen(board))
    }
}