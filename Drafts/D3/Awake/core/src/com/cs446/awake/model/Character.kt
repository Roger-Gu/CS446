package com.cs446.awake.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array

abstract class Character (val charName: String, val maxHP: Int, val maxEnergy: Int, val maxStrength: Int, val deck: Deck, var state: MutableList<State>) {
    var hand: MutableList<ActionCard> = mutableListOf()
    var energy = maxEnergy
    var strength = maxStrength
    var HP = maxHP

    fun useCard(card: ActionCard) {
        updateEnergy(card.energyCost)
        updateStrength(card.strengthCost)
        updateHealth(card.healthChange)
        for (s in card.Effect) {
            updateState(s)
        }
    }

    open fun selectHandCard(): ActionCard {
        return hand[0]
    }

    // add try catch block for 1. empty deck 2.hand full
    open fun drawCard(){
        val c = deck.pop() // deck should shuffle when it is empty
        hand.add(c)
    }

    fun updateState(newState: State){
        var curState: State? = state.find {card -> card.stateName == newState.stateName}
        if (curState == null) {
            state.add(newState)
        } else {
            curState.extend(newState)
        }
    }

    fun removeState(removedStates: MutableList<String>){
        var d = mutableListOf<State>()
        for (s in state){
            if (removedStates.contains(s.stateName)){
                d.add(s)
            }
        }
        state.removeAll(d)

    }

    open fun preRound() {
        energy = maxEnergy
        strength = maxStrength
    }

    open fun endRound() {}

    open fun postRound(){
        for (s in state) {
            s.apply()
        }
        hand.clear()
    }

    fun updateHealth(HpChange: Int){
        HP += HpChange
    }

    fun updateStrength(strengthChange: Int) {
        strength += strengthChange
    }

    fun updateEnergy(energyChange: Int) {
        energy += energyChange
    }

    fun isDead(): Boolean {
        return HP <= 0
    }

}