package com.dvdfu.fight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dvdfu.fight.components.SpriteComponent;

public class Fireball extends BoardUnit {
	SpriteComponent sprite;
	Cell cell;
	int timer;
	int xOr, yOr;
	final float timerMax = 60f;
	final float fallSpeed = -0.1f;
	float vSpeed;
	boolean dead;
	
	public Fireball(Board board, Cell cell, int xOr, int yOr, float height) {
		super(board);
		this.cell = cell;
		this.xOr = xOr;
		this.yOr = yOr;
		x = xOr * board.cellWidth;
		y = yOr * board.cellHeight;
		this.height = height;
		sprite = new SpriteComponent(Const.atlas.findRegion("fireball"));
		vSpeed = (cell.height - height) / timerMax - fallSpeed * timerMax / 2;
		zPriority = 1;
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
		cell.targeted = true;
	}
	
	public void draw(SpriteBatch batch) {
		sprite.drawCentered(batch, x + 12, y + height + 8);
	}
}
