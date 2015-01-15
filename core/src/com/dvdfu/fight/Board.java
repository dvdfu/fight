package com.dvdfu.fight;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.dvdfu.fight.components.SpriteComponent;

public class Board {
	final Board self = this;
	int width, height;
	final int cellWidth = 24, cellHeight = 16;
	Cell[][] grid;
	PlayerFire p1;
	SpriteComponent tile;
	SpriteComponent firetile;
	SpriteComponent pointer;
	LinkedList<BoardUnit> units;
	
	Pool<Fireball> poolFireball;
	Pool<FireBullet> poolFirebullet;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new Cell[width][height];
		tile = new SpriteComponent(Const.atlas.findRegion("tile"));
		firetile = new SpriteComponent(Const.atlas.findRegion("firetile"), cellWidth);
		firetile.setSize(24, 24);
		pointer = new SpriteComponent(Const.atlas.findRegion("pointer"));
		p1 = new PlayerFire(self);
		units = new LinkedList<BoardUnit>();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Cell cell = new Cell(self, i, j);
				cell.height = Math.min(Math.min(i, width - 1 - i), Math.min(j, height - 1 - j)) * 12;
				grid[i][j] = cell;
				units.add(cell);
			}
		}
		
		units.add(p1);
		
		poolFireball = new Pool<Fireball>() {
			protected Fireball newObject() {
				return new Fireball(self);
			}
		};
		
		poolFirebullet = new Pool<FireBullet>() {
			protected FireBullet newObject() {
				return new FireBullet(self);
			}
		};
	}

	public void draw(SpriteBatch batch) {
		update();
		
		for (int i = 0 ; i < units.size(); i++) {
			BoardUnit b = units.get(i);
			if (b instanceof Cell) {
				Cell cell = (Cell) b;
				tile.setSize(cellWidth, cellHeight);
				if (cell.targeted) {
					tile.setColor(1, 1, 0);
				} else {
					float ss = 1 - cell.height / 100f;
					tile.setColor(ss, ss, ss);
				}
				tile.draw(batch, cell.xCell * cellWidth, cell.yCell * cellHeight + cell.height);
				if (p1.xCell == cell.xCell && p1.yCell == cell.yCell) {
					tile.setAlpha(0.3f);
					tile.setSize(cellWidth, cellHeight);
					tile.setColor(0, 0, 0);
					tile.draw(batch, p1.xCell * cellWidth, p1.yCell * cellHeight + getHeight(p1.xCell, p1.yCell));
					tile.setAlpha(1);
				}
				if (cell.status == Cell.Status.ON_FIRE) {
					firetile.setFrame(cell.statusTimer / 10);
					firetile.draw(batch, cell.xCell * cellWidth, cell.yCell * cellHeight + cell.height);
				}
				if (cell.status == Cell.Status.BIG_FIRE) {
					firetile.setSize(24, 64);
					firetile.setFrame(cell.statusTimer / 10);
					firetile.draw(batch, cell.xCell * cellWidth, cell.yCell * cellHeight + cell.height);
					firetile.setSize(24, 24);
				}
				tile.setSize(cellWidth, cell.height);
				tile.setColor(0.5f, 0.3f, 0.2f);
				tile.draw(batch, cell.xCell * cellWidth, cell.yCell * cellHeight);
			}
			b.draw(batch);
		}
	}
	
	private void update() {
		for (int j = height - 1; j >= 0; j--) {
			for (int i = 0; i < width; i++) {
				Cell cell = grid[i][j];
				if (p1.attack[1].using && i == p1.xCell && j == p1.yCell && p1.height == cell.height) {
					cell.setStatus(Cell.Status.ON_FIRE);
				}
				if (p1.attack[0].using && Math.abs(p1.xCell - i) + Math.abs(p1.yCell - j) == (int) p1.attack[0].timer) {
					cell.targeted = true;
				}
			}
		}
		
		for (int i = 0 ; i < units.size(); i++) {
			BoardUnit b = units.get(i);
			b.update();
			if (b instanceof Fireball) {
				Fireball f = (Fireball) b;
				if (f.dead) {
					f.cell.setStatus(Cell.Status.ON_FIRE);
					poolFireball.free(f);
					units.remove(i);
					i--;
				}
			}
			if (b instanceof FireBullet) {
				FireBullet f = (FireBullet) b;
				if (f.dead) {
					poolFirebullet.free(f);
					units.remove(i);
					i--;
				}
			}
		}
		
		Collections.sort(units, new Comparator<BoardUnit>() {
			public int compare(BoardUnit a, BoardUnit b) {
				if (b.getZIndex() == a.getZIndex()) 
					return (int) (b.zPriority - a.zPriority);
				return b.getZIndex() - a.getZIndex();
			}
		});
	}
	
	public Cell getCell(int x, int y) {
		x = MathUtils.clamp(x, 0, width - 1);
		y = MathUtils.clamp(y, 0, height - 1);
		return grid[x][y];
	}

	public float getHeight(int x, int y) {
		return getCell(x, y).height;
	}
	
	public Cell.Status getStatus(int x, int y) {
		return getCell(x, y).status;
	}
}
