import javafx.scene.image.Image;
import java.security.InvalidParameterException;

public class MovingGameObject extends GameObject {

    private double speed = 0.04; //units per timestep
    private double lastVelX, lastVelY;

    public MovingGameObject(double x, double y, Image img, int animationID, double speed){
        super(x,y,img,animationID);
        this.speed = speed;
    }

    public static double keysToDegrees(boolean[] keys){
        double degX=0;
        double degY=0;
        if(keys[0]) degY--;
        if(keys[1]) degX--;
        if(keys[2]) degY++;
        if(keys[3]) degX++;
        if(degX==0&&degY==0){
            return -1;
        }
        double answer = Math.toDegrees(Math.atan2(degY,degX));
        if(answer<0){
            answer+=360;
        }
        return answer;
    }

    public void move(double angle, TileGame t){
        move(angle, speed, t);
    }
    public void move(double angle, double moveSpeed, TileGame t){
        move(angle, moveSpeed, false, false, t);
    }
    public void move(double angle, double moveSpeed, boolean phaseTiles, boolean phaseObjects, TileGame t){
        if(angle==-1){
            lastVelX=0;
            lastVelY=0;
            return;
        }

        lastVelX=getX();
        lastVelY=getY();
        double moveX=Math.cos(Math.toRadians(angle));
        double moveY=Math.sin(Math.toRadians(angle));

        double addX = moveX*moveSpeed;
        double addY = moveY*moveSpeed;
        int side,tileA,tileB;

        if(moveX!=0){
            tileA=(int)Math.floor(getBox().getMinY());
            tileB=(int)Math.floor(getBox().getMaxY());
            if(moveX>0){ //moving right
                side=(int)Math.floor(getBox().getMaxX()+addX);
            }
            else{ //moving left
                side=(int)Math.floor(getBox().getMinX()+addX);
            }
            if(t.isSafe(side,tileA,this,phaseTiles,phaseObjects)&&t.isSafe(side,tileB,this,phaseTiles,phaseObjects)){
                setX(getX()+addX);
            }
        }

        if(moveY!=0){
            tileA=(int)Math.floor(getBox().getMinX());
            tileB=(int)Math.floor(getBox().getMaxX());
            if(moveY>0){ //moving down
                side=(int)Math.floor(getBox().getMaxY()+addY);
            }
            else{ //moving up
                side=(int)Math.floor(getBox().getMinY()+addY);
            }
            if(t.isSafe(tileA,side,this,phaseTiles,phaseObjects)&&t.isSafe(tileB,side,this,phaseTiles,phaseObjects)){
                setY(getY()+addY);
            }
        }

        lastVelX=(getX()-lastVelX);
        lastVelY=(getY()-lastVelY);
    }

    public void collisionResolve(TileGame t){
        collisionResolve(t,false,false);
    }

    public void collisionResolve(TileGame t, boolean phaseTiles, boolean phaseObjects){
        //the purpose of this method is to push out of a wall in a catastrophic event or to push out of another gameobject

        /*
        if one of the four corners are currently inside of a filled tile
            find which of the four corners are not in a filled tile
            of the empty tiled corners, pick which tile center is closest to object center
            until corners have changed tiles, move towards that tile, dimensions individual
         */

        if(!phaseTiles){
            int miny=(int)Math.floor(getBox().getMinY());
            int maxy=(int)Math.floor(getBox().getMaxY());
            int minx=(int)Math.floor(getBox().getMinX());
            int maxx=(int)Math.floor(getBox().getMaxX());

            if(t.isInTile(minx,miny)){
                setY(miny+0.5);
                setX(minx+0.5);
            }
            else if(t.isInTile(minx,maxy)){
                setY(maxy+0.5);
                setX(minx+0.5);
            }
            else if(t.isInTile(maxx,miny)){
                setY(miny+0.5);
                setX(maxx+0.5);
            }
            else if(t.isInTile(maxx,maxy)){
                setY(maxy+0.5);
                setX(maxx+0.5);
            }
        }

        if(!phaseObjects){
            GameObject[] objs = t.getGameObjects();
            for(int i = 0; i<objs.length; i++) {
                if (objs[i] == this||!objs[i].isSolid()) {
                    continue;
                }
                if (Math.abs(objs[i].getX() - getX()) <= 4 && Math.abs(objs[i].getY() - getY()) <= 4) {
                    double push;
                    push = 0.02;
                    if ((getBox().intersects(objs[i].getBox()))) {
                        if (objs[i].getBox().getCenterX() > getBox().getCenterX()) {
                            move(180, push, false, true, t);
                        } else if (objs[i].getBox().getCenterX() < getBox().getCenterX()) {
                            move(360, push, false, true, t);
                        } else {
                            if (Math.random() > 0.5) {
                                move(180, push, false, true, t);
                            } else {
                                move(360, push, false, true, t);
                            }
                        }
                        if (objs[i].getBox().getCenterY() > getBox().getCenterY()) {
                            move(270, push, false, true, t);
                        } else if (objs[i].getBox().getCenterY() < getBox().getCenterY()) {
                            move(90, push, false, true, t);
                        } else {
                            if (Math.random() > 0.5) {
                                move(270, push, false, true, t);
                            } else {
                                move(90, push, false, true, t);
                            }
                        }
                    }
                }
            }
        }
    }

    public double getLastVelX(){
        return lastVelX;
    }

    public double getLastVelY(){
        return lastVelY;
    }

}
