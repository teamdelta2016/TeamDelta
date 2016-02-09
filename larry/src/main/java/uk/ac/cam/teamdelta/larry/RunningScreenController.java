package uk.ac.cam.teamdelta.larry;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static uk.ac.cam.teamdelta.Logger.debug;

public class RunningScreenController implements ScreenController {

    @FXML
    Button button;
    @FXML
    ImageView view;
    @FXML
    Label locationText;
    private ScreensContainer container;
    private Map<Integer, OtherRunningScreenController> otherControllers = new HashMap<>(Screen.getScreens().size());
    private Stage otherStage;
    private Engine engine;
    private Frame currentFrame;
    /**
     * Boolean to keep track of whether to show front or rear windscreen view
     */
    private boolean lookingForward = true;
    /**
     * Event handler for detected the UP arrow key. Use of timer only allows event firing every {@link
     * Main#KEY_HOLD_DELAY}
     */
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
    /**
     * Event handler for switching views using the spacebar, and looking left and right
     */
    final EventHandler<KeyEvent> switchViewHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode().equals(KeyCode.SPACE)) {
                switchView();
            } else if (event.getCode().equals(KeyCode.RIGHT)) {
                lookRight();
            } else if (event.getCode().equals(KeyCode.LEFT)) {
                lookLeft();
            }
        }
    };

    @Override
    public void setupScreen() {
        // TODO: Remove this
        // For testing, set the image to a default one
        view.setImage(new Image("/uk.ac.cam.teamdelta.larry/images/test1joinresult.jpg"));

        // layout hack
        view.setFitHeight(Main.GAME_HEIGHT / 1.4);
        //TODO: Constructor may change
        engine = new Engine(LarrySettings.getInstance().getLocation());
        locationText.setText("Current Location:\n" + LarrySettings.getInstance().getLocation().getLatitude() + ", " +
                LarrySettings.getInstance().getLocation().getLongitude());

        // code to create second stage on other monitor if it exists
        if (Screen.getScreens().size() > 1) {
            makeOtherScreen(1);
        }
        container.getScene().addEventHandler(KeyEvent.KEY_PRESSED, nextFrameHandler);
        container.getScene().addEventHandler(KeyEvent.KEY_PRESSED, switchViewHandler);
    }

    /**
     * Event fired when user clicks 'quit' button.
     *
     * @param event The received event object from Java FX runtime
     * @throws IOException
     */
    @FXML
    private void quitGame(ActionEvent event) throws IOException {
        // cleanup
        if (otherStage != null) {
            otherStage.close();
        }
        engine.stop();
        container.nextScreen();

        container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, nextFrameHandler);
        container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, switchViewHandler);
    }

    /**
     * Called when UP arrow is pressed, requests the next frame from the {@link Engine}
     */
    private void goToNextFrame() {
        currentFrame = engine.nextFrame(null);
        // set images in various places from frame
        if (lookingForward) {
            debug("Requested next frame");
        }
    }

    /**
     * Called when SPACEBAR is pressed
     */
    private void switchView() {
        lookingForward = !lookingForward;
        if (lookingForward) {
            view.setImage(new Image("/uk.ac.cam.teamdelta.larry/images/test1joinresult.jpg"));
            if (otherControllers.size() > 1) {
                otherControllers.get(1).setImage(new Image("/uk.ac.cam.teamdelta.larry/images/test1out.jpg"));
            }
            debug("Now looking forwards");
        } else {
            view.setImage(new Image("/uk.ac.cam.teamdelta.larry/images/car.png"));
            if (otherControllers.size() > 1) {
                otherControllers.get(1).setImage(new Image("/uk.ac.cam.teamdelta.larry/images/car.png"));
            }
            debug("Now looking backwards");
        }
    }

    private void lookLeft() {
        debug("looking left");
        if (otherControllers.size() > 1) {
            otherControllers.get(1).setImage(new Image("/uk.ac.cam.teamdelta.larry/images/test1joinresult.jpg"));
        }
    }

    private void lookRight() {
        debug("looking right");
        if (otherControllers.size() > 1) {
            otherControllers.get(1).setImage(new Image("/uk.ac.cam.teamdelta.larry/images/loadingwheel.png"));
        }
    }

    private void makeOtherScreen(int index) {
        Screen otherScreen = Screen.getScreens().get(index);
        otherStage = new Stage();
        try {
            FXMLLoader f = new FXMLLoader(getClass().getResource(Main.RUNNING_OTHER_SCREEN_FXML));
            Pane pane = f.load();
            //TODO: make this work with 3 screens
            OtherRunningScreenController controller = f.getController();
            controller.setupScreen();
            controller.setScreenIndex(index);
            otherControllers.put(index, controller);
            Scene scene = new Scene(pane);
            otherStage.setScene(scene);
            otherStage.initStyle(StageStyle.TRANSPARENT);
            otherStage.setX(otherScreen.getVisualBounds().getMinX());
            otherStage.setY(otherScreen.getVisualBounds().getMinY());
            otherStage.setWidth(otherScreen.getVisualBounds().getWidth());
            otherStage.setHeight(otherScreen.getVisualBounds().getHeight());
            otherStage.initOwner(container.getScene().getWindow());
            otherStage.setFullScreenExitHint("");
            otherStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            otherStage.show();
            // shift focus back to main window
            final Stage primaryStage = (Stage) container.getScene().getWindow();
            primaryStage.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

}
