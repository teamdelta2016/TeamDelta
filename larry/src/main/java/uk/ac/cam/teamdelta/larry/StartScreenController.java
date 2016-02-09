package uk.ac.cam.teamdelta.larry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class StartScreenController implements ScreenController {

    ScreensContainer container;

    @FXML
    Button beginBtn;

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
        beginBtn.setDefaultButton(true);
    }

}
