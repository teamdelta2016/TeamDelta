package uk.ac.cam.teamdelta;

import java.util.Set;

public class JunctionInfo {
	private Location m_next_location;
    private Direction m_primary_direction;
	private Set<Direction> m_road_angles;
	
	public Location getNextLocation(){return m_next_location;};
	public Set<Direction> getRoadDirections(){return m_road_angles;};
    public Direction getPrimaryDirection() {return m_primary_direction;}
    public Direction getClosestRoadDirection() {
        double min_diff = 360;
        Direction closest = new Direction(0);
        for (Direction dir : m_road_angles) {
            double diff = Math.min(Math.abs(m_primary_direction.getDegrees() - dir.getDegrees()),
                    Math.abs(Math.abs(m_primary_direction.getDegrees() - dir.getDegrees() - 360)));
            if (diff < min_diff) {
                min_diff = diff;
                closest = dir;
            }
        }
        return closest;
    }

    public void setPrimaryDirection(Direction d) { m_primary_direction = d;}


    public JunctionInfo(Location next_location, Set<Direction> road_angles, Direction primary_direction) {
        m_next_location = next_location;
        m_road_angles = road_angles;
        m_primary_direction = primary_direction;
    }

    public JunctionInfo(Location next_location, Direction forwardDirection){
        m_next_location = next_location;
        m_primary_direction = forwardDirection;
    }
}
