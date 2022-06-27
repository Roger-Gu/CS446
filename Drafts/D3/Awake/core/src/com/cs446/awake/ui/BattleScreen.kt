package com.cs446.awake.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.cs446.awake.model.Board
import com.cs446.awake.utils.BaseActor
import com.cs446.awake.utils.BaseScreen
import com.cs446.awake.utils.DragDropActor


class BattleScreen(private val board: Board) : BaseScreen(){

    override fun initialize() {
        super.initialize()

        val wid = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()


        val bgImg = Texture("dungeon.png")
        val background = BaseActor(0f, 0f, stage)
        background.loadTexture("dungeon.png")
        background.setSize(wid, (wid / bgImg.width * bgImg.height))
        background.centerAtPosition(wid/2, height/2)

        // Eric Start: To achieve not use then move back to deck.
        var selectedCard: BaseActor? = null
        var selectedPosX: Float = 0.0f
        var selectedPosY: Float = 0.0f
        // Eric End

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

        // enemy health
        val enemyHP = board.enemy.HP

        // health bar
        // TODO: Fit health bar into Actor model
        // bar background as red
        var pixmap = Pixmap(100, 20, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.RED)
        pixmap.fill()
        var drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        val progressBarStyle = ProgressBarStyle()
        progressBarStyle.background = drawable

        // health as green
        pixmap = Pixmap(0, 20, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.GREEN)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        progressBarStyle.knob = drawable

        pixmap = Pixmap(100, 20, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.GREEN)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        progressBarStyle.knobBefore = drawable


        val healthBar = ProgressBar(0.0f, 1.0f, 0.01f, false, progressBarStyle)
        healthBar.value = 1.0f
        healthBar.setAnimateDuration(0.25f)
        healthBar.setBounds(500F, 1000F, 1000F, 20F)

        stage.addActor(healthBar)
        healthBar.value = enemyHP / 100.0f // TODO: everytime deal damage, update this


        // Border for card
        val borderTexture = Texture(Gdx.files.internal("highlight_border.png")) // TODO: change the texture
        val borderImage = Image(borderTexture)

        val cardImg = Texture("card_empty.png")
        val cardWidth = cardImg.width.toFloat()
        var intervalWid = 40f
        val cardTotal = board.player.hand.size -1
        for ((handIndex, card) in board.player.hand.withIndex()) {
            val cardActor = DragDropActor(0f, 0f, stage, enemy)
            cardActor.loadTexture(card.img)
            // y-coord is set to hide the bottom half, click to elevate?
            cardActor.centerAtPosition(0f, height - 1000f)
            cardActor.moveBy((wid-(cardTotal*cardWidth + (cardTotal-1)*intervalWid))/2 + handIndex*cardWidth,0f)
            cardActor.setOnDropIntersect {
                println("intersected when dropped")
                cardActor.remove()
                borderImage.remove()
            }
            cardActor.setOnDropNoIntersect {
                cardActor.setPosition(cardActor.startX, cardActor.startY)
                borderImage.remove()
            }
            cardActor.setOnDragIntersect {
                //println("CARD USING?")
                val borderWidth = 30
                borderImage.setSize( cardActor.width + borderWidth * 2, cardActor.height + borderWidth * 2)
                borderImage.setPosition( cardActor.x - borderWidth, cardActor.y - borderWidth )
                stage.addActor(borderImage)
                cardActor.toFront()
            }
            cardActor.setOnDragNoIntersect {
                borderImage.remove()
            }
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

//        board.player.updateState(State("Burn", 3))
//        board.player.updateState(State("Freeze", 3))
//        board.player.updateState(State("Poison", 3))
//        board.enemy.updateState(State("Burn", 3))
//        board.enemy.updateState(State("Freeze", 3))


    }

    override fun update(delta: Float) {
    }
}