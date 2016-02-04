package uk.ac.cam.teamdelta.larry;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartScreenController implements Initializable, ScreenController {

    ScreensContainer container;

    @FXML
    Button beginBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Event handler for button clicks Takes you to {@link Main#LOCATION_SCREEN}
     *
     * @param event The event object received
     * @throws IOException
     */
    @FXML
    private void handleNextAction(ActionEvent event) throws IOException {
        // go to location screen
        container.nextScreen();
    }


    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void setupScreen() {
        // make sure Enter Key is captured
        beginBtn.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    container.nextScreen();
                }
            }
        });
    }

}
