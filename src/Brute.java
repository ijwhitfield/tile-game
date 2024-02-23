import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

public class Brute extends Enemy {
    public Brute(double x, double y){
        super(x,y,GameCharacter.characters[2],2);
    }

    private final static double SWORD_SCALE_SPEED=Math.pow(2.0,1.0/50.0)-1.0;

    private int stepsLeft = 0;
    private ImageView swordImage = new ImageView(Utility.loadImage("resources/bruteSword.png"));
    private Attack sword = new Attack(getX(),getY(),this,10,swordImage,1,0.4);
    private boolean alreadyAttacked=false;

    @Override
    public void attack(TileGame t){
        sword.setX(getX());
        sword.setY(getY());
        swordImage.setTranslateX(getImgView().getX()+TileGame.SIZE/2.0);
        swordImage.setTranslateY(getImgView().getY());
        if(Math.abs(Math.sqrt(Math.pow(t.getPlayer().getX()-getX(),2)+
                Math.pow(t.getPlayer().getY()-getY(),2))-preferredDistance)<=1&&stepsLeft<=0&&
        t.isLineSafe(new Line(getX(),getY(),t.getPlayer().getX(),t.getPlayer().getY()))){
            //initiate new attack
            stepsLeft=100;
            //set attack angle to aim at player
            sword.setAngle(Math.toDegrees(Math.atan2(t.getPlayer().getY()-getY(),(t.getPlayer().getX()-getX()))));
            swordImage.setViewOrder(1);
            swordImage.getTransforms().clear();
            sword.setScaleX(1);
            swordImage.getTransforms().add(new Rotate(sword.getAngle(),0,TileGame.SIZE/2));
            alreadyAttacked=false;
            addAttack(sword,t);
        }
        if(stepsLeft>50){
            //scale up attack
            sword.setScaleX(sword.getScaleX()*(1.0+SWORD_SCALE_SPEED));
            swordImage.getTransforms().add(new Scale(1.0+SWORD_SCALE_SPEED,1,0,TileGame.SIZE/2));
        }
        else if(stepsLeft>0){
            //scale down
            sword.setScaleX(sword.getScaleX()*(1.0-SWORD_SCALE_SPEED));
            swordImage.getTransforms().add(new Scale(1.0-SWORD_SCALE_SPEED,1,0,TileGame.SIZE/2));
        }
        else{
            removeAttack(sword,t);
        }
        if(stepsLeft>0&&!alreadyAttacked){
            if(!(Shape.intersect(sword.getCollision(),t.getPlayer().getRect()).getBoundsInLocal().getWidth()<=-1)&&t.getPlayer().getRollSteps()<=0){
                alreadyAttacked=true;
                t.getPlayer().setHealth(t.getPlayer().getHealth()-sword.getDamage());
            }
        }
        stepsLeft--;
    }
}
