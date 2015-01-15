package com.dvdfu.fight;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dvdfu.fight.components.GamepadComponent;
import com.dvdfu.fight.components.SpriteComponent;

public class PlayerFire extends Player {
	int numFires;
	LinkedList<Fireball> fireballs;
	SpriteComponent manaSprite;
	
	public PlayerFire(Board board) {
		super(board);
		sprite = new SpriteComponent(Const.atlas.findRegion("player"));
		sprite.setOrigin(8, 0);
		manaSprite = new SpriteComponent(Const.atlas.findRegion("mana"));
		fireballs = new LinkedList<Fireball>();
		moveTimeMax = 12;
		
		manaMax = 12;
		manaRegen = 0.015f;
		manaFill = 0;
		manaTicks = manaMax;
		
		attack[0] = new Attack(board, this, gp) {
			public void use() {
				if (gp.keyDown(button)) {
					if (timer < 3) {
						timer += 0.05f;
					} else {
						timer = 3;
						useAttack0();
					}
				} else {
					useAttack0();
				}
			}

			public void mana() {
				super.mana();
				timer = 1;
			}
		};
		attack[0].mana = 1;
		attack[0].damage = 1;
		attack[0].cooldown = 0;
		attack[0].windup = 0;
		attack[0].button = GamepadComponent.Button.A;
		attack[0].pressUse = false;
		
		attack[1] = new Attack(board, this, gp) {
			public void use() {}

			public void press() {
				using ^= true;
				super.press();
			}
		};
		attack[1].mana = 1;
		attack[1].damage = 1;
		attack[1].cooldown = 0;
		attack[1].windup = 0;
		attack[1].button = GamepadComponent.Button.B;
		attack[1].pressUse = true;
		
		attack[2] = new Attack(board, this, gp) {
			public void use() {
				if (attack[2].timer >= attack[2].windup) {
					attack[2].timer = 0;
					attack[2].using = false;
					int[] ax = new int[3], ay = new int[3];
					switch (moveDirection) {
					case DOWN:
						ax = new int[] { 0, 0, 0 };
						ay = new int[] { -1, -2, -3 };
						break;
					case LEFT:
						ax = new int[] { -1, -2, -3 };
						ay = new int[] { 0, 0, 0 };
						break;
					case RIGHT:
						ax = new int[] { 1, 2, 3 };
						ay = new int[] { 0, 0, 0 };
						ax = new int[] { 1, 2, 3 };
						break;
					case UP:
						ax = new int[] { 0, 0, 0 };
						ay = new int[] { 1, 2, 3 };
						break;
					default:
						break;
					}
					for (int i = 0; i < 3; i++) {
						board.getCell(xCell + ax[i], yCell + ay[i]).setStatus(Cell.Status.BIG_FIRE);
					}
				} else {
					attack[2].timer++;
					canMove = false;
				}
			}
		};
		attack[2].mana = 1;
		attack[2].damage = 1;
		attack[2].cooldown = 0;
		attack[2].windup = 30;
		attack[2].button = GamepadComponent.Button.X;
		attack[2].pressUse = false;
	}
	
	protected void startMoving() {
		if (attack[1].using && grounded) {
			if (manaTicks >= attack[1].mana) {
				manaTicks -= attack[1].mana;
			} else {
				attack[1].using = false;
			}
		}
	}
	
	public void update() {
		if (!moving) {
			numFires = 0;
			for (int i = 0; i < board.width; i++) {
				for (int j = 0; j < board.height; j++) {
					if (board.getStatus(i, j) == Cell.Status.ON_FIRE) {
						numFires++;
					}
				}
			}
			if (grounded) {
				if (board.getStatus(xCell, yCell) == Cell.Status.ON_FIRE) {
					moveTimeMax = 6;
				} else {
					moveTimeMax = 12;
				}
			}
		}
		
		for (int i = 0; i < 3; i++) {
			Attack a = attack[i];
			if (a.pressUse) {
				if (gp.keyPressed(attack[i].button)) {
					a.press();
				}
			} else if (gp.keyDown(attack[i].button)) {
				a.press();
			}
			if (a.using) {
				a.use();
			}
		}
		
		super.update();
	}
	
	private void useAttack0() {
		if ((int) attack[0].timer > 0) {
			LinkedList<Cell> cellList = new LinkedList<Cell>();
			for (int i = 0; i < board.width; i++) {
				for (int j = 0; j < board.height; j++) {
					if (Math.abs(xCell - i) + Math.abs(yCell - j) == (int) attack[0].timer) {
						cellList.add(board.getCell(i, j));
					}
				}
			}
			
			while (!cellList.isEmpty()) {
				Cell cell = cellList.remove();
				Fireball f = board.poolFireball.obtain();
				f.set(cell, xCell, yCell, height);
				fireballs.add(f);
				board.units.add(f);
			}
			attack[0].using = false;
			attack[0].timer = 0;
		}
	}
	
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		manaSprite.setSize(8, 16);
		for (int i = 0; i < manaTicks; i++) {
			manaSprite.draw(batch, i * 8 - 48, -48);
		}
		manaSprite.setSize(MathUtils.lerp(0, 8, manaFill), 16);
		manaSprite.draw(batch, manaTicks * 8 - 48, -48);
	}
}
