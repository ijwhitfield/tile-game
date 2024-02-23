import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class Handler implements EventHandler<KeyEvent> {
    private boolean[] keys = new boolean[11];
    private TileGame t;
    private boolean stop = false;
    /* KEYS IN ORDER
    0 W
    1 A
    2 S
    3 D
    4 SPACE
    5 ESCAPE
    6 SHIFT
    7 U
    8 I
    9 O
    10 P
     */

    public Handler(TileGame t){
        this.t=t;
    }

    @Override
    public void handle(KeyEvent e){
        if(stop){
            keys=new boolean[11];
            return;
        }
        if(e.getEventType()==KeyEvent.KEY_PRESSED){
            switch(e.getCode()){
                case W:
                    keys[0]=true;
                    break;
                case A:
                    keys[1]=true;
                    break;
                case S:
                    keys[2]=true;
                    break;
                case D:
                    keys[3]=true;
                    break;
                case U:
                    keys[7]=true;
                    break;
                case I:
                    keys[8]=true;
                    break;
                case SPACE:
                    keys[4]=true;
                    break;
            }
        }
        else if(e.getEventType()==KeyEvent.KEY_RELEASED){
            switch(e.getCode()){
                case W:
                    keys[0]=false;
                    break;
                case A:
                    keys[1]=false;
                    break;
                case S:
                    keys[2]=false;
                    break;
                case D:
                    keys[3]=false;
                    break;
                case U:
                    keys[7]=false;
                    break;
                case I:
                    keys[8]=false;
                    break;
                case SPACE:
                    keys[4]=false;
                    break;
            }
        }

    }
    public boolean[] getKeys(){return keys;}

    public void stop(){
        stop=true;
    }
}
