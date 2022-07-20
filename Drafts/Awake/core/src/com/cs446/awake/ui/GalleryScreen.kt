package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.cs446.awake.Awake
import com.cs446.awake.model.*
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.DragDropActor


class GalleryScreen() : BaseScreen() {
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

    private lateinit var material : BaseActor
    private lateinit var weapon : BaseActor
    private lateinit var back : BaseActor
    private lateinit var paper : BaseActor
    private lateinit var name : Label
    private lateinit var use : Label

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

    fun showInfo(card: MergableCard) {
        name.setText(card.cardName)
        name.setFontScale(0.8f)
        name.setPosition(paper.x + 50, paper.y + 300)
        name.setSize(paper.width, paper.height)
        use.setText(card.usage)
        use.setFontScale(0.8f)
        use.setPosition(paper.x + 50, paper.y)
        use.setSize(paper.width, paper.height)
    }

    fun backpackScroll(i: Int) {
        paper = BaseActor(0f, 0f, stage)
        paper.loadTexture("paperboarder.png")
        paper.setPosition(100f, 300f)
        paper.setSize(390f, 780f)

        name = Label("", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        use = Label("", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        stage.addActor(name)
        stage.addActor(use)

        var table = Table()
        var container = Table()

        var card = MaterialCard("a","equipment.png","useassssssssssssssssssssssssssssssssssssssssss")
        var card1 = MaterialCard("b","equipment.png","useb")
        var card2 = MaterialCard("c","equipment.png","usec")
        var card3 = MaterialCard("d","equipment.png","usec")
        var card4 = MaterialCard("e","equipment.png","usec")
        val d = deck
        var card5 = ItemCard("f","equipment.png","usec",1,1,1,1,1,1,1,d)
        var card6 = ItemCard("g","equipment.png","usec",1,1,1,1,1,1,1,d)
        var card7 = ItemCard("h","equipment.png","usec",1,1,1,1,1,1,1,d)
        var card8 = ItemCard("i","equipment.png","usec",1,1,1,1,1,1,1,d)
        var card9 = MergableCard("j","equipment.png","usec")
        storage.add(card)
        storage.add(card1)
        storage.add(card2)
        storage.add(card3)
        storage.add(card4)
        storage.add(card5)
        storage.add(card6)
        storage.add(card7)
        storage.add(card8)
        storage.add(card9)


        for (c in storage.getStored()) {
            if (i == 1 && c is MaterialCard) {
                continue
            }
            if (i == 2 && c is ItemCard) {
                continue
            }
            val cardActor = BaseActor(0f, 0f, stage)
            cardActor.loadTexture(c.img)
            cardActor.setSize(500f, 600f)
            cardActor.addListener(object : InputListener() {
                override fun touchDown(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    button: Int
                ): Boolean {
                    showInfo(c)
                    return true
                }
            })
            table.add(cardActor)
        }
        table.row()
        //table.setSize(screenWidth - 300, 860f)

        scrollPane = ScrollPane(table)
        scrollPane.scrollTo(0f,0f,0f,0f)

        container.add(scrollPane)
        container.setPosition(300f, 260f)
        container.setSize(screenWidth - 100, 860f)
        var skin = Skin()
        skin.add("logo", Texture("bpback.png"));
        container.background(skin.getDrawable("logo"))
        container.row()
        container.getCell(scrollPane).size(1900f,600f)
        stage.addActor(container)

        back = BaseActor(0f, 0f, stage)
        back.loadTexture("EndTurnButton.png")
        back.setSize(250f, 200f)
        back.centerAtPosition(screenWidth / 5 * 0 + back.width / 2 + 300, (screenHeight / 2) + back.height / 2 - 700)
        // Set event action
        back.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                Awake.setActiveScreen(BackpackScreen())
                return true
            }
        })

    }

    override fun initialize() {
        Gdx.input.inputProcessor = stage
        //stage.addActor(countdownLabel)
        //countdownLabel.setPosition(screenWidth/2 - countdownLabel.width/2, screenHeight/2 + countdownLabel.height/2)

        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragonBackground.png")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)


        weapon = BaseActor(0f, 0f, stage)
        weapon.loadTexture("equipment.png")
        weapon.setSize(800f, 850f)
        weapon.centerAtPosition(screenWidth / 3, (screenHeight / 3) + weapon.height / 2 - 200)
        // Set event action
        weapon.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                weapon.remove()
                material.remove()
                back.remove()
                backpackScroll(1)
                return true
            }
        })

        material = BaseActor(0f, 0f, stage)
        material.loadTexture("material.png")
        material.setSize(800f, 850f)
        material.centerAtPosition(screenWidth / 3 * 2, (screenHeight / 3) + material.height / 2 - 200)
        // Set event action
        material.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                weapon.remove()
                material.remove()
                back.remove()
                backpackScroll(2)
                return true
            }
        })

        back = BaseActor(0f, 0f, stage)
        back.loadTexture("EndTurnButton.png")
        back.setSize(300f, 350f)
        back.centerAtPosition(screenWidth / 5 * 0 + back.width / 2 + 200, (screenHeight / 2) + back.height / 2 - 600)
        // Set event action
        back.addListener(object : InputListener() {
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