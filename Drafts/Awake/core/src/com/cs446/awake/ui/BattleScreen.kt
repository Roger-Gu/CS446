package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Timer
import com.cs446.awake.Awake
import com.cs446.awake.model.*
import com.cs446.awake.utils.*

// Core Screen of the battle, exit only when battle ends, will not save data if exit.
// TODO: Graphic:
//   * Background Picture:   dungeon.png -> battle.png
//   * State Pictures (3~5): "${state.lowercase()}.png"
//   * Card Border Pic: highlight_border.png
// TODO: Next Screen: DungeonScreen
//   * Go to: when battle ends
//   * Return: NA
// Prev Screen: BattleEnterScreen
class BattleScreen(private val board: Board) : BaseScreen(){
    //// Variable of position related
    // Size of the full screen
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()
    // Spacing between state icons
    private val intervalWid = 10f

    //// Variable of data related
    // All States TODO: Should not be here, should be in some global data
    private val stateList = Array<String>(arrayOf("Burn", "Freeze", "Poison", "Paralysis", "Sleep"))

    //// Variable of display related
    private lateinit var enemy : BaseActor
    private lateinit var infoAIRound : Label
    private lateinit var infoPlayerRound : Label
    private lateinit var finishPlayerRound : BaseActor
    private val cardList = ArrayList<DragDropActor>() // Used for cleaning

//    private val playerDropTarget : BaseActor

