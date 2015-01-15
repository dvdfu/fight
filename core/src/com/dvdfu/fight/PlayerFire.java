package com.dvdfu.fight;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dvdfu.fight.components.GamepadComponent;
import com.dvdfu.fight.components.SpriteComponent;

public class PlayerFire extends Player {
	float attackTimer;
	int attackRange;
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
		
		a1 = new Attack(); // flamewheel
		a1.mana = 1;
		a1.damage = 1;
		a1.cooldown = 0;
		a1.windup = 0;
		
		a2 = new Attack(); // flamerun
		a2.mana = 1;
		a2.damage = 1;
		a2.cooldown = 0;
		a2.windup = 0;
		
		a3 = new Attack(); // firebullet
		a3.mana = 1;
		a3.damage = 1;
		a3.cooldown = 0;
		a3.windup = 0;
	}
	
	protected void startMoving() {
		if (a2.using && grounded) {
			if (manaTicks >= a2.mana) {
				manaTicks -= a2.mana;
			} else {
				a2.using = false;
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
		
		if (gp.keyPressed(GamepadComponent.Button.A)) {
			a2.using ^= true;
		}
		
		if (gp.keyPressed(GamepadComponent.Button.B)) {
			if (!a1.using && manaTicks >= a1.mana) {
				manaTicks -= a1.mana;
				a1.using = true;
				attackTimer = 1;
			}
		}
		
		if (gp.keyPressed(GamepadComponent.Button.X)) {
			if (!a3.using && manaTicks >= a3.mana) {
				manaTicks -= a3.mana;
				FireBullet fb = board.poolFirebullet.obtain();
				fb.set(moveDirection, xCell, yCell, height + 8);
				board.units.add(fb);
			}
		}
		
		if (a1.using) {
			if (gp.keyDown(GamepadComponent.Button.B)) {
				attackRange = (int) attackTimer;
				if (attackTimer < 3) {
					attackTimer += 0.05f;
				} else {
					attackTimer = 3;
					useAttack1();
				}
			} else {
				useAttack1();
			}
		}
		
		super.update();
	}
	
	private void useAttack1() {
		if (attackRange > 0) {
			LinkedList<Cell> cellList = new LinkedList<Cell>();
			for (int i = 0; i < board.width; i++) {
				for (int j = 0; j < board.height; j++) {
					if (Math.abs(xCell - i) + Math.abs(yCell - j) == attackRange) {
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
			a1.using = false;
			attackTimer = 0;
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
