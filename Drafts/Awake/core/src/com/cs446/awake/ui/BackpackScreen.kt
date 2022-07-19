package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.cs446.awake.Awake
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen


class BackpackScreen() : BaseScreen() {
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    // Timer variables
    private var worldTimer  = -1
    private var activeTimer = false
    private val timerLimit = 10 // Not 0 in case of concurrency issue.
    private var endTimeFcn : () -> Unit = {} // lambda function about what to do when time ends
    private var duringTimeFcn : () -> Unit = {} // lambda function about what to do when each frame passed.

    private var list = mutableListOf<String>("s","a")
    private lateinit var scrollPane : ScrollPane


    // Function that active the timer
    private fun startTimer(frames: Int, endTime : () -> Unit, duringTime : () -> Unit) {
        endTimeFcn = endTime
        duringTimeFcn = duringTime
        worldTimer = frames
        activeTimer = true
    }

    // Function that count down the timer. Stop timer when time ends.
    private fun runTimer() {
        if (activeTimer) {
            if (worldTimer <= timerLimit) {
                // Time up
                activeTimer = false
                endTimeFcn()
            } else {
                // During count down
                duringTimeFcn()
                worldTimer--
            }
        }
    }

    override fun initialize() {
        Gdx.input.inputProcessor = stage
        //stage.addActor(countdownLabel)
        //countdownLabel.setPosition(screenWidth/2 - countdownLabel.width/2, screenHeight/2 + countdownLabel.height/2)

        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("villageBackground.png")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        var table = Table()
        var container = Table()
        // Menu Bar Label
        val coin = Label("Coin: 20", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        val cards = Label("Cards Owned: 15/30", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        val attempt = Label("Best Attempt: 2/3", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))

        table.add(coin)
        table.add(cards)
        table.add(attempt)
        table.row()
        coin.setAlignment(1)
        stage.addActor(container)

        scrollPane = ScrollPane(table)
        container.add(scrollPane).width(500f).height(500f)
        container.setPosition(500f, screenHeight - 660f)
        container.setSize(screenWidth, 160f)
        container.row()



        val house1 = BaseActor(0f, 0f, stage)
        house1.loadTexture("House1.png")

        house1.height = house1.width // Card is a square
        house1.centerAtPosition(screenWidth / 5 * 0 + house1.width / 2 + 100, (screenHeight / 2) + house1.height / 2 - 200)
        // Set event action
        house1.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                Awake.setActiveScreen(VillageScreen())
                return true
            }
        })

    }

    override fun update(delta: Float) {
        runTimer()
        return
    }
}