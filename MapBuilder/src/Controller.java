import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Controller {

    public final static int DEFAULT_MAP_WIDTH=30,DEFAULT_MAP_HEIGHT=30,SIZE=32;
    private int tilePainter = 0, charPainter=1, mapWidth=DEFAULT_MAP_WIDTH, mapHeight=DEFAULT_MAP_HEIGHT, placing;
    private int[][] tilemap = new int[DEFAULT_MAP_HEIGHT][DEFAULT_MAP_WIDTH];
    private double playerPosX=0,playerPosY=0;
    private ImageView playerImage;
    private ArrayList<PositionAndValue> characters= new ArrayList<>();
    private Stage s;
    private File existingFile;
    private String tilesetName="ruins";
    private boolean cancelled;

    @FXML
    private TextField mapWidthText;
    @FXML
    private TextField mapHeightText;
    @FXML
    private ChoiceBox tilesetChoiceBox;
    @FXML
    private ScrollPane propertiesPane;
    @FXML
    private ScrollPane mapScrollPane;
    @FXML
    private TilePane mapPane;
    @FXML
    private TilePane tilesPane;
    @FXML
    private ScrollPane tileScrollPane;
    @FXML
    private ScrollPane charScrollPane;
    @FXML
    private TilePane charPane;
    @FXML
    private AnchorPane mapAnchor;
    @FXML
    private Text statusText;
    @FXML
    private TabPane tabs;

    public void initialize() {
        Tile.loadTileset("resources/tilesets/"+tilesetName+".png");
        for(int i = 0; i<Tile.getTypes().length; i++){
            DataButton db = new DataButton(i);
            db.setGraphic(new ImageView(Tile.getTypes()[i].getImage()));
            db.setOnAction(event -> setTilePainter(db.getValue()));
            tilesPane.getChildren().add(db);
        }
        tileScrollPane.setFitToWidth(true);
        tileScrollPane.setFitToHeight(true);
        for(int i = 1; i<GameCharacter.characters.length; i++){
            DataButton db = new DataButton(i);
            db.setGraphic(new ImageView(GameCharacter.characters[i].getIcon()));
            db.setOnAction(event -> setCharPainter(db.getValue()));
            charPane.getChildren().add(db);
        }
        charScrollPane.setFitToWidth(true);
        charScrollPane.setFitToHeight(true);
/*
        properties panel:
            load tilesets to choicebox
         */
        propertiesPane.setFitToWidth(true);
        propertiesPane.setFitToHeight(true);

        fillWithVoid(tilemap);
        initializeMapPane();
        mapPane.setTranslateX(SIZE/2.0);
        mapPane.setTranslateY(SIZE/2.0);

        setTilePainter(0);
        System.out.println("controller initialized");
    }

    private void initializeMapPane(){
        mapPane.getChildren().clear();
        for(int r = 0; r<tilemap.length; r++){
            for(int c = 0; c<tilemap[0].length; c++){
                MapTile tileToAdd = new MapTile(c,r,tilemap[r][c]);
                tileToAdd.getTransforms().add(new Scale((double)SIZE/(double)TileGame.SIZE,(double)SIZE/(double)TileGame.SIZE));
                MapTileHandler mth = new MapTileHandler(tileToAdd,this);
                tileToAdd.setOnMousePressed(mth);
                tileToAdd.setOnDragDetected(mth);
                tileToAdd.setOnMouseDragEntered(mth);
                mapPane.getChildren().add(tileToAdd);
            }
        }
        mapPane.setPrefColumns(mapWidth);
        mapPane.setPrefRows(mapHeight);
        mapPane.setPrefTileWidth(SIZE);
        mapPane.setPrefTileHeight(SIZE);
        mapAnchor.setMinHeight(tilemap.length*SIZE);
        mapAnchor.setMinWidth(tilemap[0].length*SIZE);
        mapAnchor.setOnMouseClicked(new MapAnchorHandler(mapAnchor,this));
    }

    private void fillWithVoid(int[][] tiles){
        for(int r = 0; r<tilemap.length; r++) {
            for (int c = 0; c < tilemap[0].length; c++) {
                tiles[r][c]=0;
            }
        }
    }

    public void addChar(double x, double y, int whichChar){
        PositionAndValue pav = new PositionAndValue(x,y,whichChar);
        PlaceableImage di = new PlaceableImage(GameCharacter.characters[whichChar].getIcon(),pav,this);
        di.setLayoutX(x*SIZE-SIZE/2.0);
        di.setLayoutY(y*SIZE-SIZE/2.0);
        di.getTransforms().add(new Scale((double)SIZE/(double)TileGame.SIZE,(double)SIZE/(double)TileGame.SIZE));
        mapAnchor.getChildren().add(di);
        characters.add(di.getPAV());
    }

    public void removeChar(PlaceableImage di){
        mapAnchor.getChildren().remove(di);
        characters.remove(di.getPAV());
    }

    public void setPlayer(double x, double y){
        playerPosX=x;
        playerPosY=y;
        if(playerImage==null){
            ImageView iv = new ImageView(GameCharacter.characters[0].getIcon());
            iv.setLayoutX(x*SIZE-SIZE/2.0);
            iv.setLayoutY(y*SIZE-SIZE/2.0);
            iv.getTransforms().add(new Scale((double)SIZE/(double)TileGame.SIZE,(double)SIZE/(double)TileGame.SIZE));
            playerImage=iv;
            mapAnchor.getChildren().add(iv);
        }
        else{
            playerImage.setLayoutX(x*SIZE-SIZE/2.0);
            playerImage.setLayoutY(y*SIZE-SIZE/2.0);
        }
    }

    private void safeMapResize(int[][] tiles, int width, int height){
        int[][] output = new int[height][width];
        for(int r = 0; r<height; r++){
            for(int c = 0; c<width; c++){
                try{
                    output[r][c]=tiles[r][c];
                }
                catch(NullPointerException|ArrayIndexOutOfBoundsException e){
                    output[r][c]=0;
                }
            }
        }
        tilemap=output;
    }

    private void setTilePainter(int whichTile){
        statusText.setText("Currently placing tile \""+Tile.getTypes()[whichTile].getName()+"\"");
        placing=0;
        this.tilePainter=whichTile;
    }

    private void setCharPainter(int whichChar){
        statusText.setText("Currently placing character \""+GameCharacter.characters[whichChar].getName()+"\"");
        placing=1;
        this.charPainter=whichChar;
    }

    @FXML
    private void handleTilesetButton(ActionEvent event){
        tilesetName=(String)tilesetChoiceBox.getValue();
        initializeTileset();
    }

    private void initializeTileset(){
        Tile.loadTileset("resources/tilesets/"+tilesetName+".png");
    }

    @FXML
    private void handlePlacePlayer(ActionEvent event){
        statusText.setText("Currently placing player");
        placing=2;
    }

    @FXML
    private void handleSetMapWidth(ActionEvent event) {
        try{
            setMapWidth(Integer.parseInt(mapWidthText.getText()));
            safeMapResize(tilemap,mapWidth,mapHeight);
            initializeMapPane();
        }
        catch(NumberFormatException e){
            System.out.println("invalid map width input");
        }
    }

    @FXML
    private void handleSetMapHeight(ActionEvent event) {
        try{
            setMapHeight(Integer.parseInt(mapHeightText.getText()));
            safeMapResize(tilemap,mapWidth,mapHeight);
            initializeMapPane();
        }
        catch(NumberFormatException e){
            System.out.println("invalid map height input");
        }
    }

    public int getTilePainter(){
        return tilePainter;
    }

    public int getCharPainter(){
        return charPainter;
    }

    public void setMapWidth(int value){
        this.mapWidth=value;
    }

    public void setMapHeight(int value){
        this.mapHeight=value;
    }

    public int placingWhat(){
        return placing;
    }

    public void setStage(Stage s){
        this.s=s;
    }

    public void setTile(int x, int y, int value){
        tilemap[y][x]=value;
    }

    @FXML
    public void newFile(){
        cancelled = false;
        FileChooser fc = new FileChooser();
        File f = fc.showSaveDialog(s);
        if(f==null){
            System.out.println("null file");
            return;
        }
        if(f.exists()){
            new Alert(Alert.AlertType.CONFIRMATION,
                    "This operation will overwrite a file of the same name.",
                    ButtonType.OK,
                    ButtonType.CANCEL).showAndWait().ifPresent(response -> {
                if (response == ButtonType.CANCEL) {
                    cancel();
                }
            });
        }
        try{
            f.createNewFile();
            fillWithVoid(tilemap);
            initializeMapPane();
            mapAnchor.getChildren().clear();
            mapAnchor.getChildren().add(mapPane);
            characters.clear();
            playerPosX=0;
            playerPosY=0;
            setTilePainter(0);
        }catch(IOException e){
            System.out.println("file could not be created.");
        }
        existingFile=f;
    }

    @FXML
    public void save(){
        //if there is already a known file to save to, overwrite the file, otherwise, call save as
        if(existingFile==null){
            saveAs();
        }
        else{
            saveToDisk(existingFile);
        }
    }

    @FXML
    public void saveAs(){
        //save the file to a new location
        FileChooser fc = new FileChooser();
        File saveTo = fc.showSaveDialog(s);
        cancelled=false;
        if(saveTo==null){
            return;
        }
        if(saveTo.exists()){
            new Alert(Alert.AlertType.CONFIRMATION,
                    "This operation will overwrite a file of the same name.",
                    ButtonType.OK,
                    ButtonType.CANCEL).showAndWait().ifPresent(response -> {
                if (response == ButtonType.CANCEL) {
                    cancel();
                }
            });
        }
        if(cancelled){
            return;
        }
        saveToDisk(saveTo);
        existingFile=saveTo;
    }

    private void saveToDisk(File f){
        int capacity =
                Integer.BYTES+ //buffer size to allocate (removed later, but kept here for visualization)
                Character.BYTES+ //tileset string length in bytes
                tilesetName.getBytes().length+ //tileset string
                2*Double.BYTES+ //player pos x, y
                Character.BYTES*2+ //rows and columns
                mapWidth*mapHeight+ //one byte for each tile
                Character.BYTES+ //gameobject count **OBJECTS ARE CHUNKED TOGETHER, (BYTE+DOUBLE+DOUBLE)*CHARACTER COUNT**
                (2*Double.BYTES+1)*characters.size(); //one byte for each type plus two doubles for each object for its position
        byte[] dst = new byte[capacity];
        ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.putInt(capacity-Integer.BYTES); //capacity does not count towards read buffer because its already dealt with once read
        buf.putChar((char)tilesetName.getBytes().length);
        buf.put(tilesetName.getBytes());
        buf.putDouble(playerPosX);
        buf.putDouble(playerPosY);
        buf.putChar((char)mapHeight);
        buf.putChar((char)mapWidth);
        for(int r = 0; r<mapHeight; r++){
            for(int c = 0; c<mapWidth; c++){
                buf.put((byte)tilemap[r][c]);
            }
        }
        buf.putChar((char)characters.size());
        for(int i = 0; i<characters.size(); i++){
            buf.putDouble(characters.get(i).getX());
            buf.putDouble(characters.get(i).getY());
            buf.put((byte)characters.get(i).getValue());
        }
        buf.position(0);
        buf.get(dst);
        try{
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(dst);
        }catch(IOException e){
            System.out.println("File not found or other I/O issue");
        }
    }

    @FXML
    public void open(){
        //select a file and open in editor
        cancelled=false;
        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(s);
        if(f==null||!f.exists()){
            System.out.println("null file");
            return;
        }
        byte[] dst = new byte[Integer.BYTES];
        try{
            FileInputStream fis = new FileInputStream(f);
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
            initializeTileset();
            playerPosX=buf.getDouble();
            playerPosY=buf.getDouble();
            setPlayer(playerPosX,playerPosY);
            mapHeight=buf.getChar();
            mapWidth=buf.getChar();
            tilemap=new int[mapHeight][mapWidth];
            for(int r = 0; r<mapHeight; r++){
                for(int c = 0; c<mapWidth; c++){
                    tilemap[r][c]=buf.get();
                }
            }
            int charCount = buf.getChar();
            for(int i = 0; i<charCount; i++){
                addChar(buf.getDouble(),buf.getDouble(),buf.get());
            }
            initializeMapPane();

        }catch(IOException e){
            System.out.println("File not found or other I/O issue");
        }
        existingFile=f;
    }

    @FXML
    public void quit(){
        cancelled = false;
        new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to quit? You will lose all unsaved data.",
                ButtonType.OK,
                ButtonType.CANCEL).showAndWait().ifPresent(response -> {
            if (response == ButtonType.CANCEL) {
                cancel();
            }
        });
        System.exit(0);
    }

    private void cancel(){
        cancelled=true;
    }

    @FXML
    public void about(){

    }
}