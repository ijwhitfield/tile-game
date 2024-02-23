import javafx.scene.shape.Line;

public abstract class Enemy extends GameCharacter{
    double preferredDistance;
    public Enemy(double x, double y, CharacterType ct, double preferredDistance){
        super(x,y,ct);
        this.preferredDistance=preferredDistance;
    }
    public void chasePlayer(TileGame t){

        if(Math.sqrt(Math.pow(t.getPlayer().getX()-getX(),2)+Math.pow(t.getPlayer().getY()-getY(),2))<5&&
                t.isLineSafe(new Line(getX(),getY(),t.getPlayer().getX(),t.getPlayer().getY()))){
            if(Math.sqrt(Math.pow(t.getPlayer().getX()-getX(),2)+Math.pow(t.getPlayer().getY()-getY(),2))>preferredDistance){
                move(Math.toDegrees(Math.atan2(t.getPlayer().getY()-getY(),(t.getPlayer().getX()-getX()))),t);
            }
            else if(Math.sqrt(Math.pow(t.getPlayer().getX()-getX(),2)+Math.pow(t.getPlayer().getY()-getY(),2))<preferredDistance){
                move(Math.toDegrees(Math.atan2(getY()-t.getPlayer().getY(),getX()-t.getPlayer().getX())),0.01,t);
            }
        }
    }
    public abstract void attack(TileGame t);



}
