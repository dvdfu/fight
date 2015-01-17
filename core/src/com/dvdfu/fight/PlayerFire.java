package com.dvdfu.fight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.dvdfu.fight.components.GamepadComponent;

public class PlayerFire extends Player {
	boolean onFire;
	public PlayerFire(Board board) {
		super(board);
	}
	
	public void update() {
		onFire = gp.keyDown(GamepadComponent.Button.A);
		if (Gdx.input.isKeyJustPressed(Input.Keys.F) && grounded) {
			onFire = !onFire;
		}
		if (!moving) {
			if (board.getStatus(xCell, yCell) == Cell.Status.ON_FIRE && grounded) {
				moveTimerLength = 6;
			} else if (grounded) {
				moveTimerLength = 12;
			}
		}
		super.update();
	}
}
