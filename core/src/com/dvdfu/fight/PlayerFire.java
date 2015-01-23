package com.dvdfu.fight;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dvdfu.fight.components.GamepadComponent;
import com.dvdfu.fight.components.SpriteComponent;

public class PlayerFire extends Player {
	int numFires;
	SpriteComponent healthSprite;
	SpriteComponent manaSprite;
	
	public PlayerFire(Board board) {
		super(board);
		sprite = new SpriteComponent(Const.atlas.findRegion("player"));
		sprite.setOrigin(8, 0);
		healthSprite = new SpriteComponent(Const.atlas.findRegion("health"));
		manaSprite = new SpriteComponent(Const.atlas.findRegion("mana"));
		moveTimeMax = 12;
		
		healthMax = 12;
		healthTicks = healthMax;
		manaMax = 12;
		manaRegen = 0.15f;
		manaFill = 0;
		manaTicks = manaMax;
		
		attacks[0] = new Attack(board, this, gp) {
			LinkedList<Cell> cellList = new LinkedList<Cell>();
			int attackRange;
			
			public void init() {
				manaCost = 4;
				damage = 1;
				button = GamepadComponent.Button.A;
			}
			
			public void startAttack() {
				super.startAttack();
				attackRange = 1;
				stage = 0;
				timer = 0;
			}
			
			public void duringAttack() {
				if (stage == 0 || timer < 60 || (timer / 5) % 2 == 0) {
					for (int i = 0; i < board.width; i++) {
						for (int j = 0; j < board.height; j++) {
							if (Math.abs(xCell - i) + Math.abs(yCell - j) == attackRange) {
								board.grid[i][j].setTargeted(id);
							}
						}
					}
				}
				if (stage == 0) {
					if (gp.keyDown(button)) {
						if (timer < 20) {
							timer++;
						} else {
							timer = 0;
							attackRange++;
						}
						if (attackRange == 3) {
							stage = 1;
						}
					} else {
						finishAttack();
					}
				}
				if (stage == 1) {
					if (gp.keyDown(button)) {
						if (timer < 120) {
							timer++;
						} else {
							finishAttack();
						}
					} else {
						finishAttack();
					}
				}
			}
			
			public void finishAttack() {
				super.finishAttack();
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
					f.playerID = id;
					board.units.add(f);
				}
			}
		};
		
		attacks[1] = new Attack(board, this, gp) {
			public void init() {
				manaCost = 1;
				damage = 1;
				button = GamepadComponent.Button.B;
				pressToUse = false;
				multipleCasts = false;
			}

			public void startAttack() {
				super.startAttack();
				moveTimeMax = 6;
				timer = 0;
			}
			
			public void duringAttack() {
				Cell cell = board.getCell(xCell, yCell);
				if (player.height == cell.height) {
					cell.setStatus(Cell.Status.ON_FIRE, id);
				}
				if (!gp.keyDown(button)) {
					finishAttack();
				}
				if (timer > 5) {
					if (!player.useMana(this)) {
						finishAttack();
					} else {
						timer = 0;
					}
				} else if (moving && grounded) {
					timer++;
				}
			}
			
			public void finishAttack() {
				super.finishAttack();
				moveTimeMax = 12;
			}
		};
		
		attacks[2] = new Attack(board, this, gp) {
			int attackDist;
			int rate = 3;
			Cell oCell;
			
			public void init() {
				manaCost = 4;
				damage = 1;
				button = GamepadComponent.Button.X;
				multipleCasts = false;
				useInAir = false;
			}
			
			public void startAttack() {
				super.startAttack();
				player.cancelMoving();
				attackDist = 1;
				stage = 0;
				timer = 0;
				oCell = board.getCell(player.xCell, player.yCell);
			}
			
			public void duringAttack() {
				canMove = false;
				int[] ax = new int[10], ay = new int[10];
				
				for (int i = 0; i < attackDist; i++) {
					ax[i] = 0;
					if (moveDirection == Direction.LEFT) {
						ax[i] = -(i + 1);
					} else if (moveDirection == Direction.RIGHT) {
						ax[i] = i + 1;
					}
					ay[i] = 0;
					if (moveDirection == Direction.DOWN) {
						ay[i] = -(i + 1);
					} else if (moveDirection == Direction.UP) {
						ay[i] = i + 1;
					}
				}
				
				if (stage == 0) {
					if (attackDist >= 8 || !gp.keyDown(button)) {
						stage = 1;
						timer = 0;
					} else {
						for (int i = 0; i < attackDist; i++) {
							board.getCell(xCell + ax[i], yCell + ay[i]).setTargeted(id);
						}
						if (timer < 6) {
							timer++;
						} else {
							timer = 0;
							attackDist++;
						}
					}
				}
				if (stage == 1) {
					if (timer % rate == 0) {
						Cell cell = board.getCellUnsafe(oCell.xCell + ax[timer / rate], oCell.yCell + ay[timer / rate]);
						if (cell == null) {
							finishAttack();
							return;
						}
						cell.setStatus(Cell.Status.BIG_FIRE, id);
					}
					timer++;
					for (int i = timer / rate; i < attackDist; i++) {
						board.getCell(xCell + ax[i], yCell + ay[i]).setTargeted(id);
					}
					if (timer > rate * (attackDist - 1)) {
						finishAttack();
					}
				}
			}
		};
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
		}
		
		super.update();
	}
	
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		int xOffset = id == 1? 0: 144;
		healthSprite.setSize(8, 16);
		for (int i = 0; i < healthTicks; i++) {
			healthSprite.draw(batch, i * 8 + xOffset, -30);
		}
		
		manaSprite.setSize(8, 16);
		for (int i = 0; i < manaTicks; i++) {
			manaSprite.draw(batch, i * 8 + xOffset, -48);
		}
		manaSprite.setSize(MathUtils.lerp(0, 8, manaFill), 16);
		manaSprite.draw(batch, manaTicks * 8 + xOffset, -48);
	}
}
