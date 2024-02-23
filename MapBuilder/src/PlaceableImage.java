import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlaceableImage extends ImageView {
    private PositionAndValue pav;
    private Controller c;
    public PlaceableImage(Image i, PositionAndValue pav, Controller c){
        setImage(i);
        this.pav=pav;
        this.c=c;
    }

    public PositionAndValue getPAV(){
        return pav;
    }
}
