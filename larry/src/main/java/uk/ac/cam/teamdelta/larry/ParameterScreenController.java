package uk.ac.cam.teamdelta.larry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import uk.ac.cam.teamdelta.ImageParams;

import java.io.IOException;

public class ParameterScreenController implements ScreenController {

    ScreensContainer container;

    @FXML
    Slider slider1, slider2, slider3;

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
                                                  slider3.getValue());
        container.nextScreen();
    }

    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        LarrySettings.getInstance().setParameters(slider1.getValue(),
                                                  slider2.getValue(),
                                                  slider3.getValue());
        container.prevScreen();
    }

    @FXML
    private void handleDefault1(ActionEvent event) throws IOException {
        slider1.setValue(100.0);
        slider2.setValue(0);
        slider3.setValue(10);
    }

    @FXML
    private void handleDefault2(ActionEvent event) throws IOException {
        slider1.setValue(50);
        slider2.setValue(67);
        slider3.setValue(80);
    }

    @FXML
    private void handleDefault3(ActionEvent event) throws IOException {
        slider1.setValue(30);
        slider2.setValue(11);
        slider3.setValue(0);
    }


    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void setupScreen() {
        ImageParams p = LarrySettings.getInstance().getParameters();
        slider1.setValue(p.getA());
        slider2.setValue(p.getB());
        slider3.setValue(p.getC());

        view.setImage(new Image("/uk.ac.cam.teamdelta.larry/images/test1out.jpg"));
    }

}
