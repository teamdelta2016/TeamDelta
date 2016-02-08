package uk.ac.cam.teamdelta.larry;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    /**
     * Constant for the name of the starting screen
     */
    public static final String START_SCREEN = "start";

    /**
     * Constant for the path of the starting screen FXML file
     */
    public static final String START_SCREEN_FXML = "/uk.ac.cam.teamdelta.larry/start.fxml";

    /**
     * Constant for the name of the location choosing screen
     */
    public static final String LOCATION_SCREEN = "location";

    /**
     * Constant for the path of the location choosing screen FXML file
     */
    public static final String LOCATION_SCREEN_FXML = "/uk.ac.cam.teamdelta.larry/location.fxml";

    /**
     * Constant for the name of the location confirm screen
     */
    public static final String LOCATION_CONFIRM_SCREEN = "locationConfirm";

    /**
     * Constant for the path of the location confirm screen FXML file
     */
    public static final String LOCATION_CONFIRM_SCREEN_FXML = "/uk.ac.cam.teamdelta.larry/locationConfirm.fxml";

    /**
     * Constant for the name of the parameter choosing screen
     */
    public static final String PARAMETERS_SCREEN = "parameters";

    /**
     * Constant for the path of the parameter choosing screen FXML file
     */
    public static final String PARAMETERS_SCREEN_FXML = "/uk.ac.cam.teamdelta.larry/params.fxml";

    /**
     * Constant for the name of the loading screen
     */
    public static final String LOADING_SCREEN = "loading";

    /**
     * Constant for the path of the loading screen FXML file
     */
    public static final String LOADING_SCREEN_FXML = "/uk.ac.cam.teamdelta.larry/loading.fxml";

    /**
     * Constant for the name of the running game screen
     */
    public static final String RUNNING_SCREEN = "running";

    /**
     * Constant for the path of the running game screen FXML file
     */
    public static final String RUNNING_SCREEN_FXML = "/uk.ac.cam.teamdelta.larry/run.fxml";

    /**
     * Constant for the name of the running game screen for other monitors
     */
    public static final String RUNNING_OTHER_SCREEN = "runningOther";

    /**
     * Constant for the path of the running game screen FXML file for other monitors
     */
    public static final String RUNNING_OTHER_SCREEN_FXML = "/uk.ac.cam.teamdelta.larry/runOther.fxml";


    public static final long KEY_HOLD_DELAY = 3000;

    /**
     * The width in pixels of the game
     */
    public static double GAME_WIDTH = 1000;

    /**
     * The height in pixels of the game
     */
    public static double GAME_HEIGHT = 800;


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Show the main application's stage. Also setup the StackPane which holds all the different screens
     *
     * @param primaryStage The application's main stage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // find out the index of the primary monitor
        Screen primary = Screen.getPrimary();
        for (int i = 0; i < Screen.getScreens().size(); i++) {
            if (Screen.getScreens().get(i).equals(primary)) {
                LarrySettings.getInstance().setPrimaryScreenIndex(i);
                break;
            }
        }

        GAME_HEIGHT = primary.getVisualBounds().getHeight();
        GAME_WIDTH = primary.getVisualBounds().getWidth();

        // create screen container
        ScreensContainer mainContainer = new ScreensContainer();

        // set the size
        mainContainer.setPrefSize(primary.getBounds().getWidth(), primary.getBounds().getHeight());

        // add screens to container
        // This is how the UI order is specified
        mainContainer.loadScreen(Main.START_SCREEN, Main.START_SCREEN_FXML);
        mainContainer.loadScreen(Main.LOCATION_SCREEN, Main.LOCATION_SCREEN_FXML);
        mainContainer.loadScreen(Main.LOCATION_CONFIRM_SCREEN, Main.LOCATION_CONFIRM_SCREEN_FXML);
        mainContainer.loadScreen(Main.PARAMETERS_SCREEN, Main.PARAMETERS_SCREEN_FXML);
        mainContainer.loadScreen(Main.LOADING_SCREEN, Main.LOADING_SCREEN_FXML);
        mainContainer.loadScreen(Main.RUNNING_SCREEN, Main.RUNNING_SCREEN_FXML);

        // set up main stage and display it
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(
                "/uk.ac.cam.teamdelta.larry/images/car.png")));
        primaryStage.setTitle("DriveByAge");
        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.show();
        primaryStage.requestFocus();
    }

}
