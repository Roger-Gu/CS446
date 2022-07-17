package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.cs446.awake.model.DungeonMap
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen


class DungeonScreen(private val map: DungeonMap) : BaseScreen() {
    //// Variable of position related
    // Size of the entire screen
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    // DungeonMap Data
    private val level = map.level

    override fun initialize() {
        Gdx.input.inputProcessor = stage

        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragon.jpeg")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        // Menu Bar Label
        val coin = Label("Coin: 20", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        val progress = Label("Level: $level/10", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        //"Village ${"■".repeat(level*2)}${"■".repeat(10-level*2)} BOSS"
        //"Health ${"♥".repeat(3)}"
        val health = Label("Health: 3/3", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))

        // Menu Bar Picture
        val bgPixmap = Pixmap(1, 1, Pixmap.Format.RGB565)
        bgPixmap.setColor(Color.GRAY)
        bgPixmap.fill()
        val textureRegionDrawableBg = TextureRegionDrawable(TextureRegion(Texture(bgPixmap)))
        val menuBar = Table()
        menuBar.background = textureRegionDrawableBg
        menuBar.setPosition(0f, screenHeight - 160f)
        menuBar.setSize(screenWidth, 160f)
        menuBar.top()
        menuBar.add(coin).expandX()
        menuBar.add(progress).expandX()
        menuBar.add(health).expandX()
        stage.addActor(menuBar)

        // Add Event Cards
        for (row in 0 until 3) {
            for (column in 0 until 7) {
                val card = BaseActor(0f, 0f, stage)
                card.loadTexture(map.map[row][column].backImg)
                if (row == 2 && column == 6) {
                    // Next Room (Boss)
                    card.loadTexture(map.map[row][column].frontImg)
                } else if (row == 0 && column == 0) {
                    // Current Position
                    card.loadTexture(map.map[row][column].frontImg)
                    map.map[row][column].trigger()
                }
//                card.setSize(screenWidth / 8f, (screenWidth / card.width * card.height))
                card.height = card.width
                card.centerAtPosition(screenWidth / 7 * column + card.width / 2 + 10, (screenHeight - 160) / 3 * row + card.height / 2)

                // Set event action
                card.addListener(object : InputListener() {
                    override fun touchDown(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        button: Int
                    ): Boolean {
                        if (map.map[row][column].isFlipped()) return true
                        if (map.go(row, column)) {
                            card.loadTexture(map.map[row][column].frontImg)
                            card.height = card.width
                        }
                        return true
                    }
                })
            }
        }
    }

    override fun update(delta: Float) {
        // TODO: NOT IMPLEMENTED
        return
    }
}