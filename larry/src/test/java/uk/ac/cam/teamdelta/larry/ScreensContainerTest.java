package uk.ac.cam.teamdelta.larry;

import javafx.embed.swing.JFXPanel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

/**
 * Created by Alex on 03/02/2016.
 */
public class ScreensContainerTest {

    ScreensContainer mainContainer;

    @Before
    public void setUp() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JFXPanel(); // initializes JavaFX environment
                latch.countDown();
            }
        });
        latch.await();
        mainContainer = new ScreensContainer();
        mainContainer.loadScreen(Main.START_SCREEN, Main.START_SCREEN_FXML);
        mainContainer.loadScreen(Main.LOCATION_SCREEN, Main.LOCATION_SCREEN_FXML);
        mainContainer.loadScreen(Main.PARAMETERS_SCREEN, Main.PARAMETERS_SCREEN_FXML);
        mainContainer.loadScreen(Main.RUNNING_SCREEN, Main.RUNNING_SCREEN_FXML);
    }

    @After
    public void tearDown() throws Exception {
        mainContainer.unloadScreen(Main.START_SCREEN);
        mainContainer.unloadScreen(Main.LOCATION_SCREEN);
        mainContainer.unloadScreen(Main.PARAMETERS_SCREEN);
        mainContainer.unloadScreen(Main.RUNNING_SCREEN);

    }

    //@Test
    //public void testNextScreen() throws Exception {
    //    assertEquals(mainContainer.getController(mainContainer.getChildren().get(0)).getClass().getSimpleName(),
    //            "StartScreenController");
    //    mainContainer.nextScreen();
    //    // screen gets loaded asynchronously so wait for transition to finish
    //    Thread.sleep(2000);
    //    ScreenController s = mainContainer.getController(mainContainer.getChildren().get(0));
    //    assertEquals(s.getClass().getSimpleName(),"LocationScreenController");
    //
    //}
    //
    //@Test
    //public void testPrevScreen() throws Exception {
    //    assertEquals(mainContainer.getController(mainContainer.getChildren().get(0)).getClass().getSimpleName(),
    //            "StartScreenController");
    //    mainContainer.prevScreen();
    //    Thread.sleep(2000);
    //    ScreenController s = mainContainer.getController(mainContainer.getChildren().get(0));
    //    assertEquals(s.getClass().getSimpleName(),"RunningScreenController");
    //}
}