//the purpose of this class is to start the application and draw everything on screen

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TileGame extends Application {

    public static final int WINDOW_WIDTH = 704, WINDOW_HEIGHT = 512, SIZE=64;
    private Player player;
    private ArrayList<GameObject> gameObjects = new ArrayList<>(); //list of objects in this game instance
    private Tile[][] activeTiles;
    private int[][] tilemap;
    private TilePane tilePane = new TilePane();
    private Group gameObjectGroup = new Group(), root = new Group();
    private Handler h=new Handler(this);
    private boolean[] lastInputs=h.getKeys();
    private double lastTimestep, timeGameEnded=-1;
    private int stepsPerSecond=100, stepCounter=0,score=0;
    private String tilesetName,mapName;
    private Shape debugShape = new Rectangle(0,0);
    private ArrayList<Node> nodes = new ArrayList<>();
    private Label gameOverLabel = new Label("YOU DIED"), winLabel = new Label("VICTORY ACHIEVED"), scoreLabel = new Label("$"+score), enemiesLeftLabel = new Label("Enemies Left: ");
    private ArrayList<Loot> loots = new ArrayList<>();
    private StackPane endPane = new StackPane();
    private boolean wonGame;


    @Override
    public void start(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Pick a map file");
        File f = fc.showOpenDialog(stage);
        if(f==null||!f.exists()){
            System.out.println("null file");
            return;
        }
        loadLevel(f.getPath());
        //TILES
        Tile.loadTileset("resources/tilesets/"+tilesetName+".png");
        tilePane.setPrefRows(9);
        tilePane.setPrefColumns(9);
        root.getChildren().add(tilePane);
        activeTiles = new Tile[9][9];
        for(int r = 0; r<9; r++){
            for(int c = 0; c<9; c++){
                Tile t = new Tile(0);
                tilePane.getChildren().add(t);
                activeTiles[r][c]=t;
                t.setSmooth(false);
            }
        }
        loadNewTiles();

        root.getChildren().add(gameObjectGroup);

        //MENU
        ImageView menu = new ImageView(Utility.loadImage("resources/menu.png"));
        root.getChildren().add(menu);
        menu.setX(512);

        enemiesLeftLabel.setTranslateX(SIZE*8);
        enemiesLeftLabel.setTranslateY(SIZE*3);
        enemiesLeftLabel.setFont(new Font("Baskerville",24));
        enemiesLeftLabel.setTextFill(Color.rgb(255,255,255));
        root.getChildren().add(enemiesLeftLabel);


        scoreLabel.setTranslateX(SIZE*9);
        scoreLabel.setTranslateY(SIZE);
        scoreLabel.setFont(new Font("Baskerville",48));
        scoreLabel.setTextFill(Color.rgb(255,255,255));
        root.getChildren().add(scoreLabel);

        //DEBUG
        root.getChildren().add(debugShape);

        //START APPLICATION
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.addEventHandler(KeyEvent.KEY_PRESSED,h);
        stage.addEventHandler(KeyEvent.KEY_RELEASED,h);

        lastTimestep=System.currentTimeMillis();
        AnimationTimer render = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
            }
        };
        render.start();
    }

    private void loadNewTiles(){
        //refreshes tiles
        for(int r =0; r<9; r++) {
            for (int c=0; c<9; c++) {
                try{
                    activeTiles[r][c].setTile(tilemap[r+(int)player.getY()-4][c+(int)player.getX()-4]);
                }
                catch(NullPointerException|ArrayIndexOutOfBoundsException e){
                    activeTiles[r][c].setTile(0);
                }
            }
        }
    }

    private void addGameObject(GameObject go){
        addNode(go.getImgView());
        gameObjects.add(go);
    }

    private void update(){
        if(timeGameEnded!=-1){
            if(!root.getChildren().contains(endPane)){
                root.getChildren().add(endPane);
                endPane.setTranslateY(WINDOW_HEIGHT/2);
                endPane.setPrefWidth(WINDOW_WIDTH);
                endPane.setPrefHeight(48);
                endPane.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0), CornerRadii.EMPTY, Insets.EMPTY)));
                endPane.setOpacity(0);
                if(!wonGame){
                    endPane.getChildren().add(gameOverLabel);
                    gameOverLabel.setTextFill(Color.rgb(150,0,0));
                    gameOverLabel.setFont(new Font("Baskerville",48));
                    gameOverLabel.setOpacity(0);
                }
                else{
                    endPane.getChildren().add(winLabel);
                    winLabel.setTextFill(Color.rgb(200,200,0));
                    winLabel.setFont(new Font("Baskerville",48));
                    winLabel.setOpacity(0);
                }
            }
            if(endPane.getOpacity()<1.0){
                endPane.setOpacity((System.currentTimeMillis()-timeGameEnded)/2000.0);
            }
            else if(!wonGame&&gameOverLabel.getOpacity()<1.0){
                gameOverLabel.setOpacity((System.currentTimeMillis()-(timeGameEnded+2000))/2000.0);
            }
            else if(wonGame&&winLabel.getOpacity()<1.0){
                winLabel.setOpacity((System.currentTimeMillis()-(timeGameEnded+2000))/2000.0);
            }

        }
        //debug();
        updateGameObjects();
        draw();
        inputs();
    }

    private void debug(){
        for(GameObject gc : gameObjects){
            if(gc instanceof Zombie){
                for(int i = 0; i<root.getChildren().size(); i++){
                    if(root.getChildren().get(i)==debugShape){
                        if(((Zombie)gc).getAttacks().size()>0){
                            root.getChildren().remove(i);
                            debugShape = ((Zombie)gc).getAttacks().get(0).getCollision();
                            debugShape.setTranslateX(gc.getImgView().getX());
                            debugShape.setTranslateY(gc.getImgView().getY());
                            debugShape.setScaleX(debugShape.getScaleX()*SIZE);
                            debugShape.setScaleY(debugShape.getScaleY()*SIZE);
                            //debugShape.setOpacity(0.5);

                            root.getChildren().add(debugShape);
                        }
                        break;
                    }
                }
            }
        }

    }

    private void updateGameObjects(){
        int numOfSteps = (int)((System.currentTimeMillis()-lastTimestep)/((1/(double)stepsPerSecond)*1000));
        for(int i = 0; i<numOfSteps; i++){
            stepCounter++;
            player.collisionResolve(this,false,player.getRollSteps()>0);
            if(lastInputs[4]&&player.getRollSteps()<=0&&MovingGameObject.keysToDegrees(lastInputs)!=-1){
                player.roll(MovingGameObject.keysToDegrees(lastInputs),this);
                if(MovingGameObject.keysToDegrees(lastInputs)==90) { //down
                    player.playAnimation(7,0);
                }
                else if(MovingGameObject.keysToDegrees(lastInputs)==270) { //up
                    player.playAnimation(8,0);
                }
                else if(MovingGameObject.keysToDegrees(lastInputs)<90||MovingGameObject.keysToDegrees(lastInputs)>270) { //right
                    player.playAnimation(5,0);
                }
                else if(MovingGameObject.keysToDegrees(lastInputs)>90&&MovingGameObject.keysToDegrees(lastInputs)<270) { //left
                    player.playAnimation(6,0);
                }
            }
            player.move(MovingGameObject.keysToDegrees(lastInputs), this);
            for(int enemy = 1; enemy<gameObjects.size(); enemy++){
                if(gameObjects.get(enemy) instanceof Enemy){
                    ((Enemy) gameObjects.get(enemy)).collisionResolve(this);
                    ((Enemy)gameObjects.get(enemy)).chasePlayer(this);
                    ((Enemy)gameObjects.get(enemy)).attack(this);
                    if(player.getHealth()<=0&&timeGameEnded==-1){
                        endGame(false);
                    }
                }
            }
            if(player.getSword().isActive()){
                if((lastInputs[0]||lastInputs[1]||lastInputs[2]||lastInputs[3])){
                    if(MovingGameObject.keysToDegrees(lastInputs)>=0){
                        if(lastInputs[7]){
                            player.getSword().addTarget(MovingGameObject.keysToDegrees(lastInputs),this);
                        }
                        else if(lastInputs[8]){
                            player.getSword().addTarget(MovingGameObject.keysToDegrees(lastInputs)+180,this);
                        }
                    }
                }
                player.getSword().update();
            }
            slice();
        }
        checkIfPlaying();
        lastTimestep+=numOfSteps*(1/(double)stepsPerSecond)*1000;
    }

    private void draw(){
        //Frame unlocked rendering stuff:
        if ((player.getLastVelX() != 0 || player.getLastVelY() != 0)&&player.getRollSteps()<=0) {
            double angle = MovingGameObject.keysToDegrees(lastInputs);
            if(angle==90&&player.getAnimation()!=1) { //down
                player.setAnimation(1);
            }
            else if(angle==270&&player.getAnimation()!=4) { //up
                player.setAnimation(4);
            }
            else if((angle<90||angle>270)&&player.getAnimation()!=2) { //right
                player.setAnimation(2);
            }
            else if(angle>90&&angle<270&&player.getAnimation()!=3) { //left
                player.setAnimation(3);
            }
        }
        else if(player.getLastVelX() == 0 && player.getLastVelY() == 0&&player.getAnimation()!=0){
            player.setAnimation(0);
        }
        loadNewTiles(); //TODO limit this method call to only when necessary
        tilePane.setTranslateX(-((player.getX()*SIZE)%SIZE));
        tilePane.setTranslateY(-((player.getY()*SIZE)%SIZE));
        for (GameObject obj : gameObjects) { // for all objects
            /*
            go through each gameobject and update every imageView they own
            internal list of imageview objects that currently exist in the game
            for each imageview if they are outside of the viewport but in the children remove them from the children
            if they are in the viewport but are not in the children, add to children
             */
            if (obj instanceof GameCharacter) {
                ((GameCharacter) obj).update(this);
            }
            obj.getImgView().setImage(obj.getImage());
            obj.getImgView().setX(((obj.getX() - player.getX()) * SIZE + SIZE * 4) - SIZE + SIZE / 2);
            obj.getImgView().setY(((obj.getY() - player.getY()) * SIZE + SIZE * 4) - SIZE + SIZE / 2);
        }
        for(Node node : nodes) {
            if(node.getBoundsInLocal().intersects(new BoundingBox(0,0,SIZE*9,SIZE*9))&&!gameObjectGroup.getChildren().contains(node)) {
                gameObjectGroup.getChildren().add(node);
            }
            else if(!node.getBoundsInLocal().intersects(new BoundingBox(0,0,SIZE*9,SIZE*9))&&gameObjectGroup.getChildren().contains(node)) {
                gameObjectGroup.getChildren().remove(node);
            }
        }
    }

    private void inputs(){
        //Store inputs for the next frame rendered and then start attacks
        lastInputs=h.getKeys();
        if(lastInputs[0]||lastInputs[1]||lastInputs[2]||lastInputs[3]){
            if(lastInputs[7]||lastInputs[8]){
                if((!player.getSword().isActive())&&player.getRollSteps()<=0){
                    if(MovingGameObject.keysToDegrees(lastInputs)>=0){
                        if(lastInputs[7]){
                            player.getSword().activate(MovingGameObject.keysToDegrees(lastInputs),this);
                        }
                        else{
                            player.getSword().activate(MovingGameObject.keysToDegrees(lastInputs)+180,this);
                        }
                    }
                }

                if(!player.getSword().isVisible()){
                    player.getSword().setVisible(true);
                    root.getChildren().add(player.getSword().getImage());
                }
            }
        }
        if(stepCounter-player.getSword().getLastStepActive()>=100){
            if(player.getSword().isVisible()){
                player.getSword().setVisible(false);
                root.getChildren().remove(player.getSword().getImage());
            }
            player.getSword().deactivate(this);
        }
    }

    private void endGame(boolean won){
        wonGame=won;
        if(!won){
            h.stop();
            for(Attack a : player.getAttacks()){
                removeNode(a.getImage());
            }
            for(Rectangle r : player.getHealthBars()){
                removeNode(r);
            }
            removeNode(player.getImgView());
            gameObjects.remove(player);
        }
        timeGameEnded=System.currentTimeMillis();
    }

    private void slice(){
        for(int i = 0; i<gameObjects.size(); i++) {
            if (gameObjects.get(i) == player) {
                continue;
            }
            if (Math.abs(gameObjects.get(i).getX() - player.getX()) <= 4 &&
                    Math.abs(gameObjects.get(i).getY() - player.getY()) <= 4) {
                if(Shape.intersect(player.getSword().getCollision(),gameObjects.get(i).getRect()).getBoundsInLocal().getWidth()!=-1&&player.getSword().isActive()&&
                    gameObjects.get(i) instanceof GameCharacter&&!((GameCharacter) gameObjects.get(i)).isAlreadyHit()){
                    GameCharacter gc = ((GameCharacter) gameObjects.get(i));
                    gc.setAlreadyHit(true);
                    gc.setHealth(gc.getHealth()-player.getSword().getDamage());
                    if(gc.getHealth()<=0){
                        putLoot(gc.getX(),gc.getY());
                        for(Attack a : ((GameCharacter) gameObjects.get(i)).getAttacks()){
                            removeNode(a.getImage());
                        }
                        for(Rectangle r : ((GameCharacter) gameObjects.get(i)).getHealthBars()){
                            removeNode(r);
                        }
                        removeNode(gameObjects.get(i).getImgView());
                        gameObjects.remove(i);
                        i--;
                        continue;
                    }
                }
                else if(gameObjects.get(i) instanceof GameCharacter&&(!player.getSword().isActive()||
                        (Shape.intersect(player.getSword().getCollision(),gameObjects.get(i).getRect()).getBoundsInLocal().getWidth()==-1
                        &&((GameCharacter) gameObjects.get(i)).isAlreadyHit()))){
                    ((GameCharacter) gameObjects.get(i)).setAlreadyHit(false);
                }
            }
        }

    }

    private void checkIfPlaying(){
        int enemyCount = 0;
        for(GameObject obj : gameObjects){
            if(obj instanceof Enemy){
                enemyCount++;
            }
        }
        enemiesLeftLabel.setText("Enemies Left: "+enemyCount);
        if(enemyCount==0&&timeGameEnded==-1){
            endGame(true);
        }
    }

    public void addNode(Node n){
        nodes.add(n);
    }

    public void removeNode(Node n){
        if(gameObjectGroup.getChildren().contains(n)){
            gameObjectGroup.getChildren().remove(n);
        }
        nodes.remove(n);
    }

    private int[][] random2DArrayOfInts(int width, int height, int range, int offset){
        int[][] map = new int[height][width];
        for(int r = 0; r<height; r++){
            for(int c = 0; c<width; c++){
                map[r][c]=(int)(Math.random()*(range+1)+offset);
            }
        }
        return map;
    }

    private void putLoot(double x, double y){
        Loot c = new Loot(x,y,(int)(Math.random()*10+1));
        c.getImgView().setViewOrder(2);
        loots.add(c);
        gameObjects.add(c);
        addNode(c.getImgView());
    }

    public Player getPlayer(){
        return player;
    }

    public GameObject[] getGameObjects(){
        GameObject[] objects = new GameObject[gameObjects.size()];
        for(int i = 0; i<gameObjects.size(); i++){
            objects[i]=gameObjects.get(i);
        }
        return objects;
    }

    public int[][] getTiles(){
        return tilemap;
    }

    public int getTileAt(int x, int y){
        try{
            return tilemap[y][x];
        }
        catch(IndexOutOfBoundsException | NullPointerException e){
            return 0;
        }
    }

    public ArrayList<Node> getNodes(){
        return nodes;
    }

    public boolean isLineSafe(Line l){
        double xInc = (l.getEndX()-l.getStartX())/5.0;
        double yInc = (l.getEndY()-l.getStartY())/5.0;
        double x=l.getStartX(),y=l.getStartY();
        for(int i = 0; i<5; i++){
            x+=xInc;
            y+=yInc;
            if(Tile.getTypes()[tilemap[(int)Math.floor(y)][(int)Math.floor(x)]].isFilled()){
                return false;
            }
        }

        return true;
    }

    public boolean isSafe(double x, double y, GameObject obj){
        return isSafe(x,y,obj,false,false);
    }
    public boolean isSafe(double x, double y, GameObject obj,boolean phaseTiles, boolean phaseObjects){
        return ((!isInTile(x,y)||phaseTiles)&&(!isInObject(obj)||phaseObjects));
    }
    public boolean isInTile(double x, double y){
        return Tile.getTypes()[getTileAt((int)Math.floor(x),(int)Math.floor(y))].isFilled();
    }

    private boolean isInObject(GameObject obj){
        boolean listening=false;
        for(int i = 0; i<gameObjects.size(); i++){
            if(gameObjects.get(i)==obj||!gameObjects.get(i).isSolid()){
                continue;
            }
            if(Math.abs(gameObjects.get(i).getX()-obj.getX())<=4&&Math.abs(gameObjects.get(i).getY()-obj.getY())<=4&&
                    obj.getBox().intersects(gameObjects.get(i).getBox())){
                    return true;
            }
        }
        return false;
    }

    private void loadLevel(String location){
        byte[] dst = new byte[Integer.BYTES];
        try{
            FileInputStream fis = new FileInputStream(location);
            fis.read(dst,0,Integer.BYTES);
            ByteBuffer buf = ByteBuffer.wrap(dst);
            int capacity = buf.getInt();
            dst = new byte[capacity];
            fis.read(dst);
            buf=ByteBuffer.wrap(dst);
            buf.position(0);
            int stringLength = buf.getChar();
            byte[] tilesetNameBytes = new byte[stringLength];
            buf.get(tilesetNameBytes,0,stringLength);
            tilesetName=new String(tilesetNameBytes);
            player = new Player(buf.getDouble(),buf.getDouble());
            addGameObject(player);
            int mapHeight=buf.getChar();
            int mapWidth=buf.getChar();
            tilemap=new int[mapHeight][mapWidth];
            for(int r = 0; r<mapHeight; r++){
                for(int c = 0; c<mapWidth; c++){
                    tilemap[r][c]=buf.get();
                }
            }
            int charCount = buf.getChar();
            for(int i = 0; i<charCount; i++){
                double x = buf.getDouble();
                double y = buf.getDouble();
                int value = buf.get();
                GameObject obj = GameCharacter.characters[value].getSupplier().get();
                obj.setX(x);
                obj.setY(y);
                addGameObject(obj);
            }
        }catch(IOException e){
            System.out.println("File not found or other I/O issue");
        }
    }

    public ArrayList<Loot> getLoot(){
        return loots;
    }

    public void setScore(int score){
        this.score=score;
    }

    public int getScore(){
        return score;
    }

    public void takeLoot(Loot l){
        score+=l.getValue();
        loots.remove(l);
        gameObjects.remove(l);
        removeNode(l.getImgView());
        scoreLabel.setText("$"+score);
    }

    public int getStepCounter(){
        return stepCounter;
    }

    private void setMapName(String mapName){
        this.mapName=mapName;
    }

    public static void main(String[] args){ launch();}
}
