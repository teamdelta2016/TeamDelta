package uk.ac.cam.teamdelta.larry;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.cam.teamdelta.ImageProcParams;
import uk.ac.cam.teamdelta.Location;
import uk.ac.cam.teamdelta.robert.Engine;

/**
 * A singleton class holding the state of the user's choices as they go through the UI
 */
public class LarrySettings {

    /**
     * Singleton instance of this class
     */
    private static LarrySettings larrySettings;
    private final Location l;
    private ImageProcParams i;
    private String locationAddress;
    private SimpleStringProperty stringLocation = new SimpleStringProperty();
    private int primaryScreenIndex;
    private Engine engine;

    private LarrySettings() {
        l = new Location();
        i = new ImageProcParams(0.0,0.0,0.0,0.0,false);
    }

    /**
     * Singleton method for getting an instance of this class
     *
     * @return
     */
    public static LarrySettings getInstance() {
        if (larrySettings == null) {
            larrySettings = new LarrySettings();
        }
        return larrySettings;
    }

    public int getPrimaryScreenIndex() {
        return primaryScreenIndex;
    }

    //TODO: is this really needed?
    public void setPrimaryScreenIndex(int index) {
        primaryScreenIndex = index;
    }

    public Location getLocation() {
        return l;
    }

    public void setLocation(double lat, double lng) {
        l.setLatitude(lat);
        l.setLongitude(lng);
        stringLocation.setValue("Current location:\n" + lat + ", " + lng);
    }

    public ImageProcParams getParameters() {
        return i;
    }

    public void setParameters(double a, double b, double c, double d, boolean headlights) {
        i = new ImageProcParams(a, b, c, d, headlights);
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String s) {
        locationAddress = s;
    }

    public StringProperty getStringLocation(){
        return stringLocation;
    }

    public Engine getEngine(){
        return engine;
    }

    public void setEngine(Engine e){
        engine = e;
    }


}
