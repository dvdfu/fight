package com.dvdfu.fight;

import java.util.LinkedList;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dvdfu.fight.components.GamepadComponent;
import com.dvdfu.fight.components.SpriteComponent;

public abstract class Player extends BoardUnit {
	int moveTime;
	float moveTimeMax;
	float ySpeed;
	float steepMax; // max valid steepness for moving into adjacent cell
	int xCellNext, yCellNext;
	int xMove, yMove;
	SpriteComponent sprite;
	boolean moving;
	boolean grounded;
	
	float manaFill;
	float manaRegen;
	int manaTicks;
	int manaMax;
	
	Attack a1, a2, a3, a4;
	
	GamepadComponent gp;
	enum Direction { UP, DOWN, LEFT, RIGHT };
	Direction moveDirection;
	LinkedList<Direction> moveQueue;
	int[] directionKey = { Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D };

	public Player(Board board) {
		super(board);
		moveQueue = new LinkedList<Direction>();
		moveDirection = Direction.RIGHT;
		x = (xCell + 0.5f) * board.cellWidth;
		y = (yCell + 0.5f) * board.cellHeight;
		steepMax = 6;
		gp = new GamepadComponent();
		zPriority = 1;
	}

	private void handleJump() {
		float groundHeight = Math.max(board.getHeight(xCell, yCell), board.getHeight(xCellNext, yCellNext));
		if (height + ySpeed < groundHeight) {
			ySpeed = 0;
			height = groundHeight;
		} else if (height == groundHeight) {
			if (gp.keyDown(GamepadComponent.Button.R)) {
				ySpeed = 5;
				height += ySpeed;
			}
		} else {
			height += ySpeed;
			ySpeed -= 0.3f;
			if (height + ySpeed < groundHeight) {
				ySpeed = 0;
				height = groundHeight;
			}
		}
		grounded = height == board.getHeight(xCell, yCell);
	}

	private void testMove(Direction direction) {
		boolean success = false;
		float steep = grounded? steepMax: 0;
		switch (direction) {
		case UP:
			success = yCell < board.height - 1 && height >= board.getHeight(xCell, yCell + 1) - steep;
			if (success) yMove = 1;
			break;
		case DOWN:
			success = yCell >= 1 && height >= board.getHeight(xCell, yCell - 1) - steep;
			if (success) yMove = -1;
			break;
		case LEFT:
			success = xCell >= 1 && height >= board.getHeight(xCell - 1, yCell) - steep;
			if (success) xMove = -1;
			break;
		case RIGHT:
			success = xCell < board.width - 1 && height >= board.getHeight(xCell + 1, yCell) - steep;
			if (success) xMove = 1;
			break;
		}
		if (success) {
			moving = true;
			moveTime = 1;
			moveDirection = direction;
			startMoving();
		}
	}
	
	protected void startMoving() {}
	
	protected void finishMoving() {}

	private void stopMoving() {
		moving = false;
		moveTime = 0;
		xMove = 0;
		yMove = 0;
		x = (xCell + 0.5f) * board.cellWidth;
		y = (yCell + 0.5f) * board.cellHeight;
	}

	public void update() {
		handleJump();
		
		if (manaTicks < manaMax) {
			if (manaFill >= 1) {
				manaFill = 0;
				manaTicks++;
			} else {
				manaFill += manaRegen;
			}
		}
		

		if (!moving) {
			if (moveQueue.size() < 2) {
				if (gp.keyDown(GamepadComponent.Button.RIGHT)) {
					moveQueue.add(Direction.RIGHT);
				} else if (gp.keyDown(GamepadComponent.Button.LEFT)) {
					moveQueue.add(Direction.LEFT);
				}
				if (gp.keyDown(GamepadComponent.Button.UP)) {
					moveQueue.add(Direction.UP);
				} else if (gp.keyDown(GamepadComponent.Button.DOWN)) {
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
			x = (MathUtils.lerp(xCell, xCellNext, moveTime / moveTimeMax) + 0.5f) * board.cellWidth;
			y = (MathUtils.lerp(yCell, yCellNext, moveTime / moveTimeMax) + 0.5f) * board.cellHeight;
			if (moveTime * 2 < moveTimeMax) {
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
			moveTime++;
			if (moveTime >= moveTimeMax) {
				xCell += xMove;
				yCell += yMove;
				finishMoving();
				stopMoving();
				if (height > board.getHeight(xCell, yCell)) {
					height--;
				}
			}
		}
	}

	public void draw(SpriteBatch batch) {
		sprite.drawOrigin(batch, x, y - 4 + height);
	}
	
	public int getZIndex() {
		return Math.min(yCell, yCellNext);
	}
}