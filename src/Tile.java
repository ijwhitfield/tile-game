import javafx.scene.image.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Tile extends ImageView {
    private static TileType[] tileTypes;
    private static Image tileSet;
    private int tile;
    public static void loadTileset(String location){
        tileSet=Utility.loadImage(location);
        tileTypes = new TileType[(int)(tileSet.getWidth()/ TileGame.SIZE)*(int)(tileSet.getHeight()/ TileGame.SIZE)];
        for(int i = 0; i<tileTypes.length; i++){
            tileTypes[i]=new TileType(0,"void",true);
        }
        try{
            readMeta(location.substring(0,location.lastIndexOf('.'))+".meta");
        }catch(IOException e){}
        for(int r = 0; r<tileSet.getWidth()/ TileGame.SIZE; r++) {
            for (int c = 0; c < tileSet.getHeight() / TileGame.SIZE; c++) {
                tileTypes[r * (int)(tileSet.getWidth()/ TileGame.SIZE) + c].setImage(Utility.loadImageSector(tileSet, c * TileGame.SIZE, r * TileGame.SIZE,
                        TileGame.SIZE, TileGame.SIZE));
                tileTypes[r * (int)(tileSet.getWidth()/ TileGame.SIZE) + c].setId(r * (int)(tileSet.getWidth()/ TileGame.SIZE) + c);
            }
        }
    }

    private static void readMeta(String location) throws IOException{
        //read meta file and import into editor
        byte[] dst = new byte[Integer.BYTES];
        File f = new File(location);
        FileInputStream fis = new FileInputStream(f);
        fis.read(dst,0,Integer.BYTES);
        ByteBuffer buf = ByteBuffer.wrap(dst);
        int capacity = buf.getInt();
        dst = new byte[capacity];
        fis.read(dst);
        buf=ByteBuffer.wrap(dst);
        buf.position(0);
        //read each tiletype and place in memory
        for(int i = 0; i<tileTypes.length;i++){
            int stringLength = buf.getChar();
            byte[] stringBytes = new byte[stringLength];
            buf.get(stringBytes,0,stringLength);
            tileTypes[i].setName(new String(stringBytes));
            tileTypes[i].setMaterial(buf.getChar());
            tileTypes[i].setFilled(buf.get()==(byte)1);
        }
    }

    public Tile(int tile){
        setTile(tile);
    }
    public void setTile(int tile){
        this.tile=tile;
        setImage(tileTypes[tile].getImage());
    }

    public static TileType[] getTypes(){
        return tileTypes;
    }

    public int getTile(){
        return tile;
    }
}
