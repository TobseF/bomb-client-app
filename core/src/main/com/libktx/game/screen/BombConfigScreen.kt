package com.libktx.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Color.LIGHT_GRAY
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.libktx.game.Config
import com.libktx.game.Game
import com.libktx.game.Preferences
import com.libktx.game.assets.FontAssets
import com.libktx.game.assets.get
import com.libktx.game.lib.*
import com.libktx.game.network.services.BombService
import ktx.graphics.use
import ktx.vis.table
import java.util.*
import kotlin.math.max

/**
 * Config screen for the basic settings.
 * Also shows some info, like the bombs IP address.
 */
class BombConfigScreen(private val bombService: BombService,
                       val game: Game,
                       batch: Batch,
                       shapeRenderer: ShapeRenderer,
                       assets: AssetManager,
                       camera: OrthographicCamera) :
        AbstractScreen(batch, assets, camera, shapeRenderer) {

    private val stage: Stage = Stage(FitViewport(Config.screenSize.width, Config.screenSize.height))

    private var countdown: Long? = null


    override fun render(delta: Float) {
        super.render(delta)
        clearScreen(LIGHT_GRAY)

        if (countdown != null) {
            batch.use {
                val counterFont = assets[FontAssets.Counter]
                counterFont.drawWithShadow(it, Color.RED, getTimeAsString(), 280f, 450f)
            }

        }

        stage.act(delta)
        stage.draw()
    }

    private fun getTimeAsString(): String {
        return countdown?.let { TimeFormatter.getFormattedTimeAsString(max(it - System.currentTimeMillis(), 0L)) } ?: ""
    }


    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun show() {
        val table = table(true) {
            defaults().center()
            setPosition(350f, 250f)

            label("Bomb IP:")
            textField(Preferences.bombIp ?: "") {
                bindOptional(Preferences::bombIp)
            }.cell(grow = true)

            row()

            label("Bomb Time:")
            textField(Preferences.bombTimer.toString()) {
                bindInt(Preferences::bombTimer)
            }.cell(grow = true)

            row()

            label("Bomb Screen:")
            val bombScreen = textField {
            }.cell(grow = true)

            row()

            label("Bomb Ex Time:")
            val explosionTime = textField {
            }.cell(grow = true)

            row()


            label("Timer IP:")
            textField(Preferences.timerIp ?: "") {
                bindOptional(Preferences::timerIp)
            }.cell(grow = true)
            row()

            textButton("Open Hue Config") {
                setClickListener { game.setScreen<HueConfigScreen>() }
            }
            row()

            textButton("Stop Bomb") {
                setClickListener { stopBomb() }
            }.cell(minWidth = 120f, minHeight = 45f, align = Align.right)
            textButton("Read Bomb") {
                setClickListener {
                    bombService.info { bombInfo ->
                        bombScreen.text = bombInfo.screen
                        countdown = bombInfo.time
                        explosionTime.text = TimeFormatter.getFormattedDateAsTimeString(Date(bombInfo.time))
                    }
                }
            }.cell(minWidth = 120f, minHeight = 45f, align = Align.right)
            textButton("Reset Bomb") {
                setClickListener {
                    bombService.reset()
                    countdown = System.currentTimeMillis() + (60 * 1000 * Preferences.bombTimer)
                }
            }.cell(minWidth = 120f, minHeight = 45f, align = Align.right)
        }

        Gdx.app.input.inputProcessor = stage
        stage.addActor(table)
        stage.viewport.centerCamera()
    }

    private fun startBomb() {

    }

    private fun stopBomb() {
        bombService.stop()
    }


    private fun Viewport.centerCamera() {
        camera.position.set(worldWidth / 2, worldHeight / 2, 0f)
        camera.update()
    }
}