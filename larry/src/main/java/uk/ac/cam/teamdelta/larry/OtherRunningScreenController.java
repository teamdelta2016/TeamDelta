package uk.ac.cam.teamdelta.larry;

import com.sun.glass.ui.Screen;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class OtherRunningScreenController implements ScreenController {

    private ScreensContainer container;

    private int screenIndex;

    @FXML
    ImageView imageView;

    @Override
    public void setScreenParent(ScreensContainer screensContainer) {
        container = screensContainer;
    }

    @Override
    public void setupScreen() {
        imageView.setFitWidth(Screen.getScreens().get(screenIndex).getWidth()/1.4);
    }

    public void setScreenIndex(int i){
        screenIndex = i;
    }

    public void setImage(Image i){
        imageView.setImage(i);
    }
}
