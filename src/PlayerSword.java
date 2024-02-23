import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

public class PlayerSword extends Attack {

    public final static double SWORD_WIDTH=2,SWORD_HEIGHT=1,SWING_SPEED=4.5,FLIP_SPEED=0.8;
    private boolean swordActive = false, swordVisible = false, swordReady = true, unflip = false;
    private Double currentTarget, nextTarget;
    private int lastStepActive, swordSwingType, swingTime; //swingTypes: 0 is none, 1 is clockwise, 2 is counterclockwise, 3 is flip
    public PlayerSword(Player p){
        super(p.getX(),p.getY(),p,25,
                new ImageView(Utility.loadImage("resources/sword.png")),SWORD_WIDTH,SWORD_HEIGHT);
    }


    public void update(){
        /*
        mathematical formula for shortest rotation is from:
        Unsigned (https://math.stackexchange.com/users/16400/unsigned),
            Shortest way to achieve target angle, URL (version: 2018-08-29): https://math.stackexchange.com/q/2898118
         */
        setX(getOwner().getX());
        setY(getOwner().getY());
        if(swordReady){
            //start moving to the next target
            swordReady=false;
            if((currentTarget-getAngle()+540)%360-180==-180){
                swordSwingType=3;
                swingTime=(int)Math.abs(Math.floor(((180/SWING_SPEED))));
            }
            else if((currentTarget-getAngle()+540)%360-180>0){
                swordSwingType=1;
                swingTime=(int)Math.abs(Math.floor((((currentTarget+180)-(getAngle()+180)+540)%360-180)/SWING_SPEED));
            }
            else if((currentTarget-getAngle()+540)%360-180<0){
                swordSwingType=2;
                swingTime=(int)Math.abs(Math.floor((((currentTarget+180)-(getAngle()+180)+540)%360-180)/SWING_SPEED));
            }
        }
        if(swingTime<=0){
            //if swing is complete
            setAngle(currentTarget);
            swordReady=true;
            swordSwingType=0;
            unflip=false;
            if(nextTarget!=null){
                currentTarget=nextTarget;
                nextTarget=null;
            }
            getImage().getTransforms().clear();
            getImage().getTransforms().add(new Rotate(getAngle(),TileGame.SIZE*4,TileGame.SIZE*4));
        }
        else{
            //if swing is in motion
            if(swordSwingType==1){
                setAngle(getAngle()+SWING_SPEED);
                getImage().getTransforms().add(new Rotate(SWING_SPEED,TileGame.SIZE*4,TileGame.SIZE*4));
            }
            else if(swordSwingType==2){
                setAngle(getAngle()-SWING_SPEED);
                getImage().getTransforms().add(new Rotate(-SWING_SPEED,TileGame.SIZE*4,TileGame.SIZE*4));
            }
            else if(swordSwingType==3){
                if(swingTime==(int)Math.abs(Math.floor(((180/SWING_SPEED))))/2){
                    unflip = true;
                    setAngle(getAngle()+180);
                    getImage().getTransforms().add(new Rotate(180,TileGame.SIZE*4,TileGame.SIZE*4));
                }
                if(!unflip){
                    setScaleX(getScaleX()*FLIP_SPEED);
                    getImage().getTransforms().add(new Scale(FLIP_SPEED,1,TileGame.SIZE*4,TileGame.SIZE*4));
                }
                else{
                    setScaleX(getScaleX()/FLIP_SPEED);
                    getImage().getTransforms().add(new Scale(1.0/FLIP_SPEED,1,TileGame.SIZE*4,TileGame.SIZE*4));
                }
            }
            swingTime--;
        }
    }

    public boolean isActive(){
        return swordActive;
    }

    public void activate(double initialTarget, TileGame t){
        getOwner().addAttack(this,t);
        setAngle(initialTarget);
        lastStepActive=t.getStepCounter();
        swordActive=true;
        addTarget(initialTarget,t);
        getImage().getTransforms().add(new Rotate(initialTarget,TileGame.SIZE*4,TileGame.SIZE*4));
    }

    public void deactivate(TileGame t){
        getOwner().removeAttack(this,t);
        swordActive=false;
        currentTarget=null;
        nextTarget=null;
        getImage().getTransforms().clear();
        swingTime=0;
        swordSwingType=0;
        swordReady=true;
        unflip=false;
        setScaleX(1);
    }

    public boolean isVisible(){
        return swordVisible;
    }

    public void setVisible(boolean value){
        swordVisible=value;
    }

    public void setLastStepActive(int step){
        lastStepActive=step;
    }

    public int getLastStepActive(){
        return lastStepActive;
    }

    public void addTarget(double target,TileGame t){
        if(target<=0){
            target+=360;
        }
        if(currentTarget==null){
            currentTarget=target;
        }
        else if(nextTarget==null||nextTarget!=target){
            nextTarget=target;
        }
        lastStepActive=t.getStepCounter();
    }
}
