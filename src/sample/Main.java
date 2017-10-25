package sample;

import com.sun.glass.ui.Screen;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Date;
import java.io.File;

public class Main extends Application {


    private static final int screenX = 550;
    private static final int screenY = 600;
    private static final int toolbarOffset = 75;
    private static String musicDir = "AnimalCrossingSoundtrack";
    private static Media song;
    private static MediaPlayer player;
    private static String curSong = null;
    private static MediaControl root;
    private static Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        Main.primaryStage = primaryStage;

        //Add timeline for updates every second
        update(primaryStage);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000),ae -> update(primaryStage)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();


        //Set Stage Location to bottom right
        primaryStage.setX(Screen.getMainScreen().getWidth() - screenX);
        primaryStage.setY(Screen.getMainScreen().getHeight() - screenY - toolbarOffset);

        //Show Scene
        updateScene();

    }

    public static void updateScene(){

        if(root.getScene() != null){
            return;
        }
        Scene myScene = new Scene(root,screenX,screenY);
        primaryStage.setScene(myScene);
        primaryStage.show();

    }

    public static void update(Stage stage){
        Date curDate = new Date();
        stage.setTitle("Animal Crossing Player - " + getMediaFromDate(null));
        playMedia(getMediaFromDate(null));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String getMediaFromDate(Date date){

        if(date == null){
            date = new Date();
        }

        int currentHour = date.getHours();

        String mediaName = null;

        if(currentHour == 0) {
            mediaName = "Midnight";
        }else if(currentHour == 12){
            mediaName = "Noon";
        }else if(currentHour > 12){
                mediaName = Integer.toString(currentHour - 12) + " PM";
        }else{
                mediaName = Integer.toString(currentHour) + " AM";
        }

        return mediaName;

    }

    public static void playMedia(String mediaName){

        File musDir = new File(musicDir);

        if(!musDir.isDirectory()){
            System.err.println("Bad Music Directory");
            System.exit(-1);
        }

        //Within the music directory find the first match for our song.
        String songPath = null;
        for(File f : musDir.listFiles()){
            if(f.isFile()){
                if(f.getName().contains(mediaName)){
                    songPath = f.getAbsolutePath();
                    break;
                }
            }
        }

        //If the song is the same, leave it alone
        if(curSong != null && curSong.equals(songPath)){
            System.out.println("Same song, do nothin...");
            return;
        }

        //Play song if not null and place on loop
        if(songPath != null){
            song = new Media( new File(songPath).toURI().toString());
            if(root != null){
                root.stop();
            }

            root = new MediaControl(new MediaPlayer(song));
            root.play();
            root.setRepeat(true);
            curSong = songPath;
            updateScene();

        }else{
            System.err.println("Path was null");
        }
    }
}
