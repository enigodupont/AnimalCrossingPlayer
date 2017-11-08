package main;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.io.File;
import java.util.Random;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main extends Application {


    private final int screenX = 650;
    private final int screenY = 600;
    private final int toolbarOffset = 75;
    private final double randomChance = 3.5;
    private String musicDir = "AnimalCrossingSoundtrack";
    private String randomSongs[] = {"Roost","Museum - Entrance","Nooks Cranny","Town Gate"};
    private Boolean Snow = false;
    private Boolean Rain = false;
    private Boolean specialSong = false;
    private Boolean specialSongPlaying = false;
    private Media song;
    private String curSong = null;
    private String curSongTitle = null;
    private String previousSongTitle = null;
    private MediaControl root;
    private Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;

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

    private void updateScene(){

        if(root.getScene() != null){
            return;
        }
        Scene myScene = new Scene(root,screenX,screenY);
        primaryStage.setScene(myScene);
        primaryStage.show();

    }

    private void update(Stage stage){
        playMedia(getMediaFromDate(null));
        stage.setTitle("Animal Crossing Player - " + curSongTitle);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private String getMediaFromDate(Date date){

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

        if((Math.random() * 100) < randomChance){
            System.out.println("Random SONG ");
            mediaName = randomSongs[(new Random()).nextInt(randomSongs.length)];
            specialSong = true;
        }

        return mediaName;

    }

    private void playMedia(String mediaName){

        if(specialSong && specialSongPlaying && root != null && root.isStopped()){
            specialSong = specialSongPlaying = false;
        }else if(specialSong && specialSongPlaying && root != null && !root.isStopped()){
            return;
        }

        File musDir = new File(musicDir);

        if(!musDir.isDirectory()){
            System.err.println("Bad Music Directory");
            System.exit(-1);
        }


        //Within the music directory find the first match for our song.
        String songPath = null;
        File lastFile = new File("Nothing");
        for(File f : musDir.listFiles()){
            if(f.isFile()){
                if(f.getName().contains(mediaName)){
                    //Base name off of
                    if(Rain && f.getName().contains("Rain")){
                        songPath = f.getAbsolutePath();
                    }else if(Snow && f.getName().contains("Snow")){
                        songPath = f.getAbsolutePath();
                    }else if(!Rain && !Snow && ! f.getName().contains("Rain") && ! f.getName().contains("Snow")){
                        songPath = f.getAbsolutePath();
                    }

                    if(songPath != null){
                        lastFile = f;
                        previousSongTitle = curSongTitle;
                        curSongTitle = lastFile.getName();
                        break;
                    }
                }
            }
        }

        //If the song is the same, leave it alone
        if(curSong != null && curSong.equals(songPath)){
            System.out.println("Same song " + lastFile.getName() + ", do nothin...");
            return;
        }

        if(previousSongTitle != null) System.out.println("Changing song from " + previousSongTitle + ", to " + curSongTitle);

        //Play song if not null and place on loop
        if(songPath != null){
            song = new Media( new File(songPath).toURI().toString());
            if(root != null){
                root.stop();
            }

            root = new MediaControl(new MediaPlayer(song),this);
            root.play();
            if(!specialSong){
                root.setRepeat(true);
            }else{
                specialSongPlaying = true;
            }
            curSong = songPath;
            updateScene();

        }else{
            System.err.println("Path was null");
        }
    }

    public void setRain(Boolean b){
        Rain = b;
    }

    public void setSnow(Boolean b){
        Snow = b;
    }

    public Boolean getRain(){
        return Rain;
    }

    public Boolean getSnow(){
        return Snow;
    }
}
