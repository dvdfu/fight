package com.dvdfu.fight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class BoardUnit {
	Board board;
	float x, y;
	int xCell, yCell;
	int zPriority;
	float height;
	
	public BoardUnit(Board board) {
		this.board = board;
	}
	
	public abstract void update();
	
	public abstract void draw(SpriteBatch batch);
	
	public int getZIndex() {
		// intended to be overwritten e.g. by Player
		return yCell;
	}
}
