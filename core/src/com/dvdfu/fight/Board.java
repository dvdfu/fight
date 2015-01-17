package com.dvdfu.fight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dvdfu.fight.components.SpriteComponent;

public class Board {
	int width, height;
	Cell[][] grid;
	Player p1;
	SpriteComponent tile;
	SpriteComponent firetile;
	SpriteComponent pointer;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new Cell[width][height];
		tile = new SpriteComponent(Const.atlas.findRegion("tile"));
		firetile = new SpriteComponent(Const.atlas.findRegion("firetile"), 24);
		pointer = new SpriteComponent(Const.atlas.findRegion("pointer"));
		p1 = new Player(this);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[i][j] = new Cell(i, j);
//				grid[i][j].height = 20 * (5 - Math.max(Math.abs(i - 4), Math.abs(j - 4)));
//				grid[i][j].height = 20 * MathUtils.random((j + i) / 3);
//				grid[i][j].height = 6 * ((j ^ i));
//				grid[i][j].height = 0;
			}
		}
	}

	public void draw(SpriteBatch batch) {
		Cell cell;
		p1.update();
		update();

		for (int j = height - 1; j >= 0; j--) {
			for (int i = 0; i < width; i++) {
				cell = grid[i][j];
				tile.setSize(24, 16);
				tile.setColor(1, 1, 1);
				tile.draw(batch, cell.x * 24, cell.y * 16 + cell.height);
				if (cell.status == Cell.Status.ON_FIRE) {
					firetile.draw(batch, cell.x * 24, cell.y * 16 + cell.height);
				}
				tile.setSize(24, cell.height);
				tile.setColor(0.5f, 0.3f, 0.2f);
				tile.draw(batch, cell.x * 24, cell.y * 16);
			}
			if (p1.yCell == j) {
				tile.setAlpha(0.3f);
				tile.setSize(24, 16);
				tile.setColor(0, 0, 0);
				tile.draw(batch, p1.xCell * 24, p1.yCell * 16 + getHeight(p1.xCell, p1.yCell));
				tile.setAlpha(1);
			}
			if (p1.yCellNext == j || p1.yCell == j) {
				p1.draw(batch);
			}
		}

		for (int j = height - 1; j >= 0; j--) {
			for (int i = 0; i < width; i++) {
				cell = grid[i][j];
				if (p1.xCell == i && p1.yCell == j) {
//					pointer.drawCentered(batch, cell.x * 24 + 12, cell.y * 16
//							+ 16 + cell.height);
				}
			}
		}

	}
	
	private void update() {
		Cell cell;
		for (int j = height - 1; j >= 0; j--) {
			for (int i = 0; i < width; i++) {
				cell = grid[i][j];
				if (p1.key1 && i == p1.xCell && j == p1.yCell && p1.height == cell.height) {
					cell.setStatus(Cell.Status.ON_FIRE);
				}
				cell.update();
			}
		}
		
		firetile.update();
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
