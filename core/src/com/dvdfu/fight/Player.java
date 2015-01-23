package com.dvdfu.fight;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dvdfu.fight.components.GamepadComponent;
import com.dvdfu.fight.components.SpriteComponent;

public abstract class Player extends BoardUnit {
	int id;
	int moveTime;
	float moveTimeMax;
	float ySpeed;
	float steepMax; // max valid steepness for moving into adjacent cell
	int xCellNext, yCellNext;
	int xMove, yMove;
	SpriteComponent sprite;
	boolean moving;
	boolean grounded;
	boolean canMove;
	
	int healthTicks;
	int healthMax;
	int healthTime; // invuln timer after hit
	int healthTimeMax = 90;
	boolean invuln;
	float manaFill; // 0.0 to 1.0, in between ticks
	float manaRegen; // mana regain per frame
	int manaTicks; // actual integer mana
	int manaMax;
	
	Attack[] attacks = new Attack[4];
	
	GamepadComponent gp;
	enum Direction { UP, DOWN, LEFT, RIGHT };
	Direction moveDirection;
	LinkedList<Direction> moveQueue;
	boolean firstMove;

	public Player(Board board) {
		super(board);
		moveQueue = new LinkedList<Direction>();
		moveDirection = Direction.RIGHT;
		x = (xCell + 0.5f) * board.cellWidth;
		y = (yCell + 0.5f) * board.cellHeight;
		steepMax = 6;
		gp = new GamepadComponent();
		zPriority = 10;
	}

	private void handleJump() {
		float groundHeight = Math.max(board.getHeight(xCell, yCell), board.getHeight(xCellNext, yCellNext));
		if (height + ySpeed < groundHeight) {
			ySpeed = 0;
			height = groundHeight;
		} else if (height <= groundHeight && canMove) {
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

	public void testMove(Direction direction) {
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
	
	protected void endMoving() {}

	protected void cancelMoving() {
		moving = false;
		moveTime = 0;
		xMove = 0;
		yMove = 0;
		x = (xCell + 0.5f) * board.cellWidth;
		y = (yCell + 0.5f) * board.cellHeight;
	}
	
	protected boolean useMana(Attack attack) {
		if (manaTicks >= attack.manaCost) {
			manaTicks -= attack.manaCost;
			return true;
		}
		return false;
	}

	public void update() {
		handleJump();
		
		for (int i = 0; i < attacks.length; i++) {
			Attack attack = attacks[i];
			if (attack == null) continue;
			if (attack.pressToUse) {
				if (gp.keyPressed(attack.button)) {
					attack.pressed();
				}
			} else if (gp.keyDown(attack.button)) {
				attack.pressed();
			}
			if (attack.using) {
				attack.duringAttack();
			}
		}
		
		if (manaTicks < manaMax) {
			if (manaFill >= 1) {
				manaFill = 0;
				manaTicks++;
			} else {
				manaFill += manaRegen;
			}
		}

		if (!moving && canMove) {
			if (moveQueue.size() < 2) {
				if (gp.keyDown(GamepadComponent.Button.RIGHT)) {
					moveDirection = Direction.RIGHT;
					moveQueue.add(Direction.RIGHT);
				} else if (gp.keyDown(GamepadComponent.Button.LEFT)) {
					moveDirection = Direction.LEFT;
					moveQueue.add(Direction.LEFT);
				}
				if (gp.keyDown(GamepadComponent.Button.UP)) {
					moveDirection = Direction.UP;
					moveQueue.add(Direction.UP);
				} else if (gp.keyDown(GamepadComponent.Button.DOWN)) {
					moveDirection = Direction.DOWN;
					moveQueue.add(Direction.DOWN);
				}
			}
			if (!moveQueue.isEmpty()) {
				testMove(moveQueue.remove());
			}
			if (gp.keyPressed(GamepadComponent.Button.RIGHT)) {
				moveDirection = Direction.RIGHT;
				testMove(Direction.RIGHT);
				firstMove = true;
			} else if (gp.keyPressed(GamepadComponent.Button.LEFT)) {
				moveDirection = Direction.LEFT;
				testMove(Direction.LEFT);
				firstMove = true;
			}
			if (gp.keyPressed(GamepadComponent.Button.UP)) {
				moveDirection = Direction.UP;
				testMove(Direction.UP);
				firstMove = true;
			} else if (gp.keyPressed(GamepadComponent.Button.DOWN)) {
				moveDirection = Direction.DOWN;
				testMove(Direction.DOWN);
				firstMove = true;
			}
		}
		
		xCellNext = xCell + xMove;
		yCellNext = yCell + yMove;
		
		if (moving) {
			x = (MathUtils.lerp(xCell, xCellNext, moveTime / moveTimeMax) + 0.5f) * board.cellWidth;
			y = (MathUtils.lerp(yCell, yCellNext, moveTime / moveTimeMax) + 0.5f) * board.cellHeight;
			if (!firstMove && moveTime * 2 < moveTimeMax) {
				switch (moveDirection) {
				case RIGHT:
					if (!gp.keyDown(GamepadComponent.Button.RIGHT)) cancelMoving();
					break;
				case LEFT:
					if (!gp.keyDown(GamepadComponent.Button.LEFT)) cancelMoving();
					break;
				case UP:
					if (!gp.keyDown(GamepadComponent.Button.UP)) cancelMoving();
					break;
				case DOWN:
					if (!gp.keyDown(GamepadComponent.Button.DOWN)) cancelMoving();
					break;
				}
			}
			moveTime++;
			if (moveTime >= moveTimeMax) {
				firstMove = false;
				xCell += xMove;
				yCell += yMove;
				endMoving();
				cancelMoving();
				if (height > board.getHeight(xCell, yCell)) {
					height--;
				}
			}
		}
		testHurt();
		
		canMove = true;
		gp.update();
	}
	
	public void testHurt() {
		if (invuln) {
			if (healthTime < healthTimeMax) {
				healthTime++;
			} else {
				invuln = false;
			}
			return;
		}
		if (!this.getCell().damages) return;
		int pid = this.getCell().playerID;
		if (pid == 0 || pid == id) return;
		healthTicks--;
		healthTime = 0;
		invuln = true;
	}

	public void draw(SpriteBatch batch) {
		if (invuln && (healthTime / 5) % 2 == 0) return;
		sprite.drawOrigin(batch, x, y - 4 + height);
	}
	
	public int getZIndex() {
		return Math.min(yCell, yCellNext);
	}
}