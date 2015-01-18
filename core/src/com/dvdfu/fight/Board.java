package com.dvdfu.fight;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dvdfu.fight.components.SpriteComponent;

public class Board {
	int width, height;
	final int cellWidth = 24, cellHeight = 16;
	Cell[][] grid;
	PlayerFire p1;
	SpriteComponent tile;
	SpriteComponent firetile;
	SpriteComponent pointer;
	LinkedList<BoardUnit> units;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new Cell[width][height];
		tile = new SpriteComponent(Const.atlas.findRegion("tile"));
		firetile = new SpriteComponent(Const.atlas.findRegion("firetile"), cellWidth);
		pointer = new SpriteComponent(Const.atlas.findRegion("pointer"));
		p1 = new PlayerFire(this);
		units = new LinkedList<BoardUnit>();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Cell cell = new Cell(this, i, j);
//				cell.height = ((i ^ j) / 4) * 6;
				grid[i][j] = cell;
//				grid[i][j].height = 20 * (5 - Math.max(Math.abs(i - 4), Math.abs(j - 4)));
//				grid[i][j].height = 20 * MathUtils.random((j + i) / 3);
//				grid[i][j].height = 6 * ((j ^ i));
				units.add(cell);
			}
		}
		
		units.add(p1);
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
					firetile.draw(batch, cell.xCell * cellWidth, cell.yCell * cellHeight + cell.height);
				}
				tile.setSize(cellWidth, cell.height);
				tile.setColor(0.5f, 0.3f, 0.2f);
				tile.draw(batch, cell.xCell * cellWidth, cell.yCell * cellHeight);
			}
			b.draw(batch);
			if (b instanceof Fireball && ((Fireball) b).dead) {
				((Fireball) b).cell.setStatus(Cell.Status.ON_FIRE);
				units.remove(i);
				i--;
			}
		}
	}
	
	private void update() {
		for (int j = height - 1; j >= 0; j--) {
			for (int i = 0; i < width; i++) {
				Cell cell = grid[i][j];
				if (p1.onFire && i == p1.xCell && j == p1.yCell && p1.height == cell.height) {
					cell.setStatus(Cell.Status.ON_FIRE);
				}
				if (p1.a1.using && Math.abs(p1.xCell - i) + Math.abs(p1.yCell - j) == p1.attackRange) {
					cell.targeted = true;
				}
			}
		}
		
		for (int i = 0 ; i < units.size(); i++) {
			BoardUnit b = units.get(i);
			b.update();
			if (b instanceof Fireball && ((Fireball) b).dead) {
				((Fireball) b).cell.setStatus(Cell.Status.ON_FIRE);
				units.remove(i);
				i--;
			}
		}
		
		firetile.update();
		Collections.sort(units, new Comparator<BoardUnit>() {
			public int compare(BoardUnit a, BoardUnit b) {
				if (b.getZIndex() == a.getZIndex()) 
					return (int) (b.zPriority - a.zPriority);
				return b.getZIndex() - a.getZIndex();
			}
		});
	}
	
	public Cell getCell(int x, int y) {
		if (x < 0) x = 0;
		if (x >= width) x = width - 1;
		if (y < 0) y = 0;
		if (y >= height) y = height - 1;
		return grid[x][y];
	}

	public float getHeight(int x, int y) {
		return getCell(x, y).height;
	}
	
	public Cell.Status getStatus(int x, int y) {
		return getCell(x, y).status;
	}
}
