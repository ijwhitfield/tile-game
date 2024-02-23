import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class Controller {

    /*
    application use example:
        application starts, you are required to pick a file to load a tileset from
        tiles are loaded into memory and blank tiletype objects are made
        the tile images are shown to you one by one, selectable individually with the right scrollbar
            the right scrollbar, by the way, shows names of all tiletypes, and so initially only says void
        when a tiletype is selected, the properties for that tiletype are shown on the left, and when you edit them
            and press the save button, the changes are collected and saved to memory
        then, when you are done, the save function saves all metadata to a TILESETNAME.meta file
        when tilegame loads tiletypes into memory with the loadtileset function, it reads the meta file for info.
        note that tiletype images are not saved, but are ordered so that when they are reopened the images will line up.

        save:
        char str len
        str
        char mat
        byte boolean
     */

    private File existingFile;
    private boolean cancelled = false;
    private TileType[] tileTypes;
    private int currentlySelected=0;
    ObservableList<String> tileStrings;

    private Stage s;
    @FXML
    private ListView<String> tileList;
    @FXML
    private ImageView tileImage;
    @FXML
    private TextField nameField;
    @FXML
    private TextField materialField;
    @FXML
    private CheckBox filledBox;

    public void initialize(){
        tileList.setOnMouseClicked((MouseEvent e) -> setSelected(tileList.getFocusModel().getFocusedIndex()));
        System.out.println("controller initialized");
    }

    public void setStage(Stage s){
        this.s=s;
    }

    private void updateUI(){
        //populate listView with names of each tiletype
        tileList.getItems().clear();
        for(int i = 0; i<tileTypes.length; i++){
            tileList.getItems().add(tileTypes[i].getName());
        }

    }

    private void setSelected(int selected){
        this.currentlySelected=selected;
        tileImage.setImage(tileTypes[selected].getImage());
        nameField.setText(tileTypes[selected].getName());
        materialField.setText(Integer.toString(tileTypes[selected].getMaterial()));
        filledBox.setSelected(tileTypes[selected].isFilled());
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
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Meta Files", "*.meta"));
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
        int capacity=0;
        for(int i = 0; i<tileTypes.length; i++){
            capacity+=Character.BYTES*2+tileTypes[i].getName().getBytes().length+1;
        }
        byte[] dst = new byte[capacity];
        ByteBuffer buf = ByteBuffer.allocate(capacity+Integer.BYTES);
        buf.putInt(capacity); //capacity does not count towards read buffer because its already dealt with once read
        for(int i = 0; i<tileTypes.length; i++){
            buf.putChar((char)tileTypes[i].getName().getBytes().length); //string length
            buf.put(tileTypes[i].getName().getBytes()); //string
            buf.putChar((char)tileTypes[i].getMaterial()); //material
            buf.put((byte)(tileTypes[i].isFilled()?1:0)); //filled or not
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
        //select a meta file and import
        cancelled=false;
        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(s);
        if(f==null||!f.exists()){
            System.out.println("null file");
            return;
        }
        Tile.loadTileset(f.getPath().substring(0,f.getPath().lastIndexOf('.'))+".png");
        tileTypes=Tile.getTypes();
        for(int i = 0; i<tileTypes.length; i++){
            tileTypes[i].setImage(Tile.getTypes()[i].getImage());
        }
        readMeta(f);
    }

    private void readMeta(File f){
        //read meta file and import into editor
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
            //read each tiletype and place in memory
            for(int i = 0; i<tileTypes.length;i++){
                int stringLength = buf.getChar();
                byte[] stringBytes = new byte[stringLength];
                buf.get(stringBytes,0,stringLength);
                tileTypes[i].setName(new String(stringBytes));
                tileTypes[i].setMaterial(buf.getChar());
                tileTypes[i].setFilled(buf.get()==(byte)1);
            }
        }catch(IOException e){
            System.out.println("File not found or other I/O issue");
        }
        updateUI();
        existingFile=f;
    }

    @FXML
    private void newFile(){
        //select a png file and load temporary types into memory using the image
        cancelled=false;
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File f = fc.showOpenDialog(s);
        if(f==null||!f.exists()){
            System.out.println("null file");
            return;
        }
        Tile.loadTileset(f.getPath());
        tileTypes=Tile.getTypes();
        updateUI();
    }

    @FXML
    private void quit(){

    }

    @FXML
    private void about(){

    }

    @FXML
    private void changeMetaData(){
        tileTypes[currentlySelected].setName(nameField.getText());
        tileTypes[currentlySelected].setMaterial(Integer.parseInt(materialField.getText()));
        tileTypes[currentlySelected].setFilled(filledBox.isSelected());
        updateUI();
    }

    private void cancel(){
        cancelled=true;
    }
}
