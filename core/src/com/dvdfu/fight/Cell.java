package com.dvdfu.fight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Cell extends BoardUnit {
	enum Status {
		ON_FIRE, BIG_FIRE, NONE
	};

	Status status;
	int statusTimer;
	boolean alive;
	boolean used;
	boolean targeted;

	public Cell(Board board, int xCell, int yCell) {
		super(board);
		this.xCell = xCell;
		this.yCell = yCell;
		x = xCell * board.cellWidth;
		y = yCell * board.cellHeight;
		zPriority = 2;
		alive = true;
	}

	public void update() {
		if (statusTimer > 0) {
			statusTimer--;
			if (statusTimer == 0) {
				statusTimerUp();
			}
		}
	}
	
	public void statusTimerUp() {
		switch (status) {
		case BIG_FIRE:
			setStatus(Status.ON_FIRE);
			break;
		case ON_FIRE:
		case NONE:
		default:
			setStatus(Status.NONE);
			break;
		}
	}

	public void setStatus(Status status) {
		this.status = status;
		switch (status) {
		case ON_FIRE:
			statusTimer = 120;
			break;
		case BIG_FIRE:
			statusTimer = 15;
			break;
		case NONE:
		default:
			statusTimer = 0;
			break;
		}
	}

	public void draw(SpriteBatch batch) {
		targeted = false;
	}
}
