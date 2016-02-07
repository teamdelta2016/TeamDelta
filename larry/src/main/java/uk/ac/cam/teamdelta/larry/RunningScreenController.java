package uk.ac.cam.teamdelta.larry;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uk.ac.cam.teamdelta.robert.Engine;
import uk.ac.cam.teamdelta.robert.Frame;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static uk.ac.cam.teamdelta.Logger.debug;

public class RunningScreenController implements ScreenController {

    private ScreensContainer container;

    private Stage otherStage;

    private Engine engine;

    private boolean lookingForward = true;

    private final EventHandler<KeyEvent> nextFrameHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode().equals(KeyCode.UP)) {
                goToNextFrame();
                final EventHandler<KeyEvent> ev = this;
                container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, ev);
                new Timer(true).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        container.getScene().addEventHandler(KeyEvent.KEY_PRESSED, ev);
                    }
                }, Main.KEY_HOLD_DELAY);
            }
        }
    };

    final EventHandler<KeyEvent> switchViewHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode().equals(KeyCode.SPACE)) {
                switchView();
            }
        }
    };


    @FXML
    Button button;

    @FXML
    ImageView view;

    @Override
    public void setupScreen() {
        // TODO: Remove this
        // For testing, set the image to a default one
        view.setImage(new Image("/uk.ac.cam.teamdelta.larry/images/test1joinresult.jpg"));

        engine = new Engine(LarrySettings.getInstance().getLocation());
        // show some data that was passed through the UI chain
        button.setText(LarrySettings.getInstance().getParameters().getString());
        container.getScene().getWindow().requestFocus();
        // code to create second stage on other monitor if it exists
        if (Screen.getScreens().size() > 1) {

            Screen otherScreen = Screen.getScreens().get(1);
            otherStage = new Stage();
            try {
                Pane pane = FXMLLoader.load(getClass().getResource(Main.RUNNING_OTHER_SCREEN_FXML));
                Scene scene = new Scene(pane);
                otherStage.setScene(scene);
                otherStage.setX(otherScreen.getVisualBounds().getMinX());
                otherStage.setY(otherScreen.getVisualBounds().getMinY());
                otherStage.setWidth(otherScreen.getVisualBounds().getWidth());
                otherStage.setHeight(otherScreen.getVisualBounds().getHeight());
                otherStage.initStyle(StageStyle.UNDECORATED);
                otherStage.initOwner(container.getScene().getWindow());
                otherStage.setFullScreenExitHint("");
                otherStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
                otherStage.show();
                // shift focus back to main window
                final Stage primaryStage = (Stage) container.getScene().getWindow();
                primaryStage.requestFocus();
                // only allow key events every KEY_HOLD_DELAY ms

                container.getScene().addEventHandler(KeyEvent.KEY_PRESSED, nextFrameHandler);
                container.getScene().addEventHandler(KeyEvent.KEY_PRESSED, switchViewHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void quitGame(ActionEvent event) throws IOException {
        // cleanup
        if (otherStage != null) {
            otherStage.close();
        }
        engine.stop();
        container.nextScreen();

    }

    private void goToNextFrame() {
        Frame f = engine.nextFrame(LarrySettings.getInstance().getLocation());
        // set images in various places from frame
        if (lookingForward) {
            debug("Requested next frame");
        }
    }

    private void switchView() {
        lookingForward = !lookingForward;
        if (lookingForward) {
            view.setImage(new Image("/uk.ac.cam.teamdelta.larry/images/test1joinresult.jpg"));
            debug("Now looking forwards");
        } else {
            view.setImage(new Image(String.valueOf(getClass()
                    .getResource("/uk.ac.cam.teamdelta.larry/images/car.png"))));
            debug("Now looking backwards");
        }
    }

    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

}
