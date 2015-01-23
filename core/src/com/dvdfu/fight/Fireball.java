package com.dvdfu.fight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dvdfu.fight.components.SpriteComponent;

public class Fireball extends BoardUnit implements Poolable {
	final float timerMax = 45f;
	final float fallSpeed = -0.3f;
	SpriteComponent sprite;
	Cell cell;
	int timer;
	int xOr, yOr;
	float vSpeed;
	boolean dead;
	int playerID;
	
	public Fireball(Board board) {
		super(board);
		sprite = new SpriteComponent(Const.atlas.findRegion("fireball"));
		zPriority = 10;
		reset();
	}

	public void update() {
		x = MathUtils.lerp(xOr, cell.xCell, timer / timerMax) * board.cellWidth;
		y = MathUtils.lerp(yOr, cell.yCell, timer / timerMax) * board.cellHeight;
		xCell = (int) (x / board.cellWidth + 0.5f);
		yCell = (int) (y / board.cellHeight + 0.5f);
		timer++;
		if (timer > timerMax) {
			dead = true;
		}
		vSpeed += fallSpeed;
		height += vSpeed;
		if (height + vSpeed < cell.height) {
			height = cell.height;
		}
		cell.setTargeted(playerID);
	}
	
	public void draw(SpriteBatch batch) {
		sprite.setColor(1, 1, 1);
		sprite.setAlpha(1);
		sprite.setSize(8, 8);
		sprite.drawCentered(batch, x + 12, y + height + 8);
		sprite.setColor(0, 0, 0);
		sprite.setAlpha(0.3f);
		sprite.setSize(8, 5);
		sprite.drawCentered(batch, x + 12, y + board.getHeight(xCell, yCell) + 8);
	}
	
	public void set(Cell cell, int xOr, int yOr, float height) {
		this.cell = cell;
		this.xOr = xOr;
		this.yOr = yOr;
		x = xOr * board.cellWidth;
		y = yOr * board.cellHeight;
		this.height = height;
		vSpeed = (cell.height - height) / timerMax - fallSpeed * timerMax / 2;
	}

	public void reset() {
		dead = false;
		timer = 0;
	}
}
