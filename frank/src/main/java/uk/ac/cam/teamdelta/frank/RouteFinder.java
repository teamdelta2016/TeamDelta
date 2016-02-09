package uk.ac.cam.teamdelta.frank;

import uk.ac.cam.teamdelta.*;

public interface RouteFinder {
    public JunctionInfo getNextPosition(
        Location current_position, Direction current_direction);
}
