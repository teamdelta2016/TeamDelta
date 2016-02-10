package uk.ac.cam.teamdelta;

import java.util.Set;

public class JunctionInfo {
	private Location m_next_location;
	private Set<Direction> m_road_angles;
	
	public Location getNextLocation(){return m_next_location;};
	public Set<Direction> getRoadDirections(){return m_road_angles;};
	
	public JunctionInfo(Location next_location, Set<Direction> road_angles) {
        m_next_location = next_location;
        m_road_angles = road_angles;
    }
}
