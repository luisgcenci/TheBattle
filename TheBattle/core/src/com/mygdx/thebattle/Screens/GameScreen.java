package com.mygdx.thebattle.Screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.thebattle.Heroes.Hero;
import com.mygdx.thebattle.Maps.Map;
import com.mygdx.thebattle.TheBattle;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class GameScreen implements Screen {

    public static final int SCREEN_WIDTH = 1600;
    public static final int SCREEN_HEIGHT = 960;

    Map map;
    final TheBattle GAME;
    OrthographicCamera cam;
    final Hero hero;

    ShapeRenderer srLine;
    ShapeRenderer srFilled;

    float elapsedTime = 0f;

    //network
    ArrayList<Hero> playersInGame = new ArrayList<>();
    DataInputStream in;
    DataOutputStream out;
    BufferedReader input;

    public GameScreen(final TheBattle GAME){

        //map
        map = new Map();

        //game
        this.GAME = GAME;

        //hero
        this.hero = new Hero();

        //shape renderer
        srLine = new ShapeRenderer();
        srFilled = new ShapeRenderer();

        //camera
        cam = new OrthographicCamera();
        cam.setToOrtho(false,SCREEN_WIDTH,SCREEN_HEIGHT);
        cam.update();

        //input,output streams from client
        in = this.GAME.getInputStream();
        input = this.GAME.getInputReader();
        out = this.GAME.getOutputStream();
        String heroName = "";

        //get username from user
        try{
            System.out.println(in.readUTF());
            heroName = input.readLine();
            this.hero.setHeroName(heroName);
            out.writeUTF(heroName);

            //get hero id
            this.hero.setHeroId(Integer.parseInt(in.readUTF()));

            //connection accepted and welcome
            System.out.println(in.readUTF());

            //send hero info to server
            this.GAME.getOutputStream().writeUTF(heroName + "," + this.hero.getHeroPositionX() + "," + this.hero.getHeroPositionY() + "," + hero.getCharacterId() + ',' + hero.getHealth());

            //get players already in the game info
            String message = "";
            while (true){
                message = in.readUTF();
                if (message.equals("end")){
                    break;
                }
                String [] messageInfo = message.split(",");
                Hero hero = new Hero(Integer.parseInt(messageInfo[0]), messageInfo[1], Float.parseFloat(messageInfo[2]), Float.parseFloat(messageInfo[3]), Integer.parseInt(messageInfo[4]), Integer.parseInt(messageInfo[5]));
                playersInGame.add(hero);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread serverListenerHandler = new Thread(new Runnable() {
            public void run() {
                String line = "";
                while(!line.equals("gameover")){
                    try{
                        line = in.readUTF();
                        if (line.contains("gameover:")){
                            line = line.replace("gameover:", "");
                            int heroIdOfDead = Integer.parseInt(line);

                            if (hero.getId() == heroIdOfDead){
                                out.writeUTF("close connection");
                                line = "gameover";
                            }
                        }

                        else if (line.contains("event:")){
                            line = line.replace("event:", "");
                            final String [] lineInfo = line.split(",");

                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    if (hero.getId() == Integer.parseInt(lineInfo[0])){
                                        playersInGame.add(hero);
                                    }
                                    else {
                                        Hero hero = new Hero(Integer.parseInt(lineInfo[0]), lineInfo[1], Float.parseFloat(lineInfo[2]), Float.parseFloat(lineInfo[3]), Integer.parseInt(lineInfo[4]), Integer.parseInt(lineInfo[5]));
                                        playersInGame.add(hero);
                                    }
                                }
                            });
                        }
                        else if (line.contains("KeyInput:")){
                            line = line.replace("KeyInput:", "");
                            final String [] lineInfo = line.split(",");
                            final String heroId = lineInfo[0];
                            final float dt = Float.parseFloat(lineInfo[1]);
                            final String key = lineInfo[2];

                            Gdx.app.postRunnable(new Runnable() {

                                @Override
                                public void run() {
                                    for (Hero hero: playersInGame){
                                        if (hero.getId() == Integer.parseInt(heroId)){
                                            if (key.equals("A")){
                                                hero.setHeroPositionX(hero.getHeroPositionX() + (-200 * dt));
                                                hero.setState("running");
                                            }
                                            else if (key.equals("D")){
                                                hero.setHeroPositionX(hero.getHeroPositionX() + (200 * dt));
                                                hero.setState("running");
                                            }
                                            else if (key.equals("W")){
                                                hero.setHeroPositionY(hero.getHeroPositionY() + (200 * dt));
                                                hero.setState("running");
                                            }
                                            else if (key.equals("S")){
                                                hero.setHeroPositionY(hero.getHeroPositionY() + (-200 * dt));
                                                hero.setState("running");
                                            }
                                            else if (key.equals("MOUSE1")){
                                                hero.setState("attacking");
                                                collisionHandler(hero);
                                            }
                                            else if (key.equals("MOUSE2")){
                                                if (hero.getState().equals("defending")){
                                                    hero.setState("idle");
                                                }
                                                else{
                                                    hero.setState("defending");
                                                }
                                            }
                                        }
                                    }
                                }

                                public void collisionHandler(Hero hero){
                                    for (Hero anotherHero : playersInGame){
                                        if (hero.getRectangle().overlaps(anotherHero.getRectangle()) && hero.getId() != anotherHero.getId()){
                                            if (!anotherHero.getState().equals("defending")){
                                                anotherHero.beingAttacked(hero.getPower());
                                                try{
                                                    out.writeUTF("event:new_health" + anotherHero.getId() + "," + anotherHero.getHealth());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                System.out.println(hero.getName() + " (" + hero.getHealth() + ")" + " Attacked " + anotherHero.getName() + " (" + anotherHero.getHealth() + ") ");
                                            }
                                            else {
                                                System.out.println(anotherHero.getName() + "Defended!!!");
                                            }
                                        }
                                    }
                                }

                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
            }
        });

        serverListenerHandler.start();
    }

    @Override
    public void show() {

    }

    public void inputHandler(float dt){

        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            try{
                out.writeUTF("A" + "," + dt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)){
            try{
                out.writeUTF("D" + "," + dt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.W)){
            try{
                out.writeUTF("W" + "," + dt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)){
            try{
                out.writeUTF("S" + "," + dt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            try{
                out.writeUTF("MOUSE1" + "," + dt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)){
            try{
                out.writeUTF("MOUSE2" + "," + dt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    float justPressedTimer = 0f;
    ArrayList<Hero> heroesToRemove = new ArrayList<>();

    @Override
    public void render(float delta) {

        //elapsed time
        elapsedTime += delta;

        //camera
        cam.update();

        //map
        map.getTiledMapRenderer().setView(cam);
        map.getTiledMapRenderer().render();

        //batch and shape renders draw
        this.GAME.getSpriteBatch().begin();
        srLine.begin(ShapeRenderer.ShapeType.Line);
        srFilled.begin(ShapeRenderer.ShapeType.Filled);

        srLine.setColor(0f, 0f, 0f, 0.5f);
        srFilled.setColor(Color.GREEN);

        //check if there are heroes dead
        if (heroesToRemove.size() > 0){
            playersInGame.removeAll(heroesToRemove);
            heroesToRemove.clear();
        }

        //hero
        for (Hero hero : playersInGame){
            if (hero.getState().equals("dead")){
                if (hero.getId() == this.hero.getId()){
                    this.GAME.disconnectGame();
                }
                heroesToRemove.add(hero);
            }
            else if (hero.getState().equals("running")){
                this.GAME.getSpriteBatch().draw(hero.getRunningAnimation().getKeyFrame(elapsedTime, true), hero.getHeroPositionX(),hero.getHeroPositionY());
                hero.setState("idle");
            }
            else if (hero.getState().equals("attacking")){
                this.GAME.getSpriteBatch().draw(hero.getAttackingAnimation().getKeyFrame(justPressedTimer, true), hero.getHeroPositionX(),hero.getHeroPositionY());
                if (hero.getAttackingAnimation().isAnimationFinished(justPressedTimer)){
                    justPressedTimer = 0;
                    hero.setState("idle");
                }
                else{
                    justPressedTimer += delta;
                }
            }
            else{
                this.GAME.getSpriteBatch().draw(hero.getIdleAnimation().getKeyFrame(elapsedTime, true), hero.getHeroPositionX(),hero.getHeroPositionY());
            }

            Rectangle outerHealthBar = hero.getOuterHealthBar();
            Rectangle innerHealthBar = hero.getInnerHealthBar();

            srLine.rect(outerHealthBar.x, outerHealthBar.y, outerHealthBar.width, outerHealthBar.height);
            srFilled.rect(innerHealthBar.x, innerHealthBar.y, innerHealthBar.width, innerHealthBar.height);

//            sr.rect(hero.getHeroPositionX(), hero.getHeroPositionY(), hero.getHeroWidth(), hero.getHeroHeight());
        }

        //handle input
        inputHandler(Gdx.graphics.getDeltaTime());

        this.GAME.getSpriteBatch().end();
        srLine.end();
        srFilled.end();

    }

    @Override
    public void resize(int width, int height) {

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
    }
}

