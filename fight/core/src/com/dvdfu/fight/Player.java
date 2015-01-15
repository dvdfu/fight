package com.dvdfu.fight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dvdfu.fight.components.SpriteComponent;

public class Player {
	Board board;
	final float cellWidth = 24, cellHeight = 16;
	float x, y;
	float height;
	float moveTimerLength = 12;
	int moveTimer;
	float vSpeed;
	int xCell, yCell;
	int xCellNext, yCellNext;
	int xMove, yMove;
	boolean moving;
	SpriteComponent pspr;
	float boardHeight;
	boolean key1, key2;
	boolean grounded;

	public Player(Board board) {
		this.board = board;
		pspr = new SpriteComponent(Const.atlas.findRegion("player"));
		x = (xCell + 0.5f) * cellWidth;
		y = (yCell + 0.5f) * cellHeight;
	}

	public void update() {
		grounded = height == board.getHeight(xCell, yCell);
		boardHeight = Math.max(board.getHeight(xCell, yCell),
				board.getHeight(xCellNext, yCellNext));
		key1 = Gdx.input.isKeyPressed(Input.Keys.F);
		

		if (height + vSpeed < boardHeight) {
			vSpeed = 0;
			height = boardHeight;
		} else if (height == boardHeight) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				vSpeed = 5;
				height += vSpeed;
			}
		} else {
			height += vSpeed;
			vSpeed -= 0.3f;
			if (height + vSpeed < boardHeight) {
				vSpeed = 0;
				height = boardHeight;
			}
		}
		if (!moving) {
			if (Gdx.input.isKeyPressed(Input.Keys.A) && xCell > 0
					&& height >= board.getHeight(xCell - 1, yCell)) {
				moving = true;
				xMove = -1;
				moveTimer = 0;
			}
			else if (Gdx.input.isKeyPressed(Input.Keys.D) && xCell < board.width - 1
					&& height >= board.getHeight(xCell + 1, yCell)) {
				moving = true;
				xMove = 1;
				moveTimer = 0;
			}
			else if (Gdx.input.isKeyPressed(Input.Keys.S) && yCell > 0
					&& height >= board.getHeight(xCell, yCell - 1)) {
				moving = true;
				yMove = -1;
				moveTimer = 0;
			}
			else if (Gdx.input.isKeyPressed(Input.Keys.W)
					&& yCell < board.height - 1
					&& height >= board.getHeight(xCell, yCell + 1)) {
				moving = true;
				yMove = 1;
				moveTimer = 0;
			}
		}
		
		if (moveTimer == 0) {
			if (board.getStatus(xCell, yCell) == Cell.Status.ON_FIRE && grounded) {
				moveTimerLength = 6;
			} else if (grounded) {
				moveTimerLength = 12;
			}
		}

		if (moving) {
			x = (MathUtils.lerp(xCell, xCellNext, moveTimer / moveTimerLength) + 0.5f)
					* cellWidth;
			y = (MathUtils.lerp(yCell, yCellNext, moveTimer / moveTimerLength) + 0.5f)
					* cellHeight;
			moveTimer++;
			if (moveTimer > moveTimerLength) {
				moving = false;
				moveTimer = 0;
				xCell += xMove;
				yCell += yMove;
				xMove = 0;
				yMove = 0;
				x = (xCell + 0.5f) * cellWidth;
				y = (yCell + 0.5f) * cellHeight;
				if (height > board.getHeight(xCell, yCell)) {
					height--;
				}
			}
		}

		xCellNext = xCell + xMove;
		yCellNext = yCell + yMove;
	}

	public void draw(SpriteBatch batch) {
		pspr.draw(batch, x - 8, y - 4 + height);
	}
}
