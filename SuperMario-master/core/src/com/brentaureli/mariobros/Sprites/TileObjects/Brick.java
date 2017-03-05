package com.brentaureli.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.brentaureli.mariobros.Juego;
import com.brentaureli.mariobros.Scenes.Hud;
import com.brentaureli.mariobros.Screens.PlayScreen;
import com.brentaureli.mariobros.Sprites.Player;

/**
 * Created by brentaureli on 8/28/15.
 */
public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Juego.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Player player) {
        if(player.isBig()) {
            setCategoryFilter(Juego.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            Juego.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        Juego.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }

}
