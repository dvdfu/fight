package com.dvdfu.fight.components;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

public class GamepadComponent {
	Controller controller;
	public enum Button {
		RIGHT, LEFT, UP, DOWN, L, R, A, B, X, Y, START, SELECT
	}

	public GamepadComponent() {
		controller = Controllers.getControllers().first();
	}
	
	public boolean keyDown(Button button) {
		switch (button) {
		case A:
			return controller.getButton(1);
		case B:
			return controller.getButton(2);
		case DOWN:
			return controller.getAxis(1) > 0.5f;
		case L:
			return false;
		case LEFT:
			return controller.getAxis(0) < -0.5f;
		case R:
			return controller.getButton(5);
		case RIGHT:
			return controller.getAxis(0) > 0.5f;
		case SELECT:
			return controller.getButton(8);
		case START:
			return controller.getButton(9);
		case UP:
			return controller.getAxis(1) < -0.5f;
		case X:
			return controller.getButton(0);
		case Y:
			return controller.getButton(3);
		default:
			return false;
		}
	}
}
