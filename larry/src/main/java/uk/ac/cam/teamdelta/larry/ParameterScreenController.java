package uk.ac.cam.teamdelta.larry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import uk.ac.cam.teamdelta.ImageParams;
import uk.ac.cam.teamdelta.ImageProcParams;

import java.io.IOException;

public class ParameterScreenController implements ScreenController {

    ScreensContainer container;

    @FXML
    Slider slider1, slider2, slider3, slider4;

    @FXML
    ImageView view;

    /**
     * Event handler for button clicks Sets Parameters in {@link LarrySettings} Takes you to {@link
     * Main#RUNNING_SCREEN}
     *
     * @param event The event object received
     * @throws IOException
     */
    @FXML
    private void handleNextAction(ActionEvent event) throws IOException {
        LarrySettings.getInstance().setParameters(slider1.getValue(),
                slider2.getValue(),
                slider3.getValue(),
                slider4.getValue(),
                // headlights boolean
                true);
        container.nextScreen();
    }

    /**
     * Handles the press of the back button
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        // save the state of the sliders
        LarrySettings.getInstance().setParameters(slider1.getValue(),
                slider2.getValue(),
                slider3.getValue(),
                slider4.getValue(),
                true);
        container.prevScreen();
    }

    /**
     * Handles the press of Default1 button
     *
     * @param event the event object
     * @throws IOException
     */
    @FXML
    private void handleDefault1(ActionEvent event) throws IOException {
        slider1.setValue(100.0);
        slider2.setValue(0);
        slider3.setValue(10);
        slider4.setValue(30);
    }

    /**
     * Handles the press of Default2 button
     *
     * @param event the event object
     * @throws IOException
     */
    @FXML
    private void handleDefault2(ActionEvent event) throws IOException {
        slider1.setValue(50);
        slider2.setValue(67);
        slider3.setValue(80);
        slider4.setValue(20);
    }

    /**
     * Handles the press of Default3 button
     *
     * @param event the event object
     * @throws IOException
     */
    @FXML
    private void handleDefault3(ActionEvent event) throws IOException {
        slider1.setValue(30);
        slider2.setValue(11);
        slider3.setValue(0);
        slider4.setValue(100);
    }


    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void setupScreen() {
        // retrieve previous settings
        ImageProcParams p = LarrySettings.getInstance().getParameters();
        slider1.setValue(p.blurValue);
        slider2.setValue(p.ghostFactor);
        slider3.setValue(p.nightTimeFactor);
        slider4.setValue(p.darkEdgesFactor);

        view.setImage(new Image("/uk.ac.cam.teamdelta.larry/images/test1out.jpg"));
    }

}
