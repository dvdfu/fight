package com.dvdfu.fight;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Const {
	public static TextureAtlas atlas;

	public Const() {
		AssetManager manager = new AssetManager();
		atlas = new TextureAtlas();
		
		manager.load("img/images.atlas", TextureAtlas.class);
		manager.finishLoading();
		atlas = manager.get("img/images.atlas", TextureAtlas.class);
	}
}