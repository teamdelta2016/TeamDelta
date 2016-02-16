package uk.ac.cam.teamdelta.larry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * The controller for the LocationConfirm screen. The user has just entered a location, and this screen shows them the
 * doubly looked up version (geocoded and reverse geocoded) so they can see if their query returned the correct result.
 * If not, they can go back and try again.
 */
public class LocationConfirmScreenController implements ScreenController {
    ScreensContainer container;
    @FXML
    private Label retrievedLocation;

    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void showScreen() {
        retrievedLocation.setText(LarrySettings.getInstance().getLocationAddress());
    }

    @Override
    public void setupScreen() {

    }

    @FXML
    private void handleConfirm(ActionEvent event) throws IOException {
        container.nextScreen();
    }

    @FXML
    private void handleReject(ActionEvent event) throws IOException {
        container.prevScreen();
    }
}
