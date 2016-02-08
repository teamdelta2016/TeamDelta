package uk.ac.cam.teamdelta.larry;

import uk.ac.cam.teamdelta.ImageParams;
import uk.ac.cam.teamdelta.Location;

/**
 * A singleton class holding the state of the user's choices as they
 * go through the UI
 */
public class LarrySettings {

    private final Location l;
    private String stringLocation;
    private final ImageParams i;
    private int primaryScreenIndex;
    /**
     * Singleton instance of this class
     */
    private static LarrySettings larrySettings;

    private LarrySettings() {
        l = new Location();
        i = new ImageParams();
    }

    /**
     * Singleton method for getting an instance of this class
     * @return
     */
    public static LarrySettings getInstance() {
        if (larrySettings == null) {
            larrySettings = new LarrySettings();
        }
        return larrySettings;
    }

    //TODO: is this really needed?
    public void setPrimaryScreenIndex(int index){
        primaryScreenIndex = index;
    }

    public int getPrimaryScreenIndex(){
        return primaryScreenIndex;
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

    public void setStringLocation(String s){
        stringLocation = s;
    }

    public String getStringLocation(){
        return stringLocation;
    }


}
