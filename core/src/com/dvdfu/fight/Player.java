package com.dvdfu.fight;

import java.util.LinkedList;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dvdfu.fight.components.GamepadComponent;
import com.dvdfu.fight.components.SpriteComponent;

public class Player {
	Board board;
	float x, y;
	float height;
	float moveTimerLength;
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
	GamepadComponent gp;

	enum Direction { UP, DOWN, LEFT, RIGHT };
	Direction moveDirection;
	LinkedList<Direction> moveQueue;
	int[] directionKey = { Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D };

	public Player(Board board) {
		this.board = board;
		moveQueue = new LinkedList<Direction>();
		moveDirection = Direction.RIGHT;
		moveTimerLength = 12;
		pspr = new SpriteComponent(Const.atlas.findRegion("player"));
		pspr.setOrigin(8, 0);
		x = (xCell + 0.5f) * board.cellWidth;
		y = (yCell + 0.5f) * board.cellHeight;
		
		gp = new GamepadComponent();
	}

	protected void handleJump() {
		boardHeight = Math.max(board.getHeight(xCell, yCell),
				board.getHeight(xCellNext, yCellNext));
		if (height + vSpeed < boardHeight) {
			vSpeed = 0;
			height = boardHeight;
		} else if (height == boardHeight) {
			if (gp.keyDown(GamepadComponent.Button.R)) {
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

	protected void testMove(Direction direction) {
		boolean success = false;
		switch (direction) {
		case UP:
			success = yCell < board.height - 1 && height >= board.getHeight(xCell, yCell + 1) - 6;
			if (success) yMove = 1;
			break;
		case DOWN:
			success = yCell >= 1 && height >= board.getHeight(xCell, yCell - 1) - 6;
			if (success) yMove = -1;
			break;
		case LEFT:
			success = xCell >= 1 && height >= board.getHeight(xCell - 1, yCell) - 6;
			if (success) xMove = -1;
			break;
		case RIGHT:
			success = xCell < board.width - 1 && height >= board.getHeight(xCell + 1, yCell) - 6;
			if (success) xMove = 1;
			break;
		}
		if (success) {
			moving = true;
			moveTimer = 1;
			moveDirection = direction;
		}
	}

	protected void stopMoving() {
		moving = false;
		moveTimer = 0;
		xMove = 0;
		yMove = 0;
		x = (xCell + 0.5f) * board.cellWidth;
		y = (yCell + 0.5f) * board.cellHeight;
	}

	public void update() {
		handleJump();

		if (!moving) {
			if (moveQueue.size() < 2) {
				if (gp.keyDown(GamepadComponent.Button.RIGHT)) {
					moveQueue.add(Direction.RIGHT);
				}
				if (gp.keyDown(GamepadComponent.Button.LEFT)) {
					moveQueue.add(Direction.LEFT);
				}
				if (gp.keyDown(GamepadComponent.Button.UP)) {
					moveQueue.add(Direction.UP);
				}
				if (gp.keyDown(GamepadComponent.Button.DOWN)) {
					moveQueue.add(Direction.DOWN);
				}
			}
			if (!moveQueue.isEmpty()) {
				testMove(moveQueue.remove());
			}
		}
		
		xCellNext = xCell + xMove;
		yCellNext = yCell + yMove;
		
		if (moving) {
			x = (MathUtils.lerp(xCell, xCellNext, moveTimer / moveTimerLength) + 0.5f) * board.cellWidth;
			y = (MathUtils.lerp(yCell, yCellNext, moveTimer / moveTimerLength) + 0.5f) * board.cellHeight;
			int dk = directionKey[moveDirection.ordinal()];
			if (moveTimer * 2 < moveTimerLength) {
				switch (moveDirection) {
				case RIGHT:
					if (!gp.keyDown(GamepadComponent.Button.RIGHT)) stopMoving();
					break;
				case LEFT:
					if (!gp.keyDown(GamepadComponent.Button.LEFT)) stopMoving();
					break;
				case UP:
					if (!gp.keyDown(GamepadComponent.Button.UP)) stopMoving();
					break;
				case DOWN:
					if (!gp.keyDown(GamepadComponent.Button.DOWN)) stopMoving();
					break;
				}
			}
			moveTimer++;
			if (moveTimer >= moveTimerLength) {
				xCell += xMove;
				yCell += yMove;
				stopMoving();
				if (height > board.getHeight(xCell, yCell)) {
					height--;
				}
			}
		}
	}

	public void draw(SpriteBatch batch) {
		pspr.drawOrigin(batch, x, y - 4 + height);
	}
}