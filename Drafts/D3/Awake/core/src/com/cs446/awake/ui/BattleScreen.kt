package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Timer
import com.cs446.awake.model.Board
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen


class BattleScreen(private val board: Board) : BaseScreen(){
    private val wid = Gdx.graphics.width.toFloat()
    private val height = Gdx.graphics.height.toFloat()
    // Eric Start: To achieve not use then move back to deck.
    private var selectedCard: BaseActor? = null
    private var selectedPosX: Float = 0.0f
    private var selectedPosY: Float = 0.0f
    // Eric End

    private fun startGame() {
        if (!board.isAITurn()) {
            // Border for card
            val borderTexture = Texture(Gdx.files.internal("badlogic.jpg")) // TODO: change the texture
            val borderImage = Image(borderTexture)

            // Card Actor
            val cardImg = Texture("card_empty.png")
            val cardWidth = cardImg.width.toFloat()
            var intervalWid = 40f
            val cardTotal = board.player.hand.size -1
            for ((handIndex, card) in board.player.hand.withIndex()) {
                val cardActor = BaseActor(0f, 0f, stage)
                cardActor.loadTexture(card.img)
                // y-coord is set to hide the bottom half, click to elevate?
                cardActor.centerAtPosition(0f, height - 1000f)
                cardActor.moveBy((wid-(cardTotal*cardWidth + (cardTotal-1)*intervalWid))/2 + handIndex*cardWidth,0f)
                // drag and drop listener for card
                cardActor.addListener(object : DragListener() {
                    override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                        cardActor.moveBy(x - cardActor.width / 2, y - cardActor.height / 2)
                        // Control highlight border of cards
                        if (event != null) {
                            if (event.stageX in 730.0..1450.0 && event.stageY in 440.0..1080.0) {
                                val borderWidth = 5
                                borderImage.setSize( event.target.width + borderWidth * 2, event.target.height + borderWidth * 2)
                                borderImage.setPosition( event.target.x - borderWidth, event.target.y - borderWidth)
                                stage.addActor(borderImage)
                                event.target.toFront()
                            }
                            else {
                                borderImage.remove()
                            }
                        }
                    }
                })

                // touchDown and touchUp Actor
                cardActor.addListener(object : InputListener() {
                    override fun touchDown(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        button: Int
                    ): Boolean {
                        // Eric Start
                        selectedCard = cardActor
                        selectedPosX = cardActor.x
                        selectedPosY = cardActor.y
                        // Eric End

                        val actor = stage.hit(x, y, true)
                        if (actor != null) {
                            println("touchDown: " + actor.name.toString())
                        }
                        return true
                    }

                    override fun touchUp(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        button: Int
                    ) {
                        val actor = stage.hit(x, y, true)
                        if (event != null) {
                            if (event.stageX in 730.0..1450.0 && event.stageY in 440.0..1080.0) {
                                println("Card Used")
                                // TODO: call effect fun
                                // Eric Start
                                if (board.checkTurn(board.player)) {
                                    cardActor.remove()
                                    board.removeCard(card)
                                    board.notify(card)
                                } else {
                                    selectedCard?.x = selectedPosX
                                    selectedCard?.y = selectedPosY
                                }
                                // Eric End
                            }
                            else {
                                println("Card Not Used")
                                // TODO: card return to start position
                                // Eric Start
                                selectedCard?.x = selectedPosX
                                selectedCard?.y = selectedPosY
                                // Eric End
                            }
                        }
                        borderImage.remove()
                    }
                })

            }

            val stateImg = Texture("burn.png")
            val stateWidth = stateImg.width.toFloat()
            intervalWid = 40f

            // init player states

            val stateList = Array<String>(arrayOf("Burn","Freeze","Poison","Paralysis", "Sleep"))
            for ((stateIndex, state) in stateList.withIndex()){
                val stateActor = BaseActor(0f, 0f, stage)
                board.player.characterStateMap[state] = stateActor

                stateActor.loadTexture(state.lowercase() + ".png")
                stateActor.centerAtPosition(-800f, height -1000f)
                stateActor.moveBy((wid-(4*stateWidth + 3*intervalWid))/2 + stateIndex*stateWidth,0f)

                stateActor.setOpacity(0.3f)
            }


            // init enemy states

            for ((stateIndex, state) in stateList.withIndex()){
                val stateActor = BaseActor(0f, 0f, stage)
                board.enemy.characterStateMap[state] = stateActor

                stateActor.loadTexture(state.lowercase() + ".png")
                stateActor.centerAtPosition(800f, height-100f)
                stateActor.moveBy((wid-(4*stateWidth + (4-1)*intervalWid))/2 + stateIndex*stateWidth,0f)
                stateActor.setOpacity(0.3f)
            }
        }

