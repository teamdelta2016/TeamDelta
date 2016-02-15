package uk.ac.cam.teamdelta.larry;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import uk.ac.cam.teamdelta.Direction;
import uk.ac.cam.teamdelta.ImageOutputSet;
import uk.ac.cam.teamdelta.JunctionInfo;
import uk.ac.cam.teamdelta.robert.Engine;
import uk.ac.cam.teamdelta.robert.Frame;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static uk.ac.cam.teamdelta.Logger.debug;
import static uk.ac.cam.teamdelta.Logger.error;

public class RunningScreenController implements ScreenController {

    private static LarrySettings larrySettings = LarrySettings.getInstance();
    @FXML
    Button button;
    @FXML
    ImageView frontView, leftView, rightView;
    @FXML
    Label locationText;
    @FXML
    StackPane stackPane;
    private ScreensContainer container;
    private ImageOutputSet images;
    private WritableImage front;
    private WritableImage back;
    private WritableImage left;
    private WritableImage right;
    private GridPane menuPopup;
    private boolean menuIsShowing = false;
    /**
     * Boolean to keep track of whether to show front or rear windscreen view
     */
    private boolean lookingForward = true;
    /**
     * Event handler for detected the UP arrow key. Use of timer only allows event firing every {@link
     * Main#KEY_HOLD_DELAY}
     */
    private EventHandler<KeyEvent> nextFrameHandler;

    /**
     * Event handler for switching views using the spacebar, and looking left and right
     */
    private EventHandler<KeyEvent> switchViewHandler;

    private NextFrameService nextFrameService = new NextFrameService();

    @Override
    public void showScreen() {
        menuIsShowing = false;
        // code to create second stage on other monitor if it exists
        if (Screen.getScreens().size() > 1) {
            //makeOtherScreen(1);
        } else {
            double width = 1280;
            double height = 740;
            frontView.setEffect(new PerspectiveTransform(0, 0, width, 0,
                    width - 200, height, 200, height));
            leftView.setEffect(new PerspectiveTransform(-300, 0, 300, 0,
                    500, height, -300, 1280));
            rightView.setEffect(new PerspectiveTransform(340, 0, 900, 0,
                    940, 1280, 140, height));
        }
        container.getScene().addEventHandler(KeyEvent.KEY_PRESSED, nextFrameHandler);
        container.getScene().addEventHandler(KeyEvent.KEY_PRESSED, switchViewHandler);


        BorderPane p = (BorderPane) menuPopup.getChildren().get(0);
        GridPane g = (GridPane) p.getChildren().get(0);
        Button changeLocBtn = (Button) g.getChildren().get(0);
        Button changeParamsBtn = (Button) g.getChildren().get(1);
        Button quitBtn = (Button) g.getChildren().get(2);
        changeLocBtn.addEventHandler(ActionEvent.ANY, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                debug("Change loc");
                changeLocation();
            }
        });
        changeParamsBtn.addEventHandler(ActionEvent.ANY, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                debug("Change params");
                changeParameters();
            }
        });
        quitBtn.addEventHandler(ActionEvent.ANY, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                debug("quit");
                quitGame();
            }
        });

    }

    @Override
    public void setupScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Main.MENU_FXML));
            menuPopup = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // bind the value of the location text to the value which gets updated when
        // location changes in LarrySettings
        locationText.textProperty().bind(larrySettings.getStringLocation());
        nextFrameHandler = new EventHandler<KeyEvent>() {
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

        switchViewHandler = new EventHandler<KeyEvent>() {
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
        stackPane.getChildren().add(menuPopup);
    }

    private void changeParameters() {
        larrySettings.getEngine().stop();
        container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, nextFrameHandler);
        container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, switchViewHandler);
        container.putOutOfOrderScreen(Main.PARAMETERS_SCREEN);
        container.putOutOfOrderScreen(Main.LOADING_SCREEN);
        container.putOutOfOrderScreen(Main.RUNNING_SCREEN);
    }

    private void changeLocation() {
        larrySettings.getEngine().stop();
        container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, nextFrameHandler);
        container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, switchViewHandler);
        container.putOutOfOrderScreen(Main.LOCATION_SCREEN);
        container.putOutOfOrderScreen(Main.LOCATION_CONFIRM_SCREEN);
        container.putOutOfOrderScreen(Main.LOADING_SCREEN);
        container.putOutOfOrderScreen(Main.RUNNING_SCREEN);
    }


    /**
     * Event fired when user clicks 'quit' button.
     *
     * @throws IOException
     */
    private void quitGame() {
        // cleanup
        larrySettings.getEngine().stop();
        container.nextScreen();
        container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, nextFrameHandler);
        container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, switchViewHandler);
    }

    @FXML
    private void showMenu(ActionEvent event) throws IOException {
        if (menuPopup != null) {
            if (menuIsShowing) {
                menuPopup.setVisible(false);
                menuIsShowing = false;
            } else {
                menuPopup.setVisible(true);
                menuIsShowing = true;
            }
        } else {
            error("Couldn't add menu to stack pane");
        }
    }

    /**
     * Called when UP arrow is pressed, requests the next frame from the {@link Engine}
     */
    private void goToNextFrame() {
        nextFrameService.reset();
        //TODO: pass useful information
        nextFrameService.setInput(null);
        nextFrameService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                processFrame((Frame) event.getSource().getValue());
            }
        });
        nextFrameService.start();
    }

    private void processFrame(Frame frame) {
        JunctionInfo junctions = frame.getJunctions();
        images = frame.getImages();
        if (junctions != null && images != null) {
            // update direction arrows
            for (Direction d : junctions.getRoadDirections()) {
                debug("Junction at " + d.getDegrees());
            }
            // update location text
            LarrySettings.getInstance().setLocation(junctions.getNextLocation().getLatitude(),
                    junctions.getNextLocation().getLongitude());
            // set the new image on screen, and store in 'front'
            frontView.setImage(SwingFXUtils.toFXImage(images.front, front));
            leftView.setImage(SwingFXUtils.toFXImage(images.left, left));
            rightView.setImage(SwingFXUtils.toFXImage(images.right, right));
        } else {
            error("Frame had null content");
        }

    }

    /**
     * Called when SPACEBAR is pressed
     */
    private void switchView() {
        lookingForward = !lookingForward;
        if (images != null) {
            if (lookingForward) {
                frontView.setImage(front);
                debug("Now looking forwards");
            } else {
                if (back == null) {
                    // if back doesn't exist, convert it on the fly
                    back = SwingFXUtils.toFXImage(images.back, null);
                }
                frontView.setImage(back);
                debug("Now looking backwards");
            }
        } else {
            error("Image set was null");
        }
    }

    private void lookLeft() {
        debug("looking left");
    }

    private void lookRight() {
        debug("looking right");
    }

    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    private static class NextFrameService extends Service<Frame> {

        private Direction input;

        public final Direction getInput() {
            return input;
        }

        public final void setInput(Direction i) {
            input = i;
        }

        @Override
        protected Task<Frame> createTask() {
            return new Task<Frame>() {
                @Override
                protected Frame call() throws Exception {
                    debug("Background image fetch task started");
                    return larrySettings.getEngine().nextFrame(null);
                }
            };
        }
    }

}
