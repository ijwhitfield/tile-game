import javafx.scene.control.Button;

public class DataButton extends Button {
    private final int value;
    public DataButton(int value){
        super();
        this.value=value;
    }
    public int getValue(){
        return value;
    }
}
