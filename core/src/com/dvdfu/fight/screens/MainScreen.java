package com.dvdfu.fight.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dvdfu.fight.Board;
import com.dvdfu.fight.MainGame;

public class MainScreen extends AbstractScreen {
	SpriteBatch batch;
	OrthographicCamera camera;
	Board board;

	public MainScreen(MainGame game) {
		super(game);
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(120, 80, 0);
		camera.zoom = 1 / 2f;
		board = new Board(10, 10);
	}

	public void render(float delta) {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		board.draw(batch);
		batch.end();
	}

	public void resize(int width, int height) {
	}

	public void show() {
	}

	public void hide() {
	}

	public void pause() {
	}

	public void resume() {
	}

	public void dispose() {
	}

}