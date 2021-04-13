package com.mygdx.thebattle.Heroes;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.thebattle.Maps.Map;

public class Hero {

    TextureAtlas runningTexAtlas;
    TextureAtlas idleTexAtlas;
    TextureAtlas attackTexAtlas;
    Animation<TextureRegion> runningAnimation;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> attackAnimation;

    Rectangle rec;
    int id;
    String name;
    String state;
    int characterId;
    int health;
    int power;
    Rectangle outerHealthBar;
    Rectangle innerHealthBar;

    public Hero(){

        this.characterId = (int) ((Math.random() * 3) + 1);
        this.state = "idle";

        this.rec = new Rectangle();
        this.health = 100;
        this.power = 10;

        setAnimations();
        setWidthAndHeight();

        this.rec.x = (float) Math.random() * (Map.MAP_WIDTH - this.rec.width);
        this.rec.y = (float) Math.random() * (Map.MAP_HEIGHT - this.rec.height);

        //bar health
        this.outerHealthBar = new Rectangle();
        this.innerHealthBar = new Rectangle();
        this.innerHealthBar.width = 50;
        this.innerHealthBar.height = 20;
        this.outerHealthBar.width = 50;
        this.outerHealthBar.height = 20;
        setHealthBar();
    }

    public Hero(int id, String name, float x, float y, int characterId, int health){

        this.id = id;
        this.name = name;
        this.characterId = characterId;
        this.state = "idle";

        this.rec = new Rectangle();
        this.rec.x = x;
        this.rec.y = y;
        this.health = 100;
        this.power = 10;
        this.health = health;

        setAnimations();
        setWidthAndHeight();

        //bar health
        this.outerHealthBar = new Rectangle();
        this.innerHealthBar = new Rectangle();
        this.innerHealthBar.width = 50;
        this.innerHealthBar.height = 20;
        this.outerHealthBar.width = 50;
        this.outerHealthBar.height = 20;
        setHealthBar();
    }

    //GETTERS
    public Animation<TextureRegion> getRunningAnimation(){ return this.runningAnimation; }
    public Animation<TextureRegion> getIdleAnimation(){ return this.idleAnimation; }
    public Animation<TextureRegion> getAttackingAnimation(){ return this.attackAnimation; }

    public Rectangle getOuterHealthBar() {
        return outerHealthBar;
    }

    public Rectangle getInnerHealthBar() {
        return innerHealthBar;
    }

    public String getName(){ return this.name; }

    public int getPower() { return this.power; }

    public int getHealth(){ return this.health; }

    public int getCharacterId(){ return this.characterId; }

    public String getState(){ return this.state; }

    public int getId(){ return this.id; }

    public Rectangle getRectangle(){ return this.rec; }


    public float getHeroPositionX() { return this.rec.x; }

    public float getHeroPositionY() { return this.rec.y; }

    //SETTERS

    public void setPower(int newPower) { this.power = newPower; }

    public void setHealth(int newHealth){ this.health = newHealth; }

    public void setHeroId(int id ) {
        this.id = id;
    }

    public void setWidth(int width){ this.rec.width = width; }
    public void setHeight(int height){ this.rec.height = height; }

    public void setHeroPositionX(float x) {
        setHealthBar();
        this.rec.x = x;
    }

    public void setHeroPositionY(float y ) {
        setHealthBar();
        this.rec.y = y;
    }

    public void setHealthBar(){

        this.outerHealthBar.x = (this.rec.x + (this.rec.width/2) - (this.outerHealthBar.width / 2));
        this.outerHealthBar.y = (this.rec.y + (this.rec.height + 20));
        this.innerHealthBar.x = (this.rec.x + (this.rec.width/2) - (this.outerHealthBar.width / 2));
        this.innerHealthBar.y = (this.rec.y + (this.rec.height + 20));

        this.innerHealthBar.width = (((float) this.health) / 100) * this.outerHealthBar.width;
    }

    public void beingAttacked(int damage){
        this.health -= damage;
        setHealthBar();
        if (this.health <= 0){
            setState("dead");
        }
    }

    public void setWidthAndHeight(){
        switch(this.characterId){
            case 1:
                setWidth(150);
                setHeight(105);
                break;
            case 2:
                setWidth(115);
                setHeight(115);
                break;
            case 3:
                setWidth(110);
                setHeight(105);
                break;
        }
    }

    public void setAnimations(){
        if (this.characterId == 1){
            //texturesAtlas
            this.runningTexAtlas = new TextureAtlas("Knights/Knight1/run/sprite.txt");
            this.idleTexAtlas = new TextureAtlas("Knights/Knight1/idle/sprite.txt");
            this.attackTexAtlas = new TextureAtlas("Knights/Knight1/attack/sprite.txt");

            //animations
            this.runningAnimation = new Animation<TextureRegion>(0.033f, this.runningTexAtlas.findRegions("_RUN"), PlayMode.LOOP);
            this.idleAnimation = new Animation<TextureRegion>(0.066f, this.idleTexAtlas.findRegions("_IDLE"), PlayMode.LOOP);
            this.attackAnimation = new Animation<TextureRegion>(0.1f, this.attackTexAtlas.findRegions("ATTACK"), PlayMode.LOOP);
        }
        else if(this.characterId == 2){
            //texturesAtlas
            this.runningTexAtlas = new TextureAtlas("Knights/Knight2/run/sprite.txt");
            this.idleTexAtlas = new TextureAtlas("Knights/Knight2/idle/sprite.txt");
            this.attackTexAtlas = new TextureAtlas("Knights/Knight2/attack/sprite.txt");

            //animations
            this.runningAnimation = new Animation<TextureRegion>(0.033f, this.runningTexAtlas.findRegions("_RUN"), PlayMode.LOOP);
            this.idleAnimation = new Animation<TextureRegion>(0.066f, this.idleTexAtlas.findRegions("_IDLE"), PlayMode.LOOP);
            this.attackAnimation = new Animation<TextureRegion>(0.1f, this.attackTexAtlas.findRegions("_ATTACK"), PlayMode.LOOP);
        }
        else {
            //texture atlas
            this.runningTexAtlas = new TextureAtlas("Knights/Knight3/run/sprite.txt");
            this.idleTexAtlas = new TextureAtlas("Knights/Knight3/idle/sprite.txt");
            this.attackTexAtlas = new TextureAtlas("Knights/Knight3/attack/sprite.txt");

            //animations
            this.runningAnimation = new Animation<TextureRegion>(0.033f, this.runningTexAtlas.findRegions("_RUN"), PlayMode.LOOP);
            this.idleAnimation = new Animation<TextureRegion>(0.066f, this.idleTexAtlas.findRegions("_IDLE"), PlayMode.LOOP);
            this.attackAnimation = new Animation<TextureRegion>(0.1f, this.attackTexAtlas.findRegions("_ATTACK"), PlayMode.LOOP);
        }
    }

    public void setState(String state){ this.state = state;}

    public void setHeroName(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return this.id + "," + this.name + "," + this.rec.x + "," + this.rec.y;
    }
}
