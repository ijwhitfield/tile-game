import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MapAnchorHandler implements EventHandler<MouseEvent> {
    private Controller c;
    private AnchorPane m;
    public MapAnchorHandler(AnchorPane m, Controller c){
        this.m=m;
        this.c=c;
    }
    public void handle(MouseEvent e){
        if(e.getEventType()==MouseEvent.MOUSE_CLICKED){
            if(c.placingWhat()==1) {
                for(int i = m.getChildren().size()-1; i>=0; i--){
                    if(m.getChildren().get(i) instanceof PlaceableImage &&m.getChildren().get(i).getBoundsInParent().contains(e.getX(),e.getY())){
                        c.removeChar((PlaceableImage)(m.getChildren().get(i)));
                        return;
                    }
                }
                c.addChar(e.getX() / (double) Controller.SIZE, e.getY() / (double) Controller.SIZE, c.getCharPainter());
            }
            else if(c.placingWhat()==2) {
                c.setPlayer(e.getX() / (double) Controller.SIZE, e.getY() / (double) Controller.SIZE);
            }
        }
    }
}
