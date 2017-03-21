package com.shadower;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ShaderMain extends ApplicationAdapter {

	public static final int width = 1024;
	public static final int height = 1024;
    String inputHeightMap = "hills.png";

    Batch batch;
    LightSource lightSource;
    PixelMap pixelMap;


	/**
	 * This method creates the light application and loads all necessary objects
	 */
	@Override
	public void create () {
		Gdx.graphics.setWindowedMode(ShaderMain.width, ShaderMain.height);

		batch = new SpriteBatch();
		this.lightSource = new LightSource(ShaderMain.width / 2, ShaderMain.height / 2);
		this.pixelMap = new PixelMap(inputHeightMap);

	}


	/**
	 * This is called every frame and renders the application.
	 */
	@Override
	public void render () {
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		this.lightSource.render(batch);
		this.pixelMap.render(batch, this.lightSource.getAngle());

		batch.end();

		this.pixelMap.cleanup();
	}


	/**
	 * This cleans up the project before it is closed.
	 */
	@Override
	public void dispose () {
		batch.dispose();
		this.lightSource.dispose();
		this.pixelMap.dispose();
	}
}
