import javafx.scene.image.Image;

import java.util.function.Supplier;

public class CharacterType {
    //this class contains the information to set stats for a gamecharacter
    private String name;
    private Image spriteSheet;
    private double speed;
    private int animationID;
    private Supplier<GameCharacter> supplier;

    public CharacterType(String name, Image spriteSheet, double speed, int animationID, Supplier<GameCharacter> supplier) {
        this.name = name;
        this.spriteSheet = spriteSheet;
        this.speed = speed;
        this.animationID = animationID;
        this.supplier=supplier;
    }

    public Image getSpriteSheet() {
        return spriteSheet;
    }

    public Image getIcon() {
        return Utility.loadImageSector(spriteSheet, 0, 0, TileGame.SIZE, TileGame.SIZE);
    }

    public double getSpeed() {
        return speed;
    }

    public int getAnimationID() {
        return animationID;
    }

    public String getName() {
        return name;
    }

    public Supplier<GameCharacter> getSupplier(){
        return supplier;
    }

}