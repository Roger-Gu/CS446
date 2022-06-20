package com.cs446.awake.utils

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.Viewport
import java.awt.Rectangle

// For later use
class MenuItem(x: Float, y: Float, text: String, font: BitmapFont) {

    interface Clickable {
        fun onClick(item: MenuItem)
    }

    var x: Float = x
        private set
    var y: Float = y
        private set

    var text: String = text
        private set

    var listener: Clickable? = null
    var highlight: Boolean = false

    private var font: BitmapFont = font
    private var glyph: GlyphLayout = GlyphLayout(font, text)
    private var boundingRect: Rectangle = Rectangle((x-glyph.width/2f).toInt(),
        (y-glyph.height/2f).toInt(), glyph.width.toInt(), glyph.height.toInt()
    )

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun update(dt: Float, cam: Camera, viewport: Viewport) {
        // if listener is set then we calculate for its onclick update
        listener?.let {
            // convert screen coordinate to world coordinate
            val location = Vector3(InputHandler.inputX.toFloat(), InputHandler.inputY.toFloat(), 0f)
            cam.unproject(location, viewport.screenX.toFloat(), viewport.screenY.toFloat(), viewport.screenWidth.toFloat(), viewport.screenHeight.toFloat())

            if (InputHandler.down && boundingRect.contains(location.x.toDouble(), location.y.toDouble())) {
                it.onClick(this)
            }
        }
    }

    fun render(sb: SpriteBatch) {
        val oldColor = font.color

        font.color = if (highlight) Color.RED else Color.WHITE
        glyph.setText(font, text)

        font.draw(sb, glyph, x - glyph.width / 2f, y + glyph.height / 2f)

        font.color = oldColor
    }
}