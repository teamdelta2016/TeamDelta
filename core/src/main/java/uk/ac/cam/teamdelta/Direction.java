package uk.ac.cam.teamdelta;

public class Direction implements Comparable<Direction>
{
	public Direction(double degrees)
	{
		m_degrees = degrees;
	}
	public double getDegrees() {return m_degrees;}
	private double m_degrees;
	@Override public int compareTo(Direction that)
	{
		if (this.m_degrees < that.m_degrees) { return -1; }
		if (this.m_degrees > that.m_degrees) { return 1; }
		return 0;
	}
}
