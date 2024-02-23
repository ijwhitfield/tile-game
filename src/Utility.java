import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.io.*;

public class Utility {
    //i just put these up here because of bad garbage collection
    private static WritableImage imageSector;
    private static Image image;
    private static int[] ints;
    public static Image loadImage(String location){
        try{
            image = new Image(new FileInputStream(new File(location)));
            return image;
        }
        catch(FileNotFoundException e){
            System.out.println("Image failed to load");
            return null;
        }
    }
    public static Image loadImageSector(Image image, int x, int y, int width, int height){
        imageSector = new WritableImage(width, height);
        ints = new int[width*height];
        image.getPixelReader().getPixels(x, y, width, height,
                PixelFormat.getIntArgbInstance(), ints, 0, width);
        imageSector.getPixelWriter().setPixels(0, 0, width, height,
                PixelFormat.getIntArgbInstance(), ints, 0, width);
        return imageSector;
    }

}