        // Eric start


        if (board.isAITurn()) {
            val font = BitmapFont(Gdx.files.internal("Arial120Bold.fnt"))
            val buttonStyle = TextButtonStyle()
            buttonStyle.font = font
            val textButton = TextButton("Finish AI Round", buttonStyle)
            textButton.y = height / 2 + textButton.height / 2
            stage.addActor(textButton)
            textButton.addListener(object : InputListener() {
                override fun touchDown(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    button: Int
                ): Boolean {
                    board.activeAI()
                    textButton.remove()
                    board.postRound()
                    board.switchTurn()
                    stage.clear()
                    start()
                    return true
                }
            })
        } else {
            println("\nHIHI")
            val font = BitmapFont(Gdx.files.internal("Arial120Bold.fnt"))
            val buttonStyle = TextButtonStyle()
            buttonStyle.font = font
            val textButton = TextButton("Finish Your Round", buttonStyle)
            textButton.y = height / 2 + textButton.height / 2
            stage.addActor(textButton)
            textButton.addListener(object : InputListener() {
                override fun touchDown(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    button: Int
                ): Boolean {
                    textButton.remove()
                    board.postRound()
                    board.switchTurn()
                    start()
                    return true
                }
            })
        }
        // Eric end

//        board.player.updateState(State("Burn", 3))
//        board.player.updateState(State("Freeze", 3))
//        board.player.updateState(State("Poison", 3))
//        board.enemy.updateState(State("Burn", 3))
//        board.enemy.updateState(State("Freeze", 3))
    }

    fun start() {
        if (!board.isAITurn()) {
            val bgImg = Texture("dungeon.png")
            val background = BaseActor(0f, 0f, stage)
            background.loadTexture("dungeon.png")
            background.setSize(wid, (wid / bgImg.width * bgImg.height))
            background.centerAtPosition(wid / 2, height / 2)


            /*
        val imgs = Array<String?>()
        imgs.add("skeleton1.png")
        imgs.add("skeleton2.png")
        imgs.add("skeleton3.png")
        imgs.add("skeleton2.png")
         */
            // val enemyImg = Texture("skeleton1.png")
            val enemy = BaseActor(0f, 0f, stage)
            enemy.loadAnimationFromFiles(board.enemy.images, 0.5f, true)
            // enemy.loadTexture("skeleton1.png")
            enemy.centerAtPosition(wid / 2, height)
            enemy.moveBy(0f, -550f)
        }
        startGame()
    }

    override fun initialize() {
        Gdx.input.inputProcessor = stage;

        val bgImg = Texture("dungeon.png")
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dungeon.png")
        background.setSize(wid, (wid / bgImg.width * bgImg.height))
        background.centerAtPosition(wid/2, height/2)



        /*
        val imgs = Array<String?>()
        imgs.add("skeleton1.png")
        imgs.add("skeleton2.png")
        imgs.add("skeleton3.png")
        imgs.add("skeleton2.png")
         */
        // val enemyImg = Texture("skeleton1.png")
        val enemy = BaseActor(0f, 0f, stage)
        enemy.loadAnimationFromFiles(board.enemy.images, 0.5f, true)
        // enemy.loadTexture("skeleton1.png")
        enemy.centerAtPosition(wid/2, height)
        enemy.moveBy(0f,-550f)

        // Eric Start
        val font = BitmapFont(Gdx.files.internal("Arial120Bold.fnt"))
        val buttonStyle = TextButtonStyle()
        buttonStyle.font = font
        val textButton = TextButton("Start Fight!", buttonStyle)
        textButton.setPosition(wid/2 - textButton.width/2, height/2 + textButton.height/2)
        stage.addActor(textButton)
        textButton.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                textButton.remove()
                startGame()
                board.startGame()
                return true
            }
        })
        // Eric End

    }


    override fun update(delta: Float) {
    }
}