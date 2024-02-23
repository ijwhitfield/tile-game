public class MapTile extends Tile {
    /*
    the point of this class is to have the benefits of Tile, but overriding some things like image size
    as well as being able to do mouseevent handling so you can paint the tile and mouseover etc..
     */

    private int mapX;
    private int mapY;

    public MapTile(int x, int y, int tile){
        super(tile);
        this.mapX=x;
        this.mapY=y;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }
}
