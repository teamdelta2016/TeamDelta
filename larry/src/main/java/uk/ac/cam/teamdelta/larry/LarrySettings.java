package uk.ac.cam.teamdelta.larry;

import uk.ac.cam.teamdelta.ImageParams;
import uk.ac.cam.teamdelta.Location;

/**
 * A singleton class holding the state of the user's choices as they go through the UI
 */
public class LarrySettings {

    /**
     * Singleton instance of this class
     */
    private static LarrySettings larrySettings;
    private final Location l;
    private final ImageParams i;
    private String stringLocation;
    private int primaryScreenIndex;

    private LarrySettings() {
        l = new Location();
        i = new ImageParams();
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
    }

    public ImageParams getParameters() {
        return i;
    }

    public void setParameters(double a, double b, double c) {
        i.setParams(a, b, c);
    }

    public String getStringLocation() {
        return stringLocation;
    }

    public void setStringLocation(String s) {
        stringLocation = s;
    }


}
