package com.brentaureli.mariobros.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.brentaureli.mariobros.Juego;
import com.brentaureli.mariobros.Screens.PlayScreen;
import com.brentaureli.mariobros.Sprites.Enemies.Enemy;
import com.brentaureli.mariobros.Sprites.Enemies.Turtle;
import com.brentaureli.mariobros.Sprites.TileObjects.Brick;
import com.brentaureli.mariobros.Sprites.TileObjects.Coin;
import com.brentaureli.mariobros.Sprites.Enemies.Goomba;

/**
 * Created by brentaureli on 8/28/15.
 */
public class B2WorldCreator {
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;


    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        //create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for(MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Juego.PPM, (rect.getY() + rect.getHeight() / 2) / Juego.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Juego.PPM, rect.getHeight() / 2 / Juego.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }



        //create brick bodies/fixtures
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            new Brick(screen, object);
        }

        //create all goombas
        goombas = new Array<Goomba>();
        for(int i = 0; i <= 150; i++){
            goombas.add(new Goomba(screen, 800 / Juego.PPM, 800 / Juego.PPM));


        }
//        turtles = new Array<Turtle>();
//        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
//            Rectangle rect = ((RectangleMapObject) object).getRectangle();
//            turtles.add(new Turtle(screen, rect.getX() / Juego.PPM, rect.getY() / Juego.PPM));
//        }




    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }
    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
   enemies.addAll(goombas);
       //enemies.addAll(turtles);
        return enemies;
    }
}
