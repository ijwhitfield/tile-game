
public class PositionAndValue {
    private double x;
    private double y;
    private int value;
    public PositionAndValue(double x, double y, int value){
        setX(x);
        setY(y);
        setValue(value);
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
