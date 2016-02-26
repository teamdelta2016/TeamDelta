package uk.ac.cam.teamdelta;

import java.util.Set;

public class JunctionInfo {
	private Location m_next_location;
    private Direction m_primary_direction;
	private Set<Direction> m_road_angles;
	
	/**
	 * Returns the next location as stored in this JunctionInfo
	 * @return next location
	 */
	public Location getNextLocation(){return m_next_location;};
	
	/**
	 * Returns the set of all road directions from this JunctionInfo
	 * @return set of road directions
	 */
	public Set<Direction> getRoadDirections(){return m_road_angles;};
    
	/**
	 * Returns the primary direction as stored in this Junction info.
	 * Note that it doesn't have to be in the set returned from RoadDirections,
	 * if a direction from the set is needed, use getClosestDirection instead
	 * @return primary direction
	 */
	public Direction getPrimaryDirection() {return m_primary_direction;}
	
    /**
     * Returns the direction that is included in the Set returned by getRoadDirections
     * and closest to primary direction.
     * @return road direction closest to primary direction
     */
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

    
    /**
     * Sets PrimaryDirection to d
     * @param d new primary direction
     */
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
