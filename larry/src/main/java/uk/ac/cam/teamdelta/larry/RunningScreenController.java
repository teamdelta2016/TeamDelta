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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import uk.ac.cam.teamdelta.Direction;
import uk.ac.cam.teamdelta.ImageOutputSet;
import uk.ac.cam.teamdelta.JunctionInfo;
import uk.ac.cam.teamdelta.Logger;
import uk.ac.cam.teamdelta.robert.Engine;
import uk.ac.cam.teamdelta.robert.Frame;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static uk.ac.cam.teamdelta.Logger.debug;
import static uk.ac.cam.teamdelta.Logger.error;

public class RunningScreenController implements ScreenController {

    private static LarrySettings larrySettings = LarrySettings.getInstance();
    private static GridPane menuPopup;
    private static boolean menuIsShowing = false;
    @FXML
    Button button;
    @FXML
    ImageView frontView, leftView, rightView, backView;
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

    private StackPane navOverlay;
    private Map<Direction, ImageView> navMap;
    private Image hArrow = new Image("/uk.ac.cam.teamdelta.larry/images/highlightarrow.png");
    private Image arrow = new Image("/uk.ac.cam.teamdelta.larry/images/arrow.png");
    private Image sArrow = new Image("/uk.ac.cam.teamdelta.larry/images/selectedarrow.png");
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

    private Direction facingDirection;
    private Direction intendDirection;

