public class Hero {

    String name;
    float x;
    float y;
    int id;
    public static int heroId = 0;
    int characterId;
    int health;

    public Hero(String name, int x, int y, int health){
        this.id = heroId += 1;
        this.name = name;
        this.x = x;
        this.y = y;
        this.health = health;
    }

    public Hero(){
        this.id = heroId += 1;
        this.health = 100;
    }

    public int getHealth(){
        return this.health;
    }

    public int getCharacterId(){
        return this.characterId;
    }

    public Hero getHero(){
        return this;
    }

    public float getX() { 
        return this.x; 
    }

    public float getY() { 
        return this.y; 
    }

    public String getName(){ 
        return this.name; 
    }

    public int getId(){ 
        return this.id; 
    }

    public void setPosition(String direction, float dt){
        if (direction.equals("A")){
            this.setX(this.getX() + (-200 * dt));
        }
        else if (direction.equals("D")){
            this.setX(this.getX() + (200 * dt));
        }
        else if (direction.equals("W")){
            this.setY(this.getY() + (200 * dt));
        }
        else if (direction.equals("S")){
            this.setY(this.getY() + (-200 * dt));
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setX(float x){ 
        this.x = x;
    }

    public void setY(float y){ 
        this.y = y;
    }

    public void setCharacterId(int characterId){
        this.characterId = characterId;
    }

    public void setHealth(int health){
        this.health = health;
    }

    @Override
    public String toString(){
        return this.name + "," + this.x + "," + this.y;
    }
}
