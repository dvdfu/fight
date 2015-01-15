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
		
		attacks[0] = new Attack(board, this, gp) {
			public void init() {
				manaCost = 1;
				damage = 1;
				button = GamepadComponent.Button.A;
			}
			
			public void using() {
				for (int i = 0; i < board.width; i++) {
					for (int j = 0; j < board.height; j++) {
						if (Math.abs(xCell - i) + Math.abs(yCell - j) == timer / 20) {
							board.grid[i][j].targeted = true;
						}
					}
				}
				if (gp.keyDown(button)) {
					if (timer < 60) {
						timer += 1;
					} else {
						timer = 60;
						attack();
					}
				} else {
					attack();
				}
			}
			
			public void use() {
				super.use();
			}
			
			public void attack() {
				if ((int) timer > 0) {
					LinkedList<Cell> cellList = new LinkedList<Cell>();
					for (int i = 0; i < board.width; i++) {
						for (int j = 0; j < board.height; j++) {
							if (Math.abs(xCell - i) + Math.abs(yCell - j) == timer / 20) {
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
					using = false;
					timer = 0;
				}
			}
		};
		
		attacks[1] = new Attack(board, this, gp) {
			public void init() {
				manaCost = 1;
				damage = 1;
				button = GamepadComponent.Button.B;
			}
			
			public void using() {
				Cell cell = board.getCell(xCell, yCell);
				if (player.height == cell.height) {
					cell.setStatus(Cell.Status.ON_FIRE);
				}
			}

			public void use() {
				using ^= true;
			}
		};
		
		attacks[2] = new Attack(board, this, gp) {
			public void init() {
				manaCost = 1;
				damage = 1;
				windup = 30;
				button = GamepadComponent.Button.X;
				multipleCasts = false;
				useInAir = false;
			}
			
			public void using() {
				canMove = false;
				int dist = 6;
				int[] ax = new int[dist], ay = new int[dist];
				for (int i = 0; i < dist; i++) {
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
					if (timer >= windup) {
						stage = 1;
						timer = 0;
					} else {
						for (int i = 0; i < dist; i++) {
							board.getCell(xCell + ax[i], yCell + ay[i]).targeted = true;
						}
						timer++;
					}
				}
				
				if (stage == 1) {
					int rate = 6;
					if (timer % rate == 0) {
						Cell cell = board.getCellUnsafe(xCell + ax[timer / rate], yCell + ay[timer / rate]);
						if (cell == null) {
							using = false;
							stage = 0;
							timer = 0;
							return;
						}
						cell.setStatus(Cell.Status.BIG_FIRE);
					}
					timer++;
					for (int i = timer / rate; i < dist; i++) {
						board.getCell(xCell + ax[i], yCell + ay[i]).targeted = true;
					}
					if (timer > rate * (dist - 1)) {
						using = false;
						stage = 0;
						timer = 0;
					}
				}
			}
			
			public void use() {
				super.use();
				player.cancelMoving();
				timer = 0;
			}
		};
	}
	
	protected void startMoving() {
		if (attacks[1].using && grounded) {
			attacks[1].using = useMana(attacks[1]);
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
		
		super.update();
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
