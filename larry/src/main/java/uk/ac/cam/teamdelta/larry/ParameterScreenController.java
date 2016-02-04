package uk.ac.cam.teamdelta.larry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ParameterScreenController implements Initializable, ScreenController {

    ScreensContainer container;

    @FXML
    Slider slider1, slider2, slider3;

    @FXML
    ImageView view;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Event handler for button clicks
     * Sets Parameters in {@link LarrySettings}
     * Takes you to {@link Main#RUNNING_SCREEN}
     * @param event The event object received
     * @throws IOException
     */
    @FXML
    private void handleNextAction(ActionEvent event) throws IOException {
        LarrySettings.getInstance().setParameters(slider1.getValue() / 100,
                                                  slider2.getValue() / 100,
                                                  slider3.getValue() / 100);
        container.nextScreen();
    }


    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void setupScreen() {
        slider1.setValue(0.0);
        slider2.setValue(0.0);
        slider3.setValue(0.0);
        view.setImage(new Image("/uk.ac.cam.teamdelta.larry/images/test1out.jpg"));
    }

}
