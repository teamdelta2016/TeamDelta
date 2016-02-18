package uk.ac.cam.teamdelta.larry;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import uk.ac.cam.teamdelta.ImageProcParams;
import uk.ac.cam.teamdelta.peter.ImageProc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ParameterScreenController implements ScreenController {

    ScreensContainer container;

    @FXML
    Slider slider1, slider2, slider3, slider4;

    JLabel imgLbl;

    BufferedImage imgL, imgR;

    @FXML
    GridPane gridPane;

    @FXML
    VBox vbox;

    private boolean setByButton = false;

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
        setByButton = true;
        slider1.setValue(100.0);
        slider2.setValue(0);
        slider3.setValue(10);
        slider4.setValue(30);
        updateImg();
    }

    /**
     * Handles the press of Default2 button
     *
     * @param event the event object
     * @throws IOException
     */
    @FXML
    private void handleDefault2(ActionEvent event) throws IOException {
        setByButton = true;
        slider1.setValue(50);
        slider2.setValue(67);
        slider3.setValue(80);
        slider4.setValue(20);
        updateImg();
    }

    /**
     * Handles the press of Default3 button
     *
     * @param event the event object
     * @throws IOException
     */
    @FXML
    private void handleDefault3(ActionEvent event) throws IOException {
        setByButton = true;
        slider1.setValue(30);
        slider2.setValue(11);
        slider3.setValue(0);
        slider4.setValue(100);
        updateImg();
    }

    private void updateImg() {
        ImageProcParams p = new ImageProcParams(slider1.getValue(),
                slider2.getValue(),slider3.getValue(),slider4.getValue(),true);
        BufferedImage b = ImageProc.processTest(
                imgL, imgR, p);
        setByButton = false;
        imgLbl.setIcon(new ImageIcon(b));
        vbox.requestLayout();
    }


    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void showScreen() {
        // retrieve previous settings
        ImageProcParams p = LarrySettings.getInstance().getParameters();
        slider1.setValue(p.blurValue);
        slider2.setValue(p.ghostFactor);
        slider3.setValue(p.nightTimeFactor);
        slider4.setValue(p.darkEdgesFactor);

        slider1.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(!setByButton && (!slider1.isValueChanging()
                        || newValue.doubleValue() == slider1.getMax()
                        || newValue.doubleValue() == slider1.getMin())){
                    updateImg();
                }
            }
        });
        slider2.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(!setByButton && (!slider2.isValueChanging()
                        || newValue.doubleValue() == slider2.getMax()
                        || newValue.doubleValue() == slider2.getMin())){
                    updateImg();
                }
            }
        });
        slider3.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(!setByButton && (!slider3.isValueChanging()
                        || newValue.doubleValue() == slider3.getMax()
                        || newValue.doubleValue() == slider3.getMin())){
                    updateImg();
                }
            }
        });
        slider4.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(!setByButton && (!slider4.isValueChanging()
                        || newValue.doubleValue() == slider4.getMax()
                        || newValue.doubleValue() == slider4.getMin())){
                    updateImg();
                }
            }
        });
    }

    @Override
    public void setupScreen() {
        try {
            imgL = ImageIO.read(new File(this.getClass().getResource("/uk.ac.cam.teamdelta.larry/images/Kings_L.jpg")
                                             .toURI()));
            imgR = ImageIO.read(new File(this.getClass().getResource("/uk.ac.cam.teamdelta.larry/images/Kings_R.jpg")
                                            .toURI()));
            SwingNode s = new SwingNode();
            imgLbl = new JLabel(new ImageIcon(imgL));
            vbox.getChildren().add(s);
            vbox.setAlignment(Pos.CENTER);
            updateImg();
            s.setContent(imgLbl);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
