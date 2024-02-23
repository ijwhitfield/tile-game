import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

public class Zombie extends Enemy {
    public Zombie(double x, double y){
        super(x,y,GameCharacter.characters[3],1);
    }

    private final static double FIST_SCALE_SPEED=Math.pow(2.0,1.0/50.0)-1.0;

    private int stepsLeft = 0;
    private ImageView fistImage = new ImageView(Utility.loadImage("resources/zombieFist.png"));
    private Attack fist = new Attack(getX(),getY(),this,10,fistImage,0.4,0.4);
    private boolean alreadyAttacked=false;

    @Override
    public void attack(TileGame t){
        fist.setX(getX());
        fist.setY(getY());
        fistImage.setTranslateX(getImgView().getX()+TileGame.SIZE/2.0);
        fistImage.setTranslateY(getImgView().getY());
        if(Math.abs(Math.sqrt(Math.pow(t.getPlayer().getX()-getX(),2)+
                Math.pow(t.getPlayer().getY()-getY(),2))-preferredDistance)<=1&&stepsLeft<=0&&
                t.isLineSafe(new Line(getX(),getY(),t.getPlayer().getX(),t.getPlayer().getY()))){
            //initiate new attack
            stepsLeft=100;
            //set attack angle to aim at player
            fist.setAngle(Math.toDegrees(Math.atan2(t.getPlayer().getY()-getY(),(t.getPlayer().getX()-getX()))));
            fistImage.setViewOrder(1);
            fistImage.getTransforms().clear();
            fist.setScaleX(1);
            fistImage.getTransforms().add(new Rotate(fist.getAngle(),0,TileGame.SIZE/2));
            fistImage.getTransforms().add(new Scale(0.5, 1,0,TileGame.SIZE/2));
            alreadyAttacked=false;
            addAttack(fist,t);
        }
        if(stepsLeft>50){
            //scale up attack
            fist.setScaleX(fist.getScaleX()*(1.0+FIST_SCALE_SPEED));
            fistImage.getTransforms().add(new Scale(1.0+FIST_SCALE_SPEED,1,0,TileGame.SIZE/2));
        }
        else if(stepsLeft>0){
            //scale down
            fist.setScaleX(fist.getScaleX()*(1.0-FIST_SCALE_SPEED));
            fistImage.getTransforms().add(new Scale(1.0-FIST_SCALE_SPEED,1,0,TileGame.SIZE/2));
        }
        else{
            removeAttack(fist,t);
        }
        if(stepsLeft>0&&!alreadyAttacked){
            if(!(Shape.intersect(fist.getCollision(),t.getPlayer().getRect()).getBoundsInLocal().getWidth()<=-1)&&t.getPlayer().getRollSteps()<=0){
                alreadyAttacked=true;
                t.getPlayer().setHealth(t.getPlayer().getHealth()-fist.getDamage());
            }
        }
        stepsLeft--;
    }
}
