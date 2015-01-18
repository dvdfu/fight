package com.dvdfu.fight.screens;

import com.badlogic.gdx.controllers.Controllers;
import com.dvdfu.fight.MainGame;
import com.dvdfu.fight.components.GamepadComponent;

public class ControllerTestScreen extends AbstractScreen {
	GamepadComponent gamepad;

	public ControllerTestScreen(MainGame game) {
		super(game);
		gamepad = new GamepadComponent();
		Controllers.addListener(gamepad);
	}

	public void render(float delta) {
		if (gamepad.keyPressed(GamepadComponent.Button.A)) {
			System.out.println("SUCCESS!!");
		}
		gamepad.update();
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
