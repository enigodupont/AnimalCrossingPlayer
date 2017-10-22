package sample;

import com.sun.glass.ui.Screen;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Date;
import java.io.File;

public class Main extends Application {


    private final int screenX = 350;
    private final int screenY = 200;
    private int toolbarOffset = 75;
    private static String musicDir = ".\\AnimalCrossingSoundtrack";
    private static Media song;
    private static MediaPlayer player;
    private static String curSong = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //Set Stage Location to bottom right
        primaryStage.setX(Screen.getMainScreen().getWidth() - screenX);
        primaryStage.setY(Screen.getMainScreen().getHeight() - screenY - toolbarOffset);

        //Show Scene
        Scene myScene = new Scene(root,screenX,screenY);
        primaryStage.setScene(myScene);
        primaryStage.show();

        //Add timeline for updates every second
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000),ae -> update(primaryStage)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
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
            if(player != null){
                player.stop();
            }
            player = new MediaPlayer(song);
            player.play();
            player.setCycleCount(MediaPlayer.INDEFINITE);
            curSong = songPath;

        }else{
            System.err.println("Path was null");
        }
    }
}
