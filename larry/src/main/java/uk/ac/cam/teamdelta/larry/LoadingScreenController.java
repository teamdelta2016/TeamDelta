package uk.ac.cam.teamdelta.larry;

import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import static uk.ac.cam.teamdelta.Logger.debug;

public class LoadingScreenController implements ScreenController {

    ScreensContainer container;

    @FXML
    ImageView img;

    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void setupScreen() {
        // set size of rotating wheel image
        img.setFitHeight(Main.GAME_HEIGHT / 2);
        img.setFitWidth(Main.GAME_WIDTH / 2);
        // spin image round for a few seconds
        final RotateTransition rt = new RotateTransition(Duration.millis(5000), img);
        // wait x seconds before continuing to next screen
        //TODO: don't wait arbitrary time, get notified?
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                debug("Started sleeping for loading");
                Thread.sleep(5000);
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                debug("Finished sleeping");
                rt.stop();
                container.nextScreen();
            }
        });
        new Thread(sleeper).start();
        rt.setCycleCount(10);
        rt.setByAngle(720);
        rt.play();

    }

}