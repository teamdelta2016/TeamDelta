package uk.ac.cam.teamdelta.larry;

import uk.ac.cam.teamdelta.ImageParams;
import uk.ac.cam.teamdelta.Location;

public class LarrySettings {

    private final Location l;
    private final ImageParams i;
    private int primaryScreenIndex;
    private static LarrySettings larrySettings;

    private LarrySettings() {
        l = new Location();
        i = new ImageParams();
    }

    public static LarrySettings getInstance() {
        if (larrySettings == null) {
            larrySettings = new LarrySettings();
        }
        return larrySettings;
    }

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




}
