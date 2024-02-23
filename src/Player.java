public class Player extends GameCharacter {
    private PlayerSword sword = new PlayerSword(this);
    private final static double ROLL_SPEED=0.08;
    private double rollAngle;
    private int rollSteps=0;
    public Player(double x, double y){
        super(x,y,GameCharacter.characters[0]);
        sword.getImage().setX(TileGame.SIZE*4.5);
        sword.getImage().setY(TileGame.SIZE*3.5);
    }

    public PlayerSword getSword(){
        return sword;
    }

    @Override
    public void move(double angle, TileGame t){
        if(rollSteps>0){
            super.move(rollAngle,ROLL_SPEED,false,true,t);
            rollSteps--;
        }
        else{
            super.move(angle,t);
        }
         for(int i = 0; i<t.getLoot().size(); i++){
             Loot l = t.getLoot().get(i);
            if(Math.sqrt(Math.pow(getX()-l.getX(),2)+Math.pow(getY()-l.getY(),2))<1){
                t.takeLoot(l);
                i--;
                continue;
            }
        }
    }

    public void roll(double angle,TileGame t){
        rollSteps=60;
        rollAngle=angle;
        sword.setLastStepActive(0);
        sword.deactivate(t);
    }

    public int getRollSteps(){
        return rollSteps;
    }
}
