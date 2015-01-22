package com.dvdfu.fight;

import com.dvdfu.fight.components.GamepadComponent;

public abstract class Attack {
	int manaCost;
	int damage;
	int cooldown;
	int windup;
	int timer;
	boolean using;
	GamepadComponent.Button button;
	boolean pressToUse; // skill activated by press, not hold
	boolean multipleCasts; // skill can be interrupted by second cast
	boolean useInAir;
	int stage; // for multi-stage skills
	
	Player player;
	Board board;
	GamepadComponent gp;
	
	public Attack(Board board, Player player, GamepadComponent gp) {
		this.board = board;
		this.player = player;
		this.gp = gp;
		multipleCasts = true;
		pressToUse = true;
		useInAir = true;
		init();
	}
	
	public abstract void init();
	
	public void startAttack() {
		using = true;
	}
	
	public abstract void duringAttack();
	
	public void finishAttack() {
		using = false;
	}
	
	public void pressed() {
		if (!multipleCasts && using) return;
		if (!useInAir && !player.grounded) return;
		if (player.useMana(this)) {
			startAttack();
		}
	}
}
