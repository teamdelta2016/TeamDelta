package uk.ac.cam.teamdelta;

public class Location {
    public Location(float latitude, float longitude) {
        m_latitude = latitude;
        m_longitude = longitude;
    }
    public float getLatitude() {
        return m_latitude;
    }
    public float getLongitude() {
        return m_longitude;
    }
    private float m_latitude, m_longitude;
}
