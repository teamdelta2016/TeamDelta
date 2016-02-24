package uk.ac.cam.teamdelta.frank;

import uk.ac.cam.teamdelta.*;

public interface RouteFinder {
    /**
     * Returns a JunctionInfo, which contains the next position of the car on the road,
     * when it's given the current position on the road.
     * If the road is going straight it contains only one Direction in the road_angles
     * Set which faces forwards, if the next position is on a junction, then the set
     * contains multiple Directions, which correspond to all roads from given junction.
     * Also sets the primary_direction of the JunctionInfo to the Direction in which
     * the car is facing after the move to the next location. This function is primarily
     * supposed to be used by the DrivingEngine.
     * @param current_position the current position of the car, might be slightly out off the road (~5m)
     * @param current_direction the direction in which the car is facing, should be <20deg off from actual road direction
     * @return the junction info of the next position 
     * @see JunctionInfo
     */
    public JunctionInfo getNextPosition(
        Location current_position, Direction current_direction);
    
    /**
     * Returns a JunctionInfo, which contains the closest position on the road with
     * respect to initial_position. It also contains a single direction along the road
     * in the road_angles Set. The primary_direction is set to the same value. This
     * function can be used to determine the direction that's needed for the getNextPosition.
     * @param initial_position the current position of the car, can out off the road by a bit (~15m)
     * @return the jucntion info of the closest position that's on the road
     */
    public JunctionInfo getInitialPosition(Location initial_position);
}
