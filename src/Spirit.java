import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class Spirit extends Enemy {
    public Spirit(double x, double y){
        super(x,y,GameCharacter.characters[4],3);
    }

    private final static double PROJECTILE_SPEED=0.04;

    private int stepsLeft;

    @Override
    public void attack(TileGame t){
        if(Math.abs(Math.sqrt(Math.pow(t.getPlayer().getX()-getX(),2)+
                Math.pow(t.getPlayer().getY()-getY(),2))-preferredDistance)<=1&&stepsLeft<=0&&
                t.isLineSafe(new Line(getX(),getY(),t.getPlayer().getX(),t.getPlayer().getY()))){
            //initiate new attack
            addAttack(new Projectile(getX(),getY(),Math.toDegrees(Math.atan2(t.getPlayer().getY()-getY(),
                    t.getPlayer().getX()-getX())),PROJECTILE_SPEED,this,10,
                    new ImageView(Utility.loadImage("resources/bullet.png")),0.3,0.3),t);
            getAttacks().get(getAttacks().size()-1).getImage().setTranslateX(getImgView().getX());
            getAttacks().get(getAttacks().size()-1).getImage().setTranslateY(getImgView().getY());
            getAttacks().get(getAttacks().size()-1).getImage().setViewOrder(1);
            stepsLeft=100;
        }
        for(int i = 0; i<getAttacks().size(); i++){
            if(getAttacks().get(i) instanceof  Projectile){
                Projectile p = (Projectile)(getAttacks().get(i));
                p.setX(p.getX()+Math.cos(Math.toRadians(p.getProjectileAngle()))*p.getProjectileSpeed());
                p.setY(p.getY()+Math.sin(Math.toRadians(p.getProjectileAngle()))*p.getProjectileSpeed());
                p.getImage().setTranslateX(((p.getX() - t.getPlayer().getX()) * TileGame.SIZE + TileGame.SIZE * 4) - TileGame.SIZE + TileGame.SIZE / 2);
                p.getImage().setTranslateY(((p.getY() - t.getPlayer().getY()) * TileGame.SIZE + TileGame.SIZE * 4) - TileGame.SIZE + TileGame.SIZE / 2);
                if(t.isInTile(p.getX(),p.getY())){
                    removeAttack(p,t);
                }
                else if(!(Shape.intersect(p.getCollision(),t.getPlayer().getRect()).getBoundsInLocal().getWidth()<=-1)&&t.getPlayer().getRollSteps()<=0){
                    t.getPlayer().setHealth(t.getPlayer().getHealth()-p.getDamage());
                    removeAttack(p,t);
                }
            }
        }
        stepsLeft--;
    }
}
