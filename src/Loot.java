public class Loot extends GameObject {
    private int value;
    public Loot(double x, double y, int value){
        super(x,y,Utility.loadImage("resources/loot.png"),-1,false);
        this.value=value;
    }
    public int getValue(){
        return value;
    }
}
