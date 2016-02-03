package uk.ac.cam.teamdelta.frank;

import java.util.TreeSet;

import uk.ac.cam.teamdelta.*;

public class RoutePlanner implements RouteFinder {
    public JunctionInfo getNextPosition(Location current_position, Direction current_direction) {
        JunctionInfo info = new JunctionInfo(new Location((float)0.0,(float)0.0), new TreeSet<Direction>());
        return info;
    }
    
	public static void main(String[] args) {

	}

}
