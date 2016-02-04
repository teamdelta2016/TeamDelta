package uk.ac.cam.teamdelta.robert;
import uk.ac.cam.teamdelta.*;
import uk.ac.cam.teamdelta.larry.*;

public abstract class Engine {
    public Engine(String location_query) {
        //dispatch the location query to subsystems to find out where we are
    }
    //update state to the next appropriate location
    //return frame at the new location
    //not actually abstract
    public abstract Frame nextFrame(UserInput u);
    public Location getLocation() {
        return m_location;
    }
    public Direction getDirection() {
        return m_direction;
    }
    private Location m_location;
    private Direction m_direction;
}
