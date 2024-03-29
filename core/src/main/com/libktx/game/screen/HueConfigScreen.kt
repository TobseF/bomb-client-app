package com.libktx.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.VisUI
import com.libktx.game.Config
import com.libktx.game.Game
import com.libktx.game.Preferences
import com.libktx.game.lib.addValueChangeListener
import com.libktx.game.lib.bind
import com.libktx.game.lib.rect
import com.libktx.game.lib.setClickListener
import com.libktx.game.network.services.HueService
import com.libktx.game.network.services.HueService.HueValue
import com.libktx.game.network.services.HueService.LightState.OFF
import com.libktx.game.network.services.HueService.LightState.ON
import ktx.graphics.use
import ktx.vis.table

/**
 * Config screen for the basic settings.
 * Also shows some info, like the bombs IP address.
 */
class HueConfigScreen( private val hueService: HueService,
                      val game: Game,
                      batch: Batch,
                      shapeRenderer: ShapeRenderer,
                      assets: AssetManager,
                      camera: OrthographicCamera) :
        AbstractScreen(batch, assets, camera, shapeRenderer) {

    private val stage: Stage = Stage(FitViewport(Config.screenSize.width, Config.screenSize.height))


    override fun render(delta: Float) {
        super.render(delta)
        shapeRenderer.use(ShapeRenderer.ShapeType.Filled) {
            it.rect(Color.LIGHT_GRAY, 0f, 0f, Config.screenSize.width, Config.screenSize.height)
        }

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun show() {
        val table = table(true) {
            defaults().center()
            setPosition(350f, 250f)

            textButton("Open Bomb Config") {
                setClickListener { game.setScreen<BombConfigScreen>() }
            }
            row()

            label("Hue IP:")
            textField(Preferences.hueIp) {
                bind(Preferences::hueIp)
            }.cell(grow = true)
            row()

            label("Hue Room: ")
            textField(Preferences.hueRoomName) {
                bind(Preferences::hueRoomName)
            }.cell(grow = true)

            row()

            label("Hue Key: ")
            val labelApiKey = textField(Preferences.hueApiKey ?: "").cell(grow = true, colspan = 2)

            row()

            val pairedCheckBox = checkBox("Paired") {
                isDisabled = true
                isChecked = hueService.isPaired()
            }.cell(colspan = 2)
            row()
            textButton("Reset-Hue") {
                setClickListener { resetHueSettings() }
            }.cell(fill = true)
            row()

            val white: Drawable = VisUI.getSkin().getDrawable("white")

            val previewColor = image(drawable = white) {
                height = 20f
                width = 20f
                color = Color.RED
            }.cell(fill = true)
            val previewColorValue = textField(HueValue.Green.hue.toString()) {
            }.cell(grow = true)
            slider(min = 0f, max = HueColor.COLOR_HUE_MAX) {
                value = HueValue.Green.hue.toFloat()
                addValueChangeListener { hue ->
                    previewColor.color = HueColor.fromHue(hue)
                    previewColorValue.text = hue.toInt().toString()
                }
            }.cell(grow = true)
            row()

            textButton("Test Light Red") {
                setClickListener { hueService.setLights(HueValue.Red, ON) }
            }.cell(fill = true)
            textButton("Test Light Color") {
                setClickListener { hueService.setLights(previewColorValue.text.toIntOrNull() ?: 0, ON) }
            }.cell(fill = true)
            textButton("Test Light Off") {
                setClickListener { hueService.setLights(HueValue.Red, OFF) }
            }.cell(fill = true)
            row()

            val buttonPair = textButton("Pair").cell(fill = true)
            val state = label("")

            buttonPair.setClickListener {
                state.setText("Pairing ...")
                pairedCheckBox.isChecked = hueService.pair()
                val hueApiKey: String? = Preferences.hueApiKey
                if (hueApiKey != null) {
                    labelApiKey.text = hueApiKey
                }
                state.clear()
            }
        }

        Gdx.app.input.inputProcessor = stage
        stage.addActor(table)
        stage.viewport.centerCamera()
    }


    class HueColor : Color(WHITE) {
        companion object {
            const val COLOR_HUE_MAX = 65280f

            fun fromHue(hue: Float): Color? {
                val hueAsLibGDX = 360f * (hue / COLOR_HUE_MAX)
                return HueColor().fromHsv(hueAsLibGDX, 1f, 1f)
            }
        }

    }

    private fun resetHueSettings() {
        println("Reset hueApiKey")
        Preferences.hueApiKey = null
    }

    private fun Viewport.centerCamera() {
        camera.position.set(worldWidth / 2, worldHeight / 2, 0f)
        camera.update()
    }
}