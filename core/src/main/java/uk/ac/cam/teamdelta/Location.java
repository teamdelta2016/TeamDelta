package uk.ac.cam.teamdelta;

public class Location
{
	public Location(double latitude, double longitude)
	{
		m_latitude = latitude;
		m_longitude = longitude;
	}
	public double getLatitude() {return m_latitude;}
	public double getLongitude() {return m_longitude;}
	public void reset(double latitude, double longitude)
	{
        m_latitude = latitude;
        m_longitude = longitude;
    }
	private double m_latitude, m_longitude;
}
