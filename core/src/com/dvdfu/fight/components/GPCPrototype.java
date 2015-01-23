package com.dvdfu.fight.components;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class GPCPrototype implements ControllerListener, InputProcessor {
	Controller controller;
	ArrayList<Device> devices;
	
	public class Device {
		boolean[] keys; // true = pressed
		boolean[] preKeys;
		
		public Device() {
			keys = new boolean[Button.values().length];
			preKeys = new boolean[keys.length];
		}
	}
	
	public enum Button {
		RIGHT, LEFT, UP, DOWN, L, R, A, B, X, Y, START, SELECT
	}

	public GamepadComponent() {
		Gdx.input.setInputProcessor(this);
		devices = new ArrayList<Device>();
		devices.add(new Device()); // keyboard
		if (Controllers.getControllers().size > 0) {
			controller = Controllers.getControllers().first();
			Controllers.addListener(this);
			devices.add(new Device());
		}
	}
	
	public void update() {
		for (int i = 0; i < keys.length; i++) {
			preKeys[i] = keys[i];
		}
	}
	
	public boolean keyDown(Button button) {
		return keys[button.ordinal()];
	}
	
	public boolean keyPressed(Button button) {
		return keys[button.ordinal()] && !preKeys[button.ordinal()];
	}
	
	public boolean keyReleased(Button button) {
		return !keys[button.ordinal()] && preKeys[button.ordinal()];
	}

	public void connected(Controller controller) {}
	public void disconnected(Controller controller) {}

	public boolean buttonDown(Controller controller, int buttonCode) {
		switch (buttonCode) {
			case 0: keys[Button.X.ordinal()] = true; break;
			case 1: keys[Button.A.ordinal()] = true; break;
			case 2: keys[Button.B.ordinal()] = true; break;
			case 3: keys[Button.Y.ordinal()] = true; break;
			case 4: keys[Button.L.ordinal()] = true; break;
			case 5: keys[Button.R.ordinal()] = true; break;
			case 6:
			case 7: break;
			case 8: keys[Button.SELECT.ordinal()] = true; break;
			case 9: keys[Button.START.ordinal()] = true; break;
		}
		return false;
	}

	public boolean buttonUp(Controller controller, int buttonCode) {
		switch (buttonCode) {
			case 0: keys[Button.X.ordinal()] = false; break;
			case 1: keys[Button.A.ordinal()] = false; break;
			case 2: keys[Button.B.ordinal()] = false; break;
			case 3: keys[Button.Y.ordinal()] = false; break;
			case 4: keys[Button.L.ordinal()] = false; break;
			case 5: keys[Button.R.ordinal()] = false; break;
			case 6:
			case 7: break;
			case 8: keys[Button.SELECT.ordinal()] = false; break;
			case 9: keys[Button.START.ordinal()] = false; break;
		}
		return false;
	}

	public boolean axisMoved(Controller controller, int axisCode, float value) {
		switch (axisCode) {
			case 0:
			if (value > 0.5f) {
				keys[Button.RIGHT.ordinal()] = true;
			} else if (value < -0.5f) {
				keys[Button.LEFT.ordinal()] = true;
			} else {
				keys[Button.RIGHT.ordinal()] = false;
				keys[Button.LEFT.ordinal()] = false;
			} break;
			case 1:
			if (value > 0.5f) {
				keys[Button.DOWN.ordinal()] = true;
			} else if (value < -0.5f) {
				keys[Button.UP.ordinal()] = true;
			} else {
				keys[Button.DOWN.ordinal()] = false;
				keys[Button.UP.ordinal()] = false;
			} break;
		}
		return false;
	}

	public boolean povMoved(Controller controller, int povCode, PovDirection value) { return false; }
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) { return false; }
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) { return false; }
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) { return false; }

	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Input.Keys.A: keys[Button.LEFT.ordinal()] = true; break;
		case Input.Keys.S: keys[Button.DOWN.ordinal()] = true; break;
		case Input.Keys.D: keys[Button.RIGHT.ordinal()] = true; break;
		case Input.Keys.W: keys[Button.UP.ordinal()] = true; break;
		case Input.Keys.UP: keys[Button.X.ordinal()] = true; break;
		case Input.Keys.DOWN: keys[Button.B.ordinal()] = true; break;
		case Input.Keys.LEFT: keys[Button.Y.ordinal()] = true; break;
		case Input.Keys.RIGHT: keys[Button.A.ordinal()] = true; break;
		case Input.Keys.SPACE: keys[Button.R.ordinal()] = true; break;
		}
		return false;
	}

	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Input.Keys.A: keys[Button.LEFT.ordinal()] = false; break;
		case Input.Keys.S: keys[Button.DOWN.ordinal()] = false; break;
		case Input.Keys.D: keys[Button.RIGHT.ordinal()] = false; break;
		case Input.Keys.W: keys[Button.UP.ordinal()] = false; break;
		case Input.Keys.UP: keys[Button.X.ordinal()] = false; break;
		case Input.Keys.DOWN: keys[Button.B.ordinal()] = false; break;
		case Input.Keys.LEFT: keys[Button.Y.ordinal()] = false; break;
		case Input.Keys.RIGHT: keys[Button.A.ordinal()] = false; break;
		case Input.Keys.SPACE: keys[Button.R.ordinal()] = false; break;
		}
		return false;
	}
	
	public boolean keyTyped(char character) { return false; }
	public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
	public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
	public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
	public boolean mouseMoved(int screenX, int screenY) { return false; }
	public boolean scrolled(int amount) { return false; }
}
