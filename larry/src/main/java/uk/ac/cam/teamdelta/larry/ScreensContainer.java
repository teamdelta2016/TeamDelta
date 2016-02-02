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

import java.io.IOException;
import java.util.HashMap;

public class ScreensContainer extends StackPane {

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
     * @param name The descriptive name of the screen to add
     * @param screen The Node representing the screen to add
     */
    private void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    /**
     * Loads a screen from its FXML and adds it to the StackPane
     * Also adds the node representing the screen and it's associated controller to {@link ScreensContainer#controllers}
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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sets the specified screen as the one to display to the user
     * @param name The descriptive name of the screen to display
     * @return true if succeeds, false if screen hasn't been loaded yet
     */
    public boolean setScreen(final String name) {
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
                fade.play();
            } else {
                setOpacity(0.0);
                getChildren().add(namedScreen);
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(2500),
                                new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            return true;
        } else {
            System.err.println("Screen hasn't been loaded yet");
            return false;
        }
    }

    /**
     * Removes the screen from the StackPane, so that it can't be displayed until loaded again.
     * Removes the specified screen from {@link ScreensContainer#screens},
     * and it's associated node from {@link ScreensContainer#controllers}
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
}
