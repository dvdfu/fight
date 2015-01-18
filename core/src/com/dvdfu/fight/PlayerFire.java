package com.dvdfu.fight;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.dvdfu.fight.components.GamepadComponent;

public class PlayerFire extends Player {
	boolean onFire;
	boolean attacking;
	float attackTimer;
	int attackRange;
	int numFires;
	LinkedList<Fireball> fireballs;
	
	public PlayerFire(Board board) {
		super(board);
		fireballs = new LinkedList<Fireball>();
		zPriority = 1;
	}
	
	public void update() {
		onFire = gp.keyDown(GamepadComponent.Button.A);
		if (Gdx.input.isKeyJustPressed(Input.Keys.F) && grounded) {
			onFire = !onFire;
		}
		if (!moving) {
			numFires = 0;
			for (int i = 0; i < board.width; i++) {
				for (int j = 0; j < board.height; j++) {
					if (board.getStatus(i, j) == Cell.Status.ON_FIRE) {
						numFires++;
					}
				}
			}
			moveTimerLength = 12 - numFires / 3;
		}
		
		if (gp.keyDown(GamepadComponent.Button.B)) {
			attacking = true;
			if (attackTimer < 3) {
				attackTimer += 0.1f;
			} else {
				attackTimer = 3;
			}
		} else {
			if (attackRange > 0) {
				LinkedList<Cell> cellList = new LinkedList<Cell>();
				for (int i = 0; i < board.width; i++) {
					for (int j = 0; j < board.height; j++) {
						if (Math.abs(xCell - i) + Math.abs(yCell - j) == attackRange) {
							cellList.add(board.getCell(i, j));
						}
					}
				}
				
				while (!cellList.isEmpty()) {
					Cell cell = cellList.remove();
					Fireball f = new Fireball(board, cell, xCell, yCell, height);
					fireballs.add(f);
					board.units.add(f);
				}
			}
			attacking = false;
			attackTimer = 0;
		}
		
		attackRange = (int) attackTimer;
		super.update();
	}
	
	public int getZIndex() {
		return Math.min(yCell, yCellNext);
	}
}
