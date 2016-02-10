package uk.ac.cam.teamdelta.larry;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import uk.ac.cam.teamdelta.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LocationScreenController implements ScreenController {
    private static String API_KEY;
    // apikey.txt shouldn't be included in git
    //TODO: find a better place for all the api keys we have
    static {
        try {
            //API_KEY = new BufferedReader(new FileReader("/uk.ac.cam.teamdelta.larry/apikey.txt")).readLine();
            API_KEY = new String(Files.readAllBytes(Paths.get(
                    "./src/main/resources/uk.ac.cam.teamdelta.larry/apikey.txt"))).trim();
        } catch (IOException e) {
            e.printStackTrace();
            API_KEY = "";
        }
        Logger.debug("Parsed API_KEY as " + API_KEY);
    }

    ScreensContainer container;
    @FXML
    private TextField locationText;
    @FXML
    private Label errorText;

    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void setupScreen() {
        // remove red border from textField and clear it if needed
        locationText.getStyleClass().remove("error");
        locationText.requestFocus();
        // also remove the error text
        errorText.setText("");
    }

    /**
     * Event handler for button clicks. Tries to geocode the text in {@link this#locationText}, if this succeeds, takes
     * user to {@link Main#LOCATION_CONFIRM_SCREEN}. Else, prompts user to try again.
     *
     * @param event The event object received
     * @throws IOException
     */
    @FXML
    private void handleNextAction(ActionEvent event) throws IOException {

        // Get context for using the Geocoding API
        GeoApiContext context = new GeoApiContext().setApiKey(API_KEY);
        try {
            // block whilst getting geocoding results
            GeocodingResult[] results = GeocodingApi.geocode(context, locationText.getText()).region("gb").await();

            if (results != null && results.length > 0) { // Geocoding success
                // add the location to LarrySettings
                LarrySettings.getInstance().
                setLocation(results[0].geometry.location.lat, results[0].geometry.location.lng);
                GeocodingResult[] revResults =
                    GeocodingApi.reverseGeocode(context,
                                                new LatLng(results[0].geometry.location.lat,
                                                        results[0].geometry.location.lng)).region("gb").await();
                if (revResults.length == 0) {
                    System.err.println("Geocoding isn't bijective! Reverse query failed while forward passed");
                }
                LarrySettings.getInstance().setStringLocation(
                    revResults[0].formattedAddress);
                // advance to next screen
                container.nextScreen();
            } else { // Geocoding failure: need to try again
                // Fade error text in - useless but cool
                FadeTransition ft = new FadeTransition();
                ft.setNode(errorText);
                ft.setDuration(new Duration(500));
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                errorText.setText("No results found");
                ft.play();

                locationText.requestFocus();

                // add red border to textField
                locationText.getStyleClass().add("error");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        container.prevScreen();
    }
}
