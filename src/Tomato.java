public class Tomato extends Enemy {
    public Tomato(double x, double y){
        super(x,y,GameCharacter.characters[1],2);
    }

    @Override
    public void attack(TileGame t){} //tomato is pacifist :)
}
