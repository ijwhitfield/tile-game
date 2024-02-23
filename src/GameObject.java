import javafx.geometry.BoundingBox;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

public class GameObject {
    //stretch goal: use interpolation between time steps in rendering thread
    private static final Animation[][] animations = new Animation[][]{
            new Animation[]{
                    new Animation(0, 2, 5), //standing
                    new Animation(1, 2, 5), //walk down
                    new Animation(2, 2, 5), //walk right
                    new Animation(3, 2, 5), //walk left
                    new Animation(4, 2, 5), //walk up
                    new Animation(5, 4, 5), //roll right
                    new Animation(6, 4, 5), //roll left
                    new Animation(7, 4, 5), //roll down
                    new Animation(8, 4, 5), //roll up
            },
            new Animation[]{
                    new Animation(0, 2, 5) //chillin
            },
            new Animation[]{
                    new Animation(0, 2, 5) //chillin
            },
            new Animation[]{
                    new Animation(0, 2, 5) //chillin
            },
            new Animation[]{
                    new Animation(0, 2, 5) //chillin
            },
    };
    private Image img;
    private Image[][] sprites;
    private ImageView imgView;
    private int currentFrame, currentAnimation, animationID, returnAnimation = -1;
    private Animation anim;
    private long animStartTime, lastChanged;
    private double collisionWidth = 0.6, collisionHeight = 0.6, x, y;
    private boolean solid = true;

    public GameObject(double x, double y, Image img) {
        this(x, y, img, -1);
    }

    public GameObject(double x, double y, Image img, int animationID) {
        this(x, y, img, animationID, true);
    }

    public GameObject(double x, double y, Image img, int animationID, boolean solid) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.animationID = animationID;
        this.solid = solid;
        if (animationID > -1) { //if an animation was assigned
            //split up spritesheet and fill sprites array
            sprites = new Image[(int) (img.getHeight() / TileGame.SIZE)][(int) (img.getWidth() / TileGame.SIZE)];
            for (int r = 0; r < img.getWidth() / TileGame.SIZE; r++) {
                for (int c = 0; c < img.getHeight() / TileGame.SIZE; c++) {
                    sprites[r][c] = Utility.loadImageSector(
                            img, c * TileGame.SIZE, r * TileGame.SIZE, TileGame.SIZE, TileGame.SIZE);
                }
            }
            //start animating with animation 0
            setAnimation(0);
        }
        imgView = new ImageView(getImage());
    }

    public void setCollisionWidth(double width) {
        this.collisionWidth = width;
    }

    public void setCollisionHeight(double height) {
        this.collisionHeight = height;
    }

    public void setAnimationID(int ID) {
        this.animationID = ID;
    }

    public void setAnimation(int which) {
        this.animStartTime = System.currentTimeMillis();
        this.currentAnimation = which;
        this.anim = animations[animationID][which];
    }

    public void playAnimation(int which, int returnAnim) {
        returnAnimation = returnAnim;
        this.animStartTime = System.currentTimeMillis();
        this.currentAnimation = which;
        this.anim = animations[animationID][which];
    }

    public Image getImage() {
        //this is called each frame the imageview is on screen to update its image
        if (anim != null) {
            double animLength = ((double) anim.getFrameCount() / (double) anim.getFPS()) * 1000; //animation length in milliseconds
            //if time since last changed is less than the frame length anyway, just don't change frame.
            if (returnAnimation != -1 && System.currentTimeMillis() - animStartTime > animLength) {
                setAnimation(returnAnimation);
                returnAnimation = -1;
            }
            if (System.currentTimeMillis() - lastChanged >= (animLength / (double) anim.getFrameCount())) {
                lastChanged += (animLength / (double) anim.getFrameCount());
                currentFrame = (int) (((System.currentTimeMillis() - animStartTime) % animLength) / (animLength / (double) anim.getFrameCount()));
            }
            return sprites[anim.getRow()][currentFrame];
        }
        return img;
    }

    public ImageView getImgView() {
        return imgView;
    }

    public void setImgView(ImageView imgView) {
        this.imgView = imgView;
    }

    public void setImage(Image img) {
        this.img = img;
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

    public int getAnimation() {
        return currentAnimation;
    }

    public Rectangle getRect() {
        return new Rectangle(getX() - collisionWidth / 2, getY() - collisionHeight / 2, collisionWidth, collisionHeight);
    }

    public BoundingBox getBox() {
        return new BoundingBox(getX() - collisionWidth / 2, getY() - collisionHeight / 2, collisionWidth, collisionHeight);
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean value) {
        this.solid =value;
    }
}
