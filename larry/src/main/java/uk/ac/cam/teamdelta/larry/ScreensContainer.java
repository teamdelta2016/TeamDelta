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

public class ScreensContainer extends StackPane {

    private boolean loading = false;

    /**
     * Queue of screen names to specify order of screens to be shown in UI flow Order actually specified by order in
     * which screens are added in Main
     */
    private final Deque<String> screenNames = new LinkedList<>();
    /**
     * Map linking names of screens to nodes representing them
     */
    private final HashMap<String, Node> screens = new HashMap<>();

    /**
     * Map linking nodes representing screens to their respective controllers
     */
    private final HashMap<Node, ScreenController> controllers = new HashMap<>();

    /**
     * Adds a screen to {@link ScreensContainer#screens}
     *
     * @param name   The descriptive name of the screen to add
     * @param screen The Node representing the screen to add
     */
    private void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    /**
     * Displays the screen which is at the front of the queue
     */
    public void nextScreen() {
        if (!loading) {
            loading = true;
            screenNames.addLast(screenNames.pollFirst());
            String name = screenNames.peekFirst();
            setScreen(name);
        } else {
            Logger.debug("Screen was still loading");
        }
    }

    public void prevScreen() {
        if (!loading) {
            loading = true;
            String name = screenNames.pollLast();
            screenNames.addFirst(name);
            setScreen(name);
        } else {
            Logger.debug("Screen was still loading");
        }
    }

    /**
     * Loads a screen from its FXML and adds it to the StackPane Also adds the node representing the screen and it's
     * associated controller to {@link ScreensContainer#controllers}
     *
     * @param name     the name of the screen to load
     * @param resource the location of the FXML file
     * @return true if no exception is thrown, false otherwise
     */
    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = loader.load();
            ScreenController screenController = loader.getController();
            screenController.setScreenParent(this);
            addScreen(name, loadScreen);
            controllers.put(loadScreen, screenController);
            screenNames.addLast(name);
            if (controllers.size() == 1) {
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
        loading = true;
        final Node namedScreen = screens.get(name);
        if (namedScreen != null) {
            final DoubleProperty opacity = opacityProperty();
            if (!getChildren().isEmpty()) {
                Timeline fade = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(new Duration(500),
                                new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        getChildren().remove(0);
                                        getChildren().add(0, namedScreen);
                                        controllers.get(namedScreen).setupScreen();
                                        Timeline fadeIn = new Timeline(
                                                new KeyFrame(Duration.ZERO,
                                                        new KeyValue(opacity, 0.0)),
                                                new KeyFrame(new Duration(300),
                                                        new KeyValue(opacity, 1.0)));
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
            } else {
                setOpacity(0.0);
                getChildren().add(namedScreen);
                controllers.get(namedScreen).setupScreen();
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(2500),
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

    public ScreenController getController(Node n) {
        return controllers.get(n);
    }
}
