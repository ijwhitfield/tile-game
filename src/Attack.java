import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;


public class Attack {
    private GameCharacter owner;
    private double x,y,damage,width,height,angle=0,scaleX=1.0,scaleY=1.0;
    private ImageView image;

    public Attack(double x, double y, GameCharacter owner, double damage, ImageView image, double width, double height){
        this.setX(x);
        this.setY(y);
        this.owner=owner;
        this.damage=damage;
        this.setImage(image);
        this.width=width;
        this.height=height;
    }

    public Shape getCollision(){
        Rectangle rect = new Rectangle(x,y-height/2,width,height);
        rect.getTransforms().add(new Rotate(angle,x,y));
        rect.getTransforms().add(new Scale(scaleX,scaleY,x,y));
        return rect;
    }

    public GameCharacter getOwner() {
        return owner;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getDamage() {
        return damage;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle%360;
    }

    public double getScaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

}
