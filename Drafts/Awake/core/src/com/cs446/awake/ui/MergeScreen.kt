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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.cs446.awake.Awake
import com.cs446.awake.model.*
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.DragDropActor

class MergeScreen() : BaseScreen() {

    // Screen size
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    // Background
    private lateinit var background : BaseActor

    // Merge Area
    private lateinit var mergeArea : BaseActor
    private var mergeDisplay = Container<Table>()
    private val mergeTable = Table()

    // Merge Button
    private lateinit var mergeCard : BaseActor

    // Clear Button
    private lateinit var clearMerge : BaseActor

    // Material Info
    private var mergeData = CardData(mutableListOf())
    private var materials = storage.getStored()

    // Material Table in a scroll pane
    private val materialTable = Table()
    // private val tableDisplay = AutoScrollPane(materialTable)
    private val tableDisplay = Container<Table>()

    // List store card actors
    private val mergeAreaCards = ArrayList<DragDropActor>()

    private var changeX = 0f
    private var toMerge = false
    // Split Pane that contains Merge Area at the top and Material Table at the bottom
    //private lateinit var splitPane : SplitPane

    // Timer variables
    private var worldTimer  = -1
    private var activeTimer = false
    private val timerLimit = 10 // Not 0 in case of concurrency issue.
    private var endTimeFcn : () -> Unit = {} // lambda function about what to do when time ends
    private var duringTimeFcn : () -> Unit = {} // lambda function about what to do when each frame passed.

    private lateinit var back : BaseActor


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

    /*
    private fun generateDragDrop(material: MergableCard, tableActor: BaseActor, cardStack: Stack) {
        val cardActor = DragDropActor(0f, 0f, stage, mergeDisplay, inTable = true)
        cardActor.toFront()
        cardActor.loadTexture("PoisonCard.png") //TODO: read card image & info
        // if not on the merge area, return back to the start position
        cardActor.setOnDropNoIntersect {
            cardActor.setPosition(cardActor.startX, cardActor.startY)
        }
        // if on the merge area, update merge list and storage
        cardActor.setOnDropIntersect {
            val oneMaterial : MergableCard = material.clone()
            oneMaterial.count = 1

            mergeData.add(oneMaterial)
            storage.remove(oneMaterial)
            println("Merge Area:")
            for (i in mergeData.getStored()) {
                println(i.cardName + " " + i.count.toString())
            }
            println("Storage:")
            for (i in storage.getStored()) {
                println(i.cardName + " " + i.count.toString())
            }

            mergeAreaCards.add(cardActor)

            if (material.count == 0) { // if empty
                // set to transparent before actually merge (leave a spot)
                tableActor.loadTexture("transparent.png")
            } else {
                // generate a new copy and add to stack
                generateDragDrop(material, tableActor, cardStack)
                cardStack.add(cardActor)
            }
        }
    }

     */

    private fun renderTable() {
        mergeTable.clear()
        materialTable.clear()
        for (material in materials) {
            val cardStack = Stack()
            if (material.count == 0) {
                val cardTableActor = BaseActor(0f, 0f, stage, inTable = true)
                cardTableActor.toFront()
                cardTableActor.loadTexture("transparent.png") //TODO: read card image & info
                cardStack.add(cardTableActor)
                continue
            }
            var count = material.count
            while (count > 0) {
                val cardActor = DragDropActor(0f, 0f, stage, mergeArea, inTable = true)
                cardActor.toFront()
                cardActor.loadTexture(material.img)
                cardActor.setSize(500f, 600f)

                // if not on the merge area, return back to the start position
                cardActor.setOnDropNoIntersect {
                    toMerge = false
                    cardActor.setPosition(cardActor.startX, cardActor.startY)
                }
                // if on the merge area, update merge list and storage
                cardActor.setOnDropIntersect {
                    toMerge = false
                    if (mergeAreaCards.size >= 7) {
                        cardActor.setPosition(cardActor.startX, cardActor.startY)
                    }
                    else if (!mergeAreaCards.contains(cardActor)){
                        val oneMaterial: MergableCard = material.clone()
                        oneMaterial.count = 1

                        mergeData.add(oneMaterial)
                        storage.remove(oneMaterial)
                        println("Merge Area:")
                        for (i in mergeData.getStored()) {
                            println(i.cardName + " " + i.count.toString())
                        }
                        println("Storage:")
                        for (i in storage.getStored()) {
                            println(i.cardName + " " + i.count.toString())
                        }

                        cardStack.removeActor(cardActor)
                        mergeAreaCards.add(cardActor)
                        val newCard = BaseActor(0f, 0f, stage, inTable = true)
                        newCard.toFront()

                        newCard.loadTexture(oneMaterial.img)

                        mergeTable.add(newCard).expandX().pad(10f).right()
                    }
                }

                // controls drag card or swipe table
                cardActor.setOnDragNoIntersect {
                    if (!toMerge) {
                        changeX = cardActor.x - cardActor.startX
                        tableDisplay.setPosition(tableDisplay.x + changeX, tableDisplay.y)
                    }
                }
                cardActor.setOnDragIntersect {
                    toMerge = true
                }
                cardStack.add(cardActor)
                count--
            }

            materialTable.add(cardStack).expandX().pad(10f).right()
        }
        tableDisplay.actor = materialTable
        stage.addActor(tableDisplay)

        mergeDisplay.actor = mergeTable
        stage.addActor(mergeDisplay)
        tableDisplay.toFront()
    }

