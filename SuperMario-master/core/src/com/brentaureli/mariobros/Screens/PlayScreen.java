package com.brentaureli.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.brentaureli.mariobros.Juego;
import com.brentaureli.mariobros.Scenes.Hud;
import com.brentaureli.mariobros.Sprites.Enemies.Enemy;
import com.brentaureli.mariobros.Sprites.Enemies.Goomba;
import com.brentaureli.mariobros.Sprites.Items.Item;
import com.brentaureli.mariobros.Sprites.Items.ItemDef;
import com.brentaureli.mariobros.Sprites.Items.Mushroom;
import com.brentaureli.mariobros.Sprites.Player;
import com.brentaureli.mariobros.Tools.B2WorldCreator;
import com.brentaureli.mariobros.Tools.WorldContactListener;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by brentaureli on 8/14/15.
 */
public class PlayScreen implements Screen, InputProcessor{
    public enum Keys {
        IZQUIERDA, DERECHA, ARRIBA, ABAJO
    }
    HashMap<Keys, Boolean> keys = new HashMap<PlayScreen.Keys, Boolean>();

    {
        keys.put(Keys.IZQUIERDA, false);
        keys.put(Keys.DERECHA, false);
        keys.put(Keys.ARRIBA, false);
        keys.put(Keys.ABAJO, false);
    }
    //Reference to our Game, used to set Screens
    private Juego game;
    private TextureAtlas atlas;
    public static boolean alreadyDestroyed = false;

    //basic playscreen variables
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    MapProperties prop ;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //sprites
    private Player player;
    private Goomba goomba;
    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    TiledMapTileLayer layer;


    //

    Vector3 tmpv3 = new Vector3();
    Vector2 mousePos = new Vector2();
    Vector2 force = new Vector2();
    Vector2 bodyPos = new Vector2();


    public PlayScreen(Juego game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        Gdx.input.setInputProcessor(this);

        this.game = game;
        //create cam used to follow mario through cam world
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(Juego.V_WIDTH /30, Juego.V_HEIGHT/30 , gamecam);

        //create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        //Load our map and setup our map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("maps/map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1  / Juego.PPM);

        //initially set our gamcam to be centered correctly at the start of of map
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, 0), true);
        //allows for debug lines of our box2d world.
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        //create mario in our game world
        player = new Player(this);
player.setPosition(120,120);
        world.setContactListener(new WorldContactListener());

        music = Juego.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        //music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

         layer = (TiledMapTileLayer)map.getLayers().get(0);
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }


    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }


    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {


    }

    public void handleInput(float dt){
        //control our player using immediate impulses
        liberarTecla(Keys.ABAJO);
        liberarTecla(Keys.ARRIBA);
        liberarTecla(Keys.IZQUIERDA);
        liberarTecla(Keys.DERECHA);
        if(player.currentState != Player.State.DEAD) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && player.b2body.getLinearVelocity().y <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0, 0.1f), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && player.b2body.getLinearVelocity().y >= -2)
                player.b2body.applyLinearImpulse(new Vector2(0, -0.1f), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                player.fire();


        }
    }
    public void pulsarTecla(Keys tecla) {
        keys.put(tecla, true);
    }
    public void liberarTecla(Keys tecla) {
        keys.put(tecla, false);
        player.b2body.applyLinearImpulse(new Vector2(0, 0), player.b2body.getWorldCenter(), true);

    }

    private void handlePathFinding(float delta){

//        goomba.setPosition(player.b2body.getPosition().x,player.b2body.getPosition().y);
//        Vector2 direccion = goomba.puntoDestino.cpy().sub(goomba.b2body.getPosition());
//        goomba.direccion.set(direccion.nor());
//        goomba.update(delta);
    }

    void unproject(Camera cam, Vector2 vec) {
        tmpv3.set(vec.x, vec.y, 0f);
        cam.unproject(tmpv3);
        vec.set(tmpv3.x, tmpv3.y);
    }
    void getMousePos() {
        mousePos.set(player.b2body.getPosition());
    }

    public void update(float dt){
        //handle user input first
        handleInput(dt);
        handleSpawningItems();
        handlePathFinding(dt);
        //takes 1 step in the physics simulation(60 times per second)
        world.step(1 / 60f, 6, 2);

        player.update(dt);
        for(Enemy enemy : creator.getEnemies()) {
           // if(enemy.getX() < player.getX() + 224 / Juego.PPM) {
                enemy.b2body.setActive(true);
               // enemy.setVelocity(player.b2body.getLinearVelocity());
                getMousePos();
                unproject(gamecam, mousePos);
//goomba.se
//            Vector2 direccion = goomba.puntoDestino.cpy().sub(enemy.b2body.getPosition());
          //   enemy.b2body.setTransform(direccion.nor(),0);
            enemy.update(dt);

            // }
        }

        for(Item item : items)
            item.update(dt);

        hud.update(dt);

        //attach our gamecam to our players.x coordinate
            if (player.currentState != Player.State.DEAD) {
                gamecam.position.x = player.b2body.getPosition().x;
                gamecam.position.y = player.b2body.getPosition().y;

            }
        // Horizontal axis
        if(layer.getTileWidth() < gamecam.viewportWidth)
        {
            gamecam.position.x = layer.getTileWidth() / 2;
        }
        else if(gamecam.position.x - gamecam.viewportWidth * .5f <= 0)
        {
            gamecam.position.x = 0 + gamecam.viewportWidth * .5f;
        }
        else if(gamecam.position.x + gamecam.viewportWidth * .5f >= layer.getTileWidth())
        {
            gamecam.position.x = layer.getTileWidth() - gamecam.viewportWidth * .5f;
        }

// Vertical axis
        if(layer.getTileHeight() < gamecam.viewportHeight)
        {
            gamecam.position.y = layer.getTileHeight() / 2;
        }
        else if(gamecam.position.y - gamecam.viewportHeight * .5f <= 0)
        {
            gamecam.position.y = 0 + gamecam.viewportHeight * .5f;
        }
        else if(gamecam.position.y + gamecam.viewportHeight * .5f >= layer.getTileHeight())
        {
            gamecam.position.y = layer.getTileHeight() - gamecam.viewportHeight * .5f;
        }

        //update our gamecam with correct coordinates after changes
        gamecam.update();
        //tell our renderer to draw only what our camera can see in our game world.
        renderer.setView(gamecam);

    }


    @Override
    public void render(float delta) {
        //separate our update logic from render
        update(delta);

        //Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render our game map
        renderer.render();

        //renderer our Box2DDebugLines
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        game.batch.end();

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

    }

    public boolean gameOver(){
        if(player.currentState == Player.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        //updated our game viewport
        gamePort.update(width,height);

    }

    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //dispose of all our opened resources
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public Hud getHud(){ return hud; }

    @Override
    public boolean keyDown(int keycode) {

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.UP:
                liberarTecla(Keys.ARRIBA);
                break;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                liberarTecla(Keys.ABAJO);
                break;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                liberarTecla(Keys.IZQUIERDA);
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                liberarTecla(Keys.DERECHA);
                break;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
