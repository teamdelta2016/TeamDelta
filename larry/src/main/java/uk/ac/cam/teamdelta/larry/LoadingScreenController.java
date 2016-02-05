package uk.ac.cam.teamdelta.larry;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class LoadingScreenController implements Initializable, ScreenController {

    ScreensContainer container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void setupScreen() {
        // wait x seconds before continuing to next screen
        //TODO: don't wait arbitrary time, get notified?
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Started sleeping");
                Thread.sleep(5000);
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                container.nextScreen();
            }
        });
        new Thread(sleeper).start();

    }

}
