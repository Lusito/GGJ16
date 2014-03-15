package de.hochschuletrier.gdw.ws1314.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.input.InputInterceptor;
import de.hochschuletrier.gdw.commons.gdx.state.GameState;
import de.hochschuletrier.gdw.commons.gdx.state.transition.SplitHorizontalTransition;
import de.hochschuletrier.gdw.commons.gdx.utils.DrawUtil;
import de.hochschuletrier.gdw.ws1314.Main;
import de.hochschuletrier.gdw.ws1314.shaders.DemoShader;

/**
 * Menu state
 *
 * @author Santo Pfingsten
 */
public class MainMenuState extends GameState implements InputProcessor {

    public static final int WALKING_SPEED = 100;

    private Music music;
    private Sound click;
	private Texture logo;
	private Animation walking;
    private float x = 0;
    private boolean useShader;

    private DemoShader demoShader;
    InputInterceptor inputProcessor;

    public MainMenuState() {
    }

    @Override
    public void init(AssetManagerX assetManager) {
        super.init(assetManager);

		logo = assetManager.getTexture("logo");
		walking = assetManager.getAnimation("walking");
        music = assetManager.getMusic("menu");
        click = assetManager.getSound("click");

        music.setLooping(true);
//        music.play();
        demoShader = new DemoShader(Gdx.files.internal("data/shaders/demo.vertex.glsl"),
                Gdx.files.internal("data/shaders/demo.fragment.glsl"));

        inputProcessor = new InputInterceptor(this) {
            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Keys.ESCAPE:
                        if(GameStates.GAMEPLAY.isActive())
                            GameStates.MAINMENU.activate(new SplitHorizontalTransition(500).reverse(), null);
                        else
                            GameStates.GAMEPLAY.activate(new SplitHorizontalTransition(500), null);
                        return true;
                }
                return isActive && mainProcessor.keyUp(keycode);
            }
        };
        Main.inputMultiplexer.addProcessor(inputProcessor);
    }

    @Override
    public void render() {
        DrawUtil.fillRect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Color.GRAY);

		DrawUtil.batch.draw(logo, 0, 0);

        if (useShader) {
            DrawUtil.batch.setShader(demoShader);
        }
		DrawUtil.batch.draw(walking.getKeyFrame(stateTime), x,
				512 - walking.getKeyFrame(0f).getRegionWidth());

        if (useShader) {
            DrawUtil.batch.setShader(null);
        }
    }

	float stateTime = 0f;
    @Override
    public void update(float delta) {
		stateTime += delta;
		x += delta * WALKING_SPEED;
        if (x > 1024) {
			x = -walking.getKeyFrame(0f).getRegionWidth();
        }
    }

    @Override
    public void onEnter() {
        inputProcessor.setActive(true);
    }

    @Override
    public void onLeave() {
        inputProcessor.setActive(false);
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.F11:
                useShader = !useShader;
                return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        click.play();
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
