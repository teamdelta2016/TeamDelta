package uk.ac.cam.teamdelta.larry;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import uk.ac.cam.teamdelta.Logger;

import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A custom subclass of {@link StackPane} which holds all the screens in the UI and transitions
 * between them when needed.
 */
public class ScreensContainer extends StackPane {

    /**
     * A separate queue of screen names to be used when screens are needed in an order different to normal. Works in the
     * same way as {@link ScreensContainer#screenQueue}
     */
    private final Deque<String> outOfOrderQueue = new LinkedList<>();

    /**
     * Queue of screen names to specify order of screens to be shown in UI flow Order actually specified by order in
     * which screens are added in Main. Currently displayed screen will be at the head.
     */
    private final Deque<String> screenQueue = new LinkedList<>();

    /**
     * Map linking names of screens to nodes representing them
     */
    private final HashMap<String, Node> screens = new HashMap<>();

    /**
     * Map linking nodes representing screens to their respective controllers
     */
    private final HashMap<Node, ScreenController> controllers = new HashMap<>();

    /**
     * Boolean to prevent moving to the next screen while the current one is loading still
     */
    private boolean loading = false;

    /**
     * Adds a screen to {@link ScreensContainer#screens}
     *
     * @param name   The descriptive name of the screen to add
     * @param screen The Node representing the screen to add
     */
    public void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    /**
     * Adds a screen to the {@link ScreensContainer#outOfOrderQueue}
     *
     * @param name the descriptive name of the screen to add
     */
    public void putOutOfOrderScreen(String name) {
        outOfOrderQueue.add(name);
    }

    /**
     * Called by functions that need out-of-order screens to make sure the head of the {@link
     * ScreensContainer#outOfOrderQueue out-of-order queue} is currently on display
     */
    public void showOutOfOrder() {
        String name = outOfOrderQueue.peek();
        if (!loading) {
            loading = true;
            setScreen(name);
        } else {
            Logger.error("Screen was still loading");
        }
    }

    /**
     * Displays the screen which is next in the {@link ScreensContainer#screenQueue queue}
     */
    public void nextScreen() {
        String name;
        // If already loading another screen, ignore this event
        if (!loading) {
            loading = true;
            if (outOfOrderQueue.size() > 0) { // take from out of order queue if non-empty

                // add the current head to the end of the queue
                outOfOrderQueue.addLast(outOfOrderQueue.pollFirst());

                // display the new head of the queue
                name = outOfOrderQueue.peekFirst();

                if (name.equals(Main.RUNNING_SCREEN)) {
                    // out-of-order runs always finish when the RUNNING_SCREEN is reached
                    // empty the out-of-order queue, so that the standard queue is used again
                    outOfOrderQueue.clear();
                }
            } else {

                // add the current head to the end of the queue
                screenQueue.addLast(screenQueue.pollFirst());

                // display the new head of the queue
                name = screenQueue.peekFirst();
            }
            setScreen(name);
        } else {
            Logger.debug("Screen was still loading");
        }
    }

    /**
     * Displays the screen at the back of the {@link ScreensContainer#screenQueue queue}
     */
    public void prevScreen() {
        String name;
        // If already loading another screen, ignore this event
        if (!loading) {
            loading = true;
            if (outOfOrderQueue.size() > 0) { // take from out of order queue if non-empty

                // display the current tail of the queue
                name = outOfOrderQueue.pollLast();

                // and add it to the front
                outOfOrderQueue.addFirst(name);

                if (name.equals(Main.RUNNING_SCREEN)) {
                    // out-of-order runs always finish when the RUNNING_SCREEN is reached
                    // empty the out-of-order queue, so that the standard queue is used again
                    outOfOrderQueue.clear();
                }
            } else {

                // display the current tail of the queue
                name = screenQueue.pollLast();

                // and add it to the front
                screenQueue.addFirst(name);
            }
            setScreen(name);
        } else {
            Logger.debug("Screen was still loading");
        }
    }

    /**
     * Loads a screen from its FXML and adds it to the {@link ScreensContainer StackPane}. Also adds the node
     * representing the screen and it's associated controller to {@link ScreensContainer#controllers}
     * @param name     the name of the screen to load
     * @param resource the location of the FXML file
     * @return true if no exception is thrown, false otherwise
     */
    public boolean loadScreen(String name, String resource) {
        try {
            // load the JavaFX Node from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = loader.load();
            ScreenController screenController = loader.getController();
            screenController.setScreenParent(this);

            // add it to our maps
            addScreen(name, loadScreen);
            controllers.put(loadScreen, screenController);

            // invoke setupScreen() to perform one-time setup
            screenController.setupScreen();

            // add this screen to the queue
            screenQueue.addLast(name);

            if (controllers.size() == 1) {
                // also make sure this screen is displayed if it is the only one loaded
                setScreen(name);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sets the specified screen as the one to display to the user
     *
     * @param name The descriptive name of the screen to display
     * @return true if succeeds, false if screen hasn't been loaded yet
     */
    public boolean setScreen(final String name) {
        // retrieve the node for the screen with this name
        final Node namedScreen = screens.get(name);

        if (namedScreen != null) {
            final DoubleProperty opacity = opacityProperty();

            if (!getChildren().isEmpty()) {
                // have to fade out another screen, then fade this one in
                Timeline fade = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(new Duration(500),
                                // event called when the timeline has finished
                                new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {

                                        // remove the currently displayed screen and add this one
                                        // to the display graph
                                        getChildren().remove(0);
                                        getChildren().add(0, namedScreen);

                                        // invoke showScreen to call setup code on the screen
                                        // each time it is displayed
                                        controllers.get(namedScreen).showScreen();
                                        Timeline fadeIn = new Timeline(
                                                new KeyFrame(Duration.ZERO,
                                                        new KeyValue(opacity, 0.0)),
                                                new KeyFrame(new Duration(300),
                                                        new KeyValue(opacity, 1.0)));
                                        // start the fade in animation
                                        fadeIn.play();

                                    }
                                }, new KeyValue(opacity, 0.0)));
                fade.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        loading = false;
                    }
                });
                fade.play();
            } else { // This is the first screen - nothing to fade out,
                // only need to fade this in
                setOpacity(0.0);
                getChildren().add(namedScreen);
                controllers.get(namedScreen).showScreen();

                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(1000),
                                new KeyValue(opacity, 1.0)));
                fadeIn.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        loading = false;
                    }
                });
                fadeIn.play();
            }
            return true;
        } else {
            System.err.println("Screen hasn't been loaded yet");
            return false;
        }
    }

    /**
     * Removes the screen from the StackPane, so that it can't be displayed until loaded again. Removes the specified
     * screen from {@link ScreensContainer#screens}, and it's associated node from {@link ScreensContainer#controllers}
     *
     * @param name The name of the screen to unload
     * @return true if succeeds, false if the screen didn't exist
     */
    public boolean unloadScreen(String name) {
        Node node = screens.remove(name);
        if (node == null) {
            System.err.println("Couldn't unload screen " + name + ", didn't exist");
            return false;
        } else {
            if (controllers.remove(node) == null) {
                System.err.println("Couldn't unload controller for " + name + ", didn't exist");
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Get a reference to a specific screen's controller instance
     *
     * @param node the {@link Node} representing the screen
     * @return the {@link ScreenController} associated with the node
     */
    public ScreenController getController(Node node) {
        return controllers.get(node);
    }
}
