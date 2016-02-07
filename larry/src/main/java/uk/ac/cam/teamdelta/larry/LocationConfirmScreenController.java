package uk.ac.cam.teamdelta.larry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class LocationConfirmScreenController implements ScreenController {

    ScreensContainer container;

    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void setupScreen() {
        retrievedLocation.setText(LarrySettings.getInstance().getStringLocation());
    }

    private static final String API_KEY = "AIzaSyBYeEDUeckhXm_j8gpymwisXALFuzOShXk";

    @FXML
    private Label retrievedLocation;


    @FXML
    private void handleConfirm(ActionEvent event) throws IOException {
        container.nextScreen();
    }

    @FXML
    private void handleReject(ActionEvent event) throws IOException {
        container.prevScreen();
    }
}
