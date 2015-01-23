package com.dvdfu.fight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dvdfu.fight.components.SpriteComponent;

public class Cell extends BoardUnit {
	enum Status {
		ON_FIRE, BIG_FIRE, NONE
	};

	Status status;
	int statusTimer;
	int playerID;
	int cellWidth, cellHeight;
	boolean alive;
	boolean used;
	boolean targeted;
	boolean damages;
	SpriteComponent tile;
	SpriteComponent firetile;

	public Cell(Board board, int xCell, int yCell) {
		super(board);
		this.xCell = xCell;
		this.yCell = yCell;
		cellWidth = board.cellWidth;
		cellHeight = board.cellHeight;
		x = xCell * board.cellWidth;
		y = yCell * board.cellHeight;
		alive = true;

		tile = new SpriteComponent(Const.atlas.findRegion("tile"));
		firetile = new SpriteComponent(Const.atlas.findRegion("firetile"), cellWidth);
		firetile.setSize(24, 24);
	}

	public void update() {
		if (statusTimer > 0) {
			statusTimer--;
			targeted = true;
			// if (status == Status.ON_FIRE) height += 0.1f;
			if (statusTimer == 0) {
				statusTimerUp();
			}
		}
	}

	public void statusTimerUp() {
		switch (status) {
		case BIG_FIRE:
			setStatus(Status.ON_FIRE, playerID);
			break;
		case ON_FIRE:
		case NONE:
		default:
			setStatus(Status.NONE, 0);
			break;
		}
	}

	public void setStatus(Status status, int id) {
		this.status = status;
		playerID = id;
		switch (status) {
		case ON_FIRE:
			statusTimer = 120;
			damages = true;
			break;
		case BIG_FIRE:
			statusTimer = 15;
			damages = true;
			break;
		case NONE:
		default:
			statusTimer = 0;
			damages = false;
			break;
		}
	}

	public void setTargeted(int id) {
		targeted = true;
		playerID = id;
	}

	public void draw(SpriteBatch batch) {
		tile.setSize(cellWidth, cellHeight);
		float ss = 1 - height / 100f;
		if (targeted) {
			switch (playerID) {
			case 1:
				tile.setColor(1, 0.8f, 0.6f);
				break;
			case 2:
				tile.setColor(0.6f, 0.8f, 1);
				break;
			default:
				tile.setColor(ss, ss, ss);
				break;
			}
		} else {
			tile.setColor(ss, ss, ss);
		}
		tile.draw(batch, xCell * cellWidth, yCell * cellHeight + height);
		if (board.p1.getCell() == this || board.p2.getCell() == this) {
			tile.setAlpha(0.3f);
			tile.setColor(0, 0, 0);
			tile.draw(batch, xCell * cellWidth, yCell * cellHeight + height);
			tile.setAlpha(1);
		}
		if (status == Cell.Status.ON_FIRE) {
			firetile.setFrame(statusTimer / 10);
			firetile.draw(batch, xCell * cellWidth, yCell * cellHeight + height);
		}
		if (status == Cell.Status.BIG_FIRE) {
			firetile.setSize(24, 64);
			firetile.setFrame(statusTimer / 10);
			firetile.draw(batch, xCell * cellWidth, yCell * cellHeight + height
					- 4);
			firetile.setSize(24, 24);
		}
		tile.setSize(cellWidth, height);
		tile.setColor(0.5f, 0.3f, 0.2f);
		tile.draw(batch, xCell * cellWidth, yCell * cellHeight);
		targeted = false;
	}
}
