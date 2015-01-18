package com.dvdfu.fight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Cell extends BoardUnit {
	enum Status {
		ON_FIRE, NONE
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
				status = Status.NONE;
			}
		}
	}

	public void setStatus(Status status) {
		this.status = status;
		switch (status) {
		case ON_FIRE:
			statusTimer = 120;
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
