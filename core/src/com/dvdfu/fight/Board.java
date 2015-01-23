package com.dvdfu.fight;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

public class Board {
	final Board self = this;
	int width, height;
	final int cellWidth = 24, cellHeight = 16;
	Cell[][] grid;
	PlayerFire p1;
	PlayerFire p2;
	LinkedList<BoardUnit> units;
	
	Pool<Fireball> poolFireball;
	Pool<FireBullet> poolFirebullet;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new Cell[width][height];
		units = new LinkedList<BoardUnit>();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Cell cell = new Cell(self, i, j);
				cell.height = Math.min(Math.min(i, width - 1 - i), Math.min(j, height - 1 - j)) * 12;
				grid[i][j] = cell;
				units.add(cell);
			}
		}
		
		p1 = new PlayerFire(self);
		p2 = new PlayerFire(self);
		p1.id = 1;
		p2.id = 2;
		p2.xCell = 9;
		p2.yCell = 9;
		p2.cancelMoving();
		units.add(p1);
		units.add(p2);
		
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
			b.draw(batch);
		}
	}
	
	private void update() {
		for (int i = 0 ; i < units.size(); i++) {
			BoardUnit b = units.get(i);
			b.update();
			if (b instanceof Fireball) {
				Fireball f = (Fireball) b;
				if (f.dead) {
					f.cell.setStatus(Cell.Status.ON_FIRE, 1);
					f.cell.playerID = f.playerID;
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
					return (int) (a.zPriority - b.zPriority);
				return b.getZIndex() - a.getZIndex();
			}
		});
	}
	
	public Cell getCell(int x, int y) {
		x = MathUtils.clamp(x, 0, width - 1);
		y = MathUtils.clamp(y, 0, height - 1);
		return grid[x][y];
	}
	
	public Cell getCellUnsafe(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) return null;
		return grid[x][y];
	}

	public float getHeight(int x, int y) {
		return getCell(x, y).height;
	}
	
	public Cell.Status getStatus(int x, int y) {
		return getCell(x, y).status;
	}
}
