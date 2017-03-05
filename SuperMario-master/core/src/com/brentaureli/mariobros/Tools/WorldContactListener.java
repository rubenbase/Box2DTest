package com.brentaureli.mariobros.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.brentaureli.mariobros.Juego;
import com.brentaureli.mariobros.Sprites.Enemies.Enemy;
import com.brentaureli.mariobros.Sprites.Items.Item;
import com.brentaureli.mariobros.Sprites.Player;
import com.brentaureli.mariobros.Sprites.Other.FireBall;
import com.brentaureli.mariobros.Sprites.TileObjects.InteractiveTileObject;

/**
 * Created by brentaureli on 9/4/15.
 */
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case Juego.MARIO_HEAD_BIT | Juego.BRICK_BIT:
            case Juego.MARIO_HEAD_BIT | Juego.COIN_BIT:
                if(fixA.getFilterData().categoryBits == Juego.MARIO_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Player) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Player) fixB.getUserData());
                break;
            case Juego.ENEMY_HEAD_BIT | Juego.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == Juego.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead((Player) fixB.getUserData());
                else
                    ((Enemy)fixB.getUserData()).hitOnHead((Player) fixA.getUserData());
                break;
            case Juego.ENEMY_BIT | Juego.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Juego.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Juego.MARIO_BIT | Juego.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == Juego.MARIO_BIT)
                    ((Player) fixA.getUserData()).hit((Enemy)fixB.getUserData());
                else
                    ((Player) fixB.getUserData()).hit((Enemy)fixA.getUserData());
                break;
            case Juego.ENEMY_BIT | Juego.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).hitByEnemy((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).hitByEnemy((Enemy)fixA.getUserData());
                break;
            case Juego.ITEM_BIT | Juego.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Juego.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Juego.ITEM_BIT | Juego.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == Juego.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Player) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Player) fixA.getUserData());
                break;
            case Juego.FIREBALL_BIT | Juego.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Juego.FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
