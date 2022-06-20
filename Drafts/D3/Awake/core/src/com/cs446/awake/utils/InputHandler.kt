package com.cs446.awake.utils

import com.badlogic.gdx.InputProcessor

class InputHandler: InputProcessor {
    companion object {
        var inputX: Int = 0
        var inputY: Int = 0

        var down: Boolean = false
    }

    override fun keyDown(keycode: Int): Boolean {
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        inputX = screenX
        inputY = screenY
        down = true

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        inputX = screenX
        inputY = screenY
        down = false

        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        inputX = screenX
        inputY = screenY
        down = true

        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }
}