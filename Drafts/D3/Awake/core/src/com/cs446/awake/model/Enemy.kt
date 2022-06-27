package com.cs446.awake.model

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.utils.Array
import com.cs446.awake.utils.AbstractActor
import com.cs446.awake.utils.BaseActor

class Enemy(val images: Array<String?>, charName: String, HP: Int, energy: Int, strength: Int, deck: Deck, state: MutableList<State>, playerType: PlayerType) : Character(charName, HP, energy, strength, deck, state, playerType) {
    lateinit var healthBar : ProgressBar

    override fun updateHealth(HpChange: Int){
        HP += HpChange
        println("change")
        println(HP)
        healthBar.value = HP / 100f
    }
}