package com.libktx.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.kotcrab.vis.ui.VisUI
import com.libktx.game.lib.Countdown
import com.libktx.game.lib.GameContext
import com.libktx.game.lib.Resetable
import com.libktx.game.lib.setToOrtho
import com.libktx.game.network.services.BombService
import com.libktx.game.network.services.HueService
import com.libktx.game.network.services.TimerService
import com.libktx.game.screen.BombConfigScreen
import com.libktx.game.screen.BombState
import com.libktx.game.screen.HueConfigScreen
import com.libktx.game.screen.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import ktx.log.logger

private val log = logger<Game>()

class Game : KtxGame<KtxScreen>(), Resetable {

    private val context = GameContext()

    override fun create() {
        context.bind {
            VisUI.load()
            KtxAsync.initiate()

            bindSingleton(this@Game)
            bindSingleton<Batch>(SpriteBatch())
            bindSingleton(BitmapFont())
            bindSingleton(AssetManager())
            bindSingleton(ShapeRenderer())
            bindSingleton(Countdown(minutes = Config.countdownTime))
            bindSingleton(BombService())
            bindSingleton(HueService())
            bindSingleton(TimerService())

            bindSingleton(BombState())

            // The camera ensures we can render using our target resolution
            // pixels no matter what the screen resolution is.
            bindSingleton(OrthographicCamera().apply { setToOrtho(Config.screenSize) })

            addScreen(LoadingScreen(inject(), inject(), inject(), inject(), inject(), inject(), inject()))
            addScreen(BombConfigScreen(inject(), inject(), inject(), inject(), inject(), inject()))
            addScreen(HueConfigScreen(inject(), inject(), inject(), inject(), inject(), inject()))

        }
        setScreen<LoadingScreen>()
    }

    override fun reset() {
    }

    override fun dispose() {
        context.dispose()
        super.dispose()
    }
}
