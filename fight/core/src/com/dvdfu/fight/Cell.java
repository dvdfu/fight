package com.dvdfu.fight;

public class Cell {
	enum Status {
		ON_FIRE, NONE
	};

	Status status;
	int statusTimer;
	int x, y;
	float height;
	boolean alive;
	boolean used;

	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
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
			statusTimer = 60;
			break;
		case NONE:
		default:
			statusTimer = 0;
			break;
		}
	}
}
