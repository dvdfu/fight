package com.dvdfu.fight;

public class Projectile {
	enum Types {
		FIRE
	};
	Types type;
	float x, y;
	float xSpeed, ySpeed;
	int xCell, yCell;
	int xCellNext, yCellNext;
	
	public Projectile() {
		
	}
}