    @Override
    public void showScreen() {

        menuIsShowing = false;


        processFrame(larrySettings.getEngine().getCurrentFrame());
        double width = 1280;
        double height = 640;
        leftView.setEffect(new PerspectiveTransform(-300, 0, 300, 0,
                300, height, -300, 960));
        rightView.setEffect(new PerspectiveTransform(340, 0, 900, 0,
                940, 960, 340, height));
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
                if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.DOWN)) {
                    if (event.getCode().equals(KeyCode.DOWN)) {
                        intendDirection = new Direction((facingDirection.getDegrees() + 180) % 360);
                    }
                    goToNextFrame();
                    final EventHandler<KeyEvent> ev = this;
                    container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, ev);
                } else if (event.getCode().equals(KeyCode.RIGHT) || event.getCode().equals(KeyCode.LEFT)) {
                    Direction nextDir;
                    if (event.getCode().equals(KeyCode.RIGHT)) {
                        //cycle right in navMap
                        nextDir = getNextLargest();
                    } else {
                        //cycle left in navMap
                        nextDir = getNextSmallest();
                    }
                    //do highlighting
                    unhighlightArrow(intendDirection);
                    highlightArrow(nextDir);
                    intendDirection = nextDir;
                    Logger.debug("Intend direction: " + intendDirection.getDegrees());
                }
            }
        };

        switchViewHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.SPACE)) {
                    switchView();
                }
            }
        };

        stackPane.getChildren().add(menuPopup);
    }

    private void changeParameters() {
        cleanup();
        container.putOutOfOrderScreen(Main.PARAMETERS_SCREEN);
        container.putOutOfOrderScreen(Main.LOADING_SCREEN);
        container.putOutOfOrderScreen(Main.RUNNING_SCREEN);
        container.showOutOfOrder();
    }

    private void changeLocation() {
        cleanup();
        container.putOutOfOrderScreen(Main.LOCATION_SCREEN);
        container.putOutOfOrderScreen(Main.LOCATION_CONFIRM_SCREEN);
        container.putOutOfOrderScreen(Main.LOADING_SCREEN);
        container.putOutOfOrderScreen(Main.RUNNING_SCREEN);
        container.showOutOfOrder();
    }


    /**
     * Event fired when user clicks 'quit' button.
     *
     * @throws IOException
     */
    private void quitGame() {
        cleanup();
        container.nextScreen();
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

    @FXML
    private void hideMenu(MouseEvent event) throws IOException {
        menuPopup.setVisible(false);
        menuIsShowing = false;
    }

    /**
     * Called when UP arrow is pressed, requests the next frame from the {@link Engine}
     */
    private void goToNextFrame() {
        selectArrow(intendDirection);
        nextFrameService.reset();
        //TODO: pass useful information
        facingDirection = intendDirection;
        nextFrameService.setInput(facingDirection);
        nextFrameService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                processFrame((Frame) event.getSource().getValue());
            }
        });
        nextFrameService.start();
    }

    private void processFrame(Frame frame) {
        container.getScene().addEventHandler(KeyEvent.KEY_PRESSED, nextFrameHandler);
        debug("Processing frame");
        JunctionInfo junctions = frame.getJunctions();
        images = frame.getImages();
        //TODO: remove backwards junction - if/when it is implemented
        if (junctions != null && images != null) {
            // update direction arrows
            for (Direction d : junctions.getRoadDirections()) {
                debug("Junction at " + d.getDegrees());
            }

            facingDirection = junctions.getPrimaryDirection();
            /*for (Iterator<Direction> it = junctions.getRoadDirections().iterator(); it.hasNext();) {

                facingDirection = it.next();
                break;
            } */
            intendDirection = junctions.getClosestRoadDirection();
            Logger.debug("Intend Direction: " + intendDirection.getDegrees());

            navMap = new TreeMap<Direction, ImageView>();
            stackPane.getChildren().remove(navOverlay);

            navOverlay = arrowOverlay(junctions);
            stackPane.getChildren().add(navOverlay);
            // make sure the menu button is always on top
            button.toFront();

            highlightArrow(intendDirection);

            // update location text
            LarrySettings.getInstance().setLocation(junctions.getNextLocation().getLatitude(),
                    junctions.getNextLocation().getLongitude());
            // set the new image on screen, and store in 'front'
            debug("Setting front left and back images");
            front = SwingFXUtils.toFXImage(images.front, null);
            frontView.setImage(SwingFXUtils.toFXImage(images.front, front));
            leftView.setImage(SwingFXUtils.toFXImage(images.left, left));
            rightView.setImage(SwingFXUtils.toFXImage(images.right, right));
            back = null;
            if (!lookingForward) {
                backView.setImage(SwingFXUtils.toFXImage(images.back, back));
            }

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
                backView.setVisible(false);
                debug("Now looking forwards");
            } else {
                // convert back on the fly
                if (back == null) {
                    back = SwingFXUtils.toFXImage(images.back, null);
                }
                backView.setImage(back);
                backView.setVisible(true);
                debug("Now looking backwards");
            }
        } else {
            error("Image set was null");
        }
    }

    private void cleanup() {
        larrySettings.getEngine().stop();
        container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, nextFrameHandler);
        container.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, switchViewHandler);
        menuPopup.setVisible(false);
    }

    private StackPane arrowOverlay(JunctionInfo junctions) {
        StackPane p = new StackPane();
        for (Direction d : junctions.getRoadDirections()) {
            ImageView arrowView = new ImageView();
            arrowView.setImage(arrow);
            double rotateVal = d.getDegrees() - facingDirection.getDegrees();
            arrowView.getTransforms().add(new Translate(0, 150));
            arrowView.getTransforms().add(new Rotate(rotateVal, 50, 100));
            navMap.put(d, arrowView);
            p.getChildren().add(arrowView);

        }
        return p;
    }

    private void highlightArrow(Direction d) {
        ImageView iv = navMap.get(d);
        if (iv == null) { error("NULL IV - d was not in direction set"); }
        iv.setImage(hArrow);
    }

    private void unhighlightArrow(Direction d) {
        ImageView iv = navMap.get(d);
        iv.setImage(arrow);
    }

    private void selectArrow(Direction d){
        ImageView iv = navMap.get(d);
        iv.setImage(sArrow);
    }

    private Direction getNextLargest() {
        if (navMap.size() > 1) {
            Iterator<Direction> it = navMap.keySet().iterator();
            Direction d;
            while (it.hasNext()) {
                d = it.next();
                if (d.compareTo(intendDirection) == 0) {
                    break;
                }
                //reached intendDirection
            }
            if (it.hasNext()) {
                return it.next(); //return next CW direction
            }
            //otherwise this is the largest in the treeset
            Iterator<Direction> newit = navMap.keySet().iterator();
            return newit.next();
        }
        return intendDirection;
    }

    private Direction getNextSmallest() {
        if (navMap.size() > 1) {
            Iterator<Direction> it = navMap.keySet().iterator();
            Direction prev = null;
            Direction curr = null;
            while (it.hasNext()) {
                prev = curr;
                curr = it.next();
                if (curr.compareTo(intendDirection) == 0) {
                    break;
                }
                //reached intendDirection
            }
            if (prev != null) {
                return prev; //return next ACW direction
            }
            //otherwise this is the smallest in the treeSet
            while (it.hasNext()) {
                curr = it.next();
            }
            return curr;
            //return largest in treeSet
        }
        return intendDirection;
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
                    return larrySettings.getEngine().nextFrame(input);
                }
            };
        }
    }

}
