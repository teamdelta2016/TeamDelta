package uk.ac.cam.teamdelta.larry;

public interface ScreenController {

    /**
     * Sets the value of screensContainer
     * @param screensContainer The parent container
     */
    void setScreenParent(ScreensContainer screensContainer);

    /**
     * Called when {@link ScreensContainer#setScreen(String)} is called.
     * Runs any actions that need to be done just before the screen is displayed to the user.
     */
    void setupScreen();
}
