import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public abstract class GameCharacter extends MovingGameObject {

    public final static int HEALTH_BAR_WIDTH=64,HEALTH_BAR_HEIGHT=15;
    private boolean alreadyHit=false;

    public final static CharacterType[] characters = new CharacterType[]{
            new CharacterType("player",
                    Utility.loadImage("resources/spritesheets/player.png"),0.04,0,() -> new Player(0,0)),
            new CharacterType("tomato",
                    Utility.loadImage("resources/spritesheets/tomato.png"),0.02,1,() -> new Tomato(0,0)),
            new CharacterType("brute",
                    Utility.loadImage("resources/spritesheets/brute.png"),0.02,2,() -> new Brute(0,0)),
            new CharacterType("zombie",
                    Utility.loadImage("resources/spritesheets/zombie.png"),0.02,3,() -> new Zombie(0,0)),
            new CharacterType("spirit",
                    Utility.loadImage("resources/spritesheets/spirit.png"),0.02,4,() -> new Spirit(0,0))
    };

    private ArrayList<Attack> attacks = new ArrayList<>();
    private Rectangle[] healthBars = new Rectangle[2];
    private double health=100,totalHealth=100;

    public GameCharacter(double x, double y, CharacterType ct){
        super(x,y,ct.getSpriteSheet(),ct.getAnimationID(),ct.getSpeed());
        healthBars[0]=new Rectangle(x,y,HEALTH_BAR_WIDTH,HEALTH_BAR_HEIGHT);
        healthBars[1]=new Rectangle(x,y,HEALTH_BAR_WIDTH,HEALTH_BAR_HEIGHT);
        healthBars[0].setLayoutY(-40);
        healthBars[0].setFill(Color.rgb(0,255,0));
        healthBars[1].setLayoutY(-40);
        healthBars[1].setFill(Color.rgb(255,0,0));
    }

    public void update(TileGame t){
        if(!t.getNodes().contains(healthBars[0])){
            healthBars[0].setViewOrder(-1);
            t.addNode(healthBars[0]);
        }
        if(!t.getNodes().contains(healthBars[1])){
            healthBars[1].setViewOrder(-1);
            t.addNode(healthBars[1]);
        }
        healthBars[0].setX(((getX() - t.getPlayer().getX()) * TileGame.SIZE + TileGame.SIZE *4) - TileGame.SIZE+TileGame.SIZE/2);
        healthBars[0].setY(((getY() - t.getPlayer().getY()) * TileGame.SIZE + TileGame.SIZE *4) - TileGame.SIZE+TileGame.SIZE/2);
        healthBars[1].setX(((getX() - t.getPlayer().getX()) * TileGame.SIZE + TileGame.SIZE *4) - TileGame.SIZE+TileGame.SIZE/2);
        healthBars[1].setY(((getY() - t.getPlayer().getY()) * TileGame.SIZE + TileGame.SIZE *4) - TileGame.SIZE+TileGame.SIZE/2);
        healthBars[0].setWidth((health/totalHealth)*HEALTH_BAR_WIDTH);
        healthBars[1].setLayoutX((health/totalHealth)*HEALTH_BAR_WIDTH);
        healthBars[1].setWidth(((totalHealth-health)/totalHealth)*HEALTH_BAR_WIDTH);
    }

    public ArrayList<Attack> getAttacks(){
        return attacks;
    }
    public void addAttack(Attack a, TileGame t){
        t.addNode(a.getImage());
        attacks.add(a);
    }

    public void removeAttack(Attack a, TileGame t){
        t.removeNode(a.getImage());
        attacks.remove(a);
    }

    public void setHealth(double value){
        this.health=value;
    }

    public double getHealth(){
        return health;
    }

    public Rectangle[] getHealthBars(){
        return healthBars;
    }

    public void setAlreadyHit(boolean value){
        alreadyHit=value;
    }

    public boolean isAlreadyHit(){
        return alreadyHit;
    }

}
