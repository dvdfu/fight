package com.dvdfu.fight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dvdfu.fight.components.SpriteComponent;

public class FireBullet extends BoardUnit implements Poolable {
	final float timerMax = 45f;
	SpriteComponent sprite;
	Player.Direction direction;
	int timer;
	int xOr, yOr;
	boolean dead;
	
	public FireBullet(Board board) {
		super(board);
		sprite = new SpriteComponent(Const.atlas.findRegion("fireball"));
		zPriority = 1;
		reset();
	}

	public void update() {
		switch (direction) {
		case UP:
			y += 4;
			break;
		case DOWN:
			y -= 4;
			break;
		case LEFT:
			x -= 4;
			break;
		case RIGHT:
			x += 4;
			break;
		default:
			break;
		}
		xCell = (int) (x / board.cellWidth + 0.5f);
		yCell = (int) (y / board.cellHeight + 0.5f);
		timer++;
		if (timer > timerMax) {
			dead = true;
		}
//		board.getCell(xCell, yCell).setTargeted(playerID);
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
	
	public void set(Player.Direction direction, int xOr, int yOr, float height) {
		this.direction = direction;
		this.xOr = xOr;
		this.yOr = yOr;
		x = xOr * board.cellWidth;
		y = yOr * board.cellHeight;
		this.height = height;
	}

	public void reset() {
		dead = false;
		timer = 0;
	}
}