    // ERIC TEST
    var worldTimer = 180
    val countdownLabel = Label(String.format("%03d", worldTimer), Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))

    // 1. Render Card if player turn
    // 2. Tell enemy and player turn started
    private fun startTurn() {
        // 1. Render Card if player turn
        if (!board.isAITurn()) {
            // Player's turn
            cardList.clear()
            renderCard()
        }
    }


    // Function that render player's card on the screen.
    // Call at the beginning of the player's turn.
    // TODO: The Card should only be movable at player's turn.
    // TODO: The Card can apply to enemy and player itself.
    private fun renderCard() {
        // Border for card
        val borderTexture =
            Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
        val borderImage = Image(borderTexture)

        // Card Actor
        // TODO: Click and show card info
        val cardTotal = board.player.hand.size - 1
        for ((handIndex, card) in board.player.hand.withIndex()) {
            // TODO: Change target enemy to player for heal card, need an area to drop for player
            val cardActor = DragDropActor(0f, 0f, stage, enemy)
            cardActor.loadTexture(card.img)
            cardActor.centerAtPosition(0f, screenHeight - 950f)
            cardActor.moveBy(
                (screenWidth - (cardTotal * cardActor.width + (cardTotal - 1) * intervalWid)) / 2 + handIndex * (cardActor.width + intervalWid),
                0f
            )
            // Event when card drag hit enemy TODO: add when hit player itself
            cardActor.setOnDropIntersect {
                println("intersected when dropped")
                // TODO: check later on when the turn is not player, should not able to move.
                //   if (board.checkTurn(board.player)) {
                cardActor.remove()
                board.removeCard(card)
                board.notify(card)
                borderImage.remove()
            }
            // Event when card drag not hit enemy, go back to original position
            cardActor.setOnDropNoIntersect {
                cardActor.setPosition(cardActor.startX, cardActor.startY)
                borderImage.remove()
            }
            // Event when card is being dragged
            cardActor.setOnDragIntersect {
                val borderWidth = 30
                borderImage.setSize(
                    cardActor.width + borderWidth * 2,
                    cardActor.height + borderWidth * 2
                )
                borderImage.setPosition(cardActor.x - borderWidth, cardActor.y - borderWidth)
                stage.addActor(borderImage)
                cardActor.toFront()
            }
            // Event when card is released from drag
            cardActor.setOnDragNoIntersect {
                borderImage.remove()
            }
            // Add current card to the cardList so that can be cleaned each round.
            cardList.add(cardActor)
        }
    }


    // Function that initialize Battle View:
    //   * background
    //   * enemy display
    //   * state of player, state of enemy
    //   * [not show] game turn indicator label
    //   * [not show] player end turn button
    // Property change:
    //   * State - opacity: by Character
    //   * Health - Bar: by Character
    //   * Energy - Bar: by Player
    private fun battleScreen() {
        stage.clear()
        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dungeon.png")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth / 2, screenHeight / 2)

        // Enemy Animation (or picture)
        // TODO: 素材: 敌人: 动画素材 or 图片素材
        enemy = BaseActor(0f, 0f, stage)
        // TODO: if Animation
        enemy.loadAnimationFromFiles(board.enemy.images, 0.5f, true)
        // TODO: if picture
        // enemy.loadTexture("skeleton1.png")
        // TODO: remove this line when above done.
        enemy.centerAtPosition(screenWidth / 2, screenHeight)
        enemy.moveBy(0f, -550f)

        // State
        for ((stateIndex, state) in stateList.withIndex()) {
            val stateImg = Texture("${state.lowercase()}.png")
            val stateWidth = stateImg.width.toFloat()

            // State of player
            val playerStateActor = BaseActor(0f, 0f, stage)
            playerStateActor.loadTexture("${state.lowercase()}.png")
            playerStateActor.centerAtPosition(-900f, screenHeight - 1000f)
            playerStateActor.moveBy(
                (screenWidth - (4 * stateWidth + 3 * intervalWid)) / 2 + stateIndex * stateWidth,
                0f
            )
            playerStateActor.setOpacity(0.3f)

            // State of enemy
            val enemyStateActor = BaseActor(0f, 0f, stage)
            enemyStateActor.loadTexture("${state.lowercase()}.png")
            enemyStateActor.centerAtPosition(800f, screenHeight - 100f)
            enemyStateActor.moveBy(
                (screenWidth - (4 * stateWidth + (4 - 1) * intervalWid)) / 2 + stateIndex * stateWidth,
                0f
            )
            enemyStateActor.setOpacity(0.3f)

            // Future property change by Character class updateState(...)
            board.player.characterStateMap[state] = playerStateActor
            board.enemy.characterStateMap[state] = enemyStateActor
        }

        // TODO: Better display (using pic or other text font)
        // AI-Round Indicator
        // * Not yet added to stage, only added when AI turn starts
        infoAIRound = Label("AI-Round", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        infoAIRound.y = screenHeight / 2 + infoAIRound.height / 2

        // Player-Round Indicator
        // * Not yet added to stage, only added when Player turn starts
        infoPlayerRound = Label("Your-Round", Label.LabelStyle(BitmapFont(Gdx.files.internal("Arial120Bold.fnt")), Color.WHITE))
        infoPlayerRound.y = screenHeight / 2 + infoPlayerRound.height / 2

        // Finish-Player-Round button
        // * Not yet added to stage, only added when Player turn starts
        finishPlayerRound = BaseActor(0f, 0f, stage)
        finishPlayerRound.remove() // Remove from display stage
        finishPlayerRound.loadTexture("EndTurnButton.png")
        finishPlayerRound.centerAtPosition(screenWidth - 250f, 150f)
        finishPlayerRound.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                finishPlayerRound.remove()
                board.endRound()
                return true
            }
        })

        // Bars
        // TODO: NOTICE: strength bar will not be used in final game design, need remove.
        stage.addActor(board.enemy.healthBar)
        stage.addActor(board.player.healthBar)
        stage.addActor(board.player.energyBar)
    }

    // Function that initialize Battle Start View:
    //   * background
    //   * text info (touch to start)
    private fun battleEnterScreen() {
        // Background Picture
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dragon.jpeg")
        background.setSize(screenWidth, (screenWidth / background.width * background.height))
        background.centerAtPosition(screenWidth/2, screenHeight/2)
        background.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                board.startGame()
                board.enemy.initBars()
                board.player.initBars()
                battleScreen()
                return true
            }
        })

        // Text Info
        val start = BaseActor(0f, 0f, stage)
        start.loadTexture("start-message.png")
        start.centerAtPosition(screenWidth/2, screenHeight)
        start.moveBy(0f, -800f)
    }


    override fun initialize() {
        Gdx.input.inputProcessor = stage
        battleEnterScreen()
        // ERIC TEST
//        stage.addActor(countdownLabel)
//        countdownLabel.x = screenWidth / 2
//        countdownLabel.y = screenHeight / 2
    }


    override fun update(delta: Float) {
        worldTimer--
        countdownLabel.setText(String.format("%03d", worldTimer))
    }
}

