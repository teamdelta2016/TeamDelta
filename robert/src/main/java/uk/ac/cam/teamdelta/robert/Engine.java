package uk.ac.cam.teamdelta.robert;

import uk.ac.cam.teamdelta.Direction;
import uk.ac.cam.teamdelta.Location;

public class Engine
{
	public Engine(Location location_query)
	{
		//dispatch the location query to subsystems to find out where we are
	}
	//update state to the next appropriate location
	//return frame at the new location
	//not actually abstract
	public Frame nextFrame(Location l){
		System.out.println("next frame");
		return new Frame(null,null);
	}

    public void stop(){
        System.out.println("Stopping Engine");
    }
	public Location getLocation() {return m_location;}
	public Direction getDirection() {return m_direction;}
	private Location m_location;
	private Direction m_direction;
}