    override fun initialize() {
        // test data
        storage.add(log)
        storage.add(log)
        storage.add(log)
        storage.add(log)
        storage.add(log)

        tableDisplay.setSize(screenWidth, screenHeight/2)
        tableDisplay.setPosition(0f,screenHeight -1000 )
        println("Init Storage:")
        for (i in storage.getStored()) {
            println(i.cardName + " " + i.count.toString())
        }
        materials = storage.getStored()

        Gdx.input.inputProcessor = stage

        // set background
        background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragonBackground.png") //TODO: background image
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        // set merge area
        mergeDisplay.setSize(screenWidth, screenHeight/2)
        mergeDisplay.setPosition(0f,screenHeight -600 )
        mergeArea = BaseActor(0f, 0f, stage)
        mergeArea.toFront()
        mergeArea.loadTexture("transparent.png") //TODO: transparent merge area
        mergeArea.setSize(screenWidth, screenHeight / 2)
        mergeArea.centerAtPosition(screenWidth / 2, screenHeight * 3 / 4)

        // init table
        renderTable()

        // merge card button
        mergeCard = BaseActor(0f, 0f, stage)
        mergeCard.loadTexture("EndTurnButton.png")
        mergeCard.setSize(250f, 200f)
        mergeCard.centerAtPosition(screenWidth - 250f, screenHeight / 2)
        mergeCard.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                // TODO: check empty
                // the output item after merging

                val outputCard = mergeData.merge()

                println("Merge Clicked")
                if (outputCard != null) {
                    println("Card Merged")
                    storage.add(outputCard)

                    mergeAreaCards.clear()
                    mergeData = CardData(mutableListOf())

                    val txt = Label("Congratulation! You have successfully merged this card. Click to collect the card.", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
                    txt.setSize(screenWidth/3, screenHeight/2)
                    txt.setPosition(screenWidth/5, screenHeight/2)
                    txt.setFontScale(0.5f)
                    txt.wrap = true
                    // Menu Bar Picture
//                    val bgPixmap = Pixmap(1, 1, Pixmap.Format.RGB565)
//                    bgPixmap.setColor(Color.GRAY)
//                    bgPixmap.fill()
//                    val textureRegionDrawableBg = TextureRegionDrawable(TextureRegion(Texture(bgPixmap)))
//                    val menuBar = Table()
//                    menuBar.background = textureRegionDrawableBg
//                    menuBar.setPosition(0f, screenHeight - 160f)
//                    menuBar.setSize(screenWidth, screenHeight/2)
//                    menuBar.add(txt)
                    stage.addActor(txt)

                    mergeTable.clear()
                    materialTable.clear()

                    // add this item into storage
                    val outputCardActor = BaseActor(0f, 0f, stage)
                    outputCardActor.toFront()
                    outputCardActor.loadTexture("skeleton1.png") //TODO: read card image & info
                    outputCardActor.setPosition(screenWidth/2 + 100, screenHeight/2 - 300)
                    outputCardActor.addListener(object : InputListener() {
                        override fun touchDown(
                            event: InputEvent?,
                            x: Float,
                            y: Float,
                            pointer: Int,
                            button: Int
                        ): Boolean {
                            // refresh table
                            txt.isVisible = false
                            outputCardActor.remove()
                            renderTable()

                            return true
                        }
                    })
//
//                    // remove card after several seconds
//                    val duringTime : () -> Unit = { outputCardActor.setOpacity(worldTimer / 60f) }
//                    val endTime : () -> Unit = {
//                        outputCardActor.remove()
//                    }
//                    startTimer(180, endTime, duringTime) // about 3 seconds
                } else {
                    println("Invalid Merge")
                }

                // TODO: show this card and add this card to item list, generate the cardActor
                return true
            }
        })

        // clear merge button
        clearMerge = BaseActor(0f, 0f, stage)
        clearMerge.loadTexture("EndTurnButton.png")
        clearMerge.setSize(250f, 200f)
        clearMerge.centerAtPosition(screenWidth / 2, (screenHeight / 2) + clearMerge.height / 2 - 700)
        clearMerge.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                println("Clear Clicked")
                // backend restore cards
                storage.append(mergeData)
                mergeData = CardData(mutableListOf())

                // clear merge area
                mergeAreaCards.clear()

                // refresh table
                renderTable()

                return true
            }
        })

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
                Awake.setActiveScreen(VillageScreen())
                return true
            }
        })
    }

    override fun update(delta: Float) {
        runTimer()
    }

}