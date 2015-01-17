package com.dvdfu.fight;

import java.util.LinkedList;

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

	enum Direction {
		UP, DOWN, LEFT, RIGHT
	};

	LinkedList<Direction> moveQueue;

	public Player(Board board) {
		this.board = board;
		moveQueue = new LinkedList<Direction>();
		pspr = new SpriteComponent(Const.atlas.findRegion("player"));
		x = (xCell + 0.5f) * cellWidth;
		y = (yCell + 0.5f) * cellHeight;
	}

	private void handleJump() {
		boardHeight = Math.max(board.getHeight(xCell, yCell),
				board.getHeight(xCellNext, yCellNext));
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
		grounded = height == board.getHeight(xCell, yCell);
	}

	private void testMove(Direction direction, int distance) {
		switch (direction) {
		case UP:
			if (yCell < board.height - distance && height >= board.getHeight(xCell, yCell + distance)) {
				moving = true;
				moveTimer = 0;
				yMove = distance;
				System.out.println("UP");
			}
			break;
		case DOWN:
			if (yCell >= distance && height >= board.getHeight(xCell, yCell - distance)) {
				moving = true;
				moveTimer = 0;
				yMove = -distance;
				System.out.println("DOWN");
			}
			break;
		case LEFT:
			if (xCell >= distance && height >= board.getHeight(xCell - distance, yCell)) {
				moving = true;
				moveTimer = 0;
				xMove = -distance;
				System.out.println("LEFT");
			}
			break;
		case RIGHT:
			if (xCell < board.width - distance && height >= board.getHeight(xCell + distance, yCell)) {
				moving = true;
				moveTimer = 0;
				xMove = distance;
				System.out.println("RIGHT");
			}
			break;
		}
	}

	public void update() {
		key1 = Gdx.input.isKeyPressed(Input.Keys.F);
		handleJump();
		
		if (moveQueue.size() < 2) {
			if (!moving && Gdx.input.isKeyPressed(Input.Keys.W)) {
				if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
					moveQueue.clear();
				}
				moveQueue.add(Direction.UP);
			}
			if (!moving && Gdx.input.isKeyPressed(Input.Keys.S)) {
				if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
					moveQueue.clear();
				}
				moveQueue.add(Direction.DOWN);
			}
			if (!moving && Gdx.input.isKeyPressed(Input.Keys.A)) {
				if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
					moveQueue.clear();
				}
				moveQueue.add(Direction.LEFT);
			}
			if (!moving && Gdx.input.isKeyPressed(Input.Keys.D)) {
				if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
					moveQueue.clear();
				}
				moveQueue.add(Direction.RIGHT);
			}
		}
		
		if (!moving) {
			if (!moveQueue.isEmpty()) {
				testMove(moveQueue.remove(), 1);
			}
		}
		
		if (moveTimer == 0) {
			if (board.getStatus(xCell, yCell) == Cell.Status.ON_FIRE
					&& grounded) {
				moveTimerLength = 6;
			} else if (grounded) {
				moveTimerLength = 12;
			}
		}

		xCellNext = xCell + xMove;
		yCellNext = yCell + yMove;

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
	}

	public void draw(SpriteBatch batch) {
		pspr.draw(batch, x - 8, y - 4 + height);
	}
}
