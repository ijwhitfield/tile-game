import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

public class MapTileHandler implements EventHandler<Event> {
    private MapTile m;
    private Controller c;
    public MapTileHandler(MapTile m,Controller c){
        this.m=m;
        this.c=c;
    }
    public void handle(Event e){
        if(c.placingWhat()==0){
            if(e.getEventType()==MouseEvent.MOUSE_PRESSED||e.getEventType()==MouseDragEvent.MOUSE_DRAG_ENTERED){
                //int oldTile = m.getTile();
                m.setTile(c.getTilePainter());
                c.setTile(m.getMapX(),m.getMapY(),m.getTile());
                //int newTile=c.getTilePainter();
                /*c.addUndo((boolean redo) -> {
                        if(!redo){
                            m.setTile(newTile);
                        }
                        else {
                            m.setTile(oldTile);
                        }
                });*/
            }
            if(e.getEventType()==MouseEvent.DRAG_DETECTED){
                m.startFullDrag();
            }
        }
    }
}
