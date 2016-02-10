package uk.ac.cam.teamdelta;

public class Location
{
	public Location(double latitude, double longitude)
	{
		m_latitude = latitude;
		m_longitude = longitude;
	}

	public Location(){this(0.0,0.0);}
	public double getLatitude() {return m_latitude;}
	public double getLongitude() {return m_longitude;}

    public void setLatitude(double m_latitude) {
        this.m_latitude = m_latitude;
    }

    public void setLongitude(double m_longitude) {
        this.m_longitude = m_longitude;
    }

    private double m_latitude, m_longitude;
}
