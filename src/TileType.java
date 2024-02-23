import javafx.scene.image.Image;

public class TileType {
    private int id, material;
    private String name;
    private Image img;
    private boolean filled;

    public TileType(int material, String name, boolean filled){
        this(material,name,filled,null);
    }

    public TileType(int material, String name, boolean filled, Image img){
        this.material=material;
        this.name=name;
        this.filled=filled;
        this.img=img;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setImage(Image img){
        this.img=img;
    }

    public Image getImage() {
        return img;
    }

    public boolean isFilled(){
        return filled;
    }

    public String getName(){
        return name;
    }

    public int getMaterial(){
        return material;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setMaterial(int mat){
        this.material=mat;
    }

    public void setFilled(boolean filled){
        this.filled=filled;
    }

}
