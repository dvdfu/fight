package com.dvdfu.fight;

import com.dvdfu.fight.components.GamepadComponent;

public abstract class Attack {
	int mana;
	int damage;
	float cooldown;
	float windup;
	float timer;
	boolean using;
	GamepadComponent.Button button;
	boolean pressUse;
	
	Player player;
	Board board;
	GamepadComponent gp;
	
	public Attack(Board board, Player player, GamepadComponent gp) {
		this.board = board;
		this.player = player;
		this.gp = gp;
	}
	
	public abstract void use();
	
	public void mana() {
		if (!using && player.manaTicks >= mana) {
			player.manaTicks -= mana;
			using = true;
		}
	}
	
	public void press() {
		mana();
	}
	
	public boolean keyPressed() {
		return gp.keyPressed(button);
	}
	
	public boolean keyDown() {
		return gp.keyDown(button);
	}
}
