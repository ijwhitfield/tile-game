import javafx.scene.image.ImageView;

public class Projectile extends Attack {
    private double projectileAngle,projectileSpeed;
    public Projectile(double x, double y, double projectileAngle, double projectileSpeed,
                      GameCharacter owner, double damage, ImageView image, double width, double height){
        super(x,y,owner,damage,image,width,height);
        this.projectileAngle=projectileAngle;
        this.projectileSpeed=projectileSpeed;
    }

    public double getProjectileAngle(){
        return projectileAngle;
    }

    public double getProjectileSpeed(){
        return projectileSpeed;
    }

}
