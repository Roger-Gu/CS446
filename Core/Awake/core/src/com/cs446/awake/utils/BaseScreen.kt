package com.cs446.awake.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage

abstract class BaseScreen : Screen {

    lateinit var stage: Stage

    abstract fun initialize()

    abstract fun update(delta: Float)

    override fun show() {
        stage = Stage()
        initialize()
    }

    override fun render(delta: Float) {
        stage.act(delta)

        update(delta)

        Gdx.gl.glClearColor(0f,0f,0f,1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.draw()
    }


    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
    }
}