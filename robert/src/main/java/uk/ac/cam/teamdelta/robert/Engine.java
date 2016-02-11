package uk.ac.cam.teamdelta.robert;

import uk.ac.cam.teamdelta.Direction;
import uk.ac.cam.teamdelta.ImageInputSet;
import uk.ac.cam.teamdelta.ImageOutputSet;
import uk.ac.cam.teamdelta.ImageProcParams;
import uk.ac.cam.teamdelta.JunctionInfo;
import uk.ac.cam.teamdelta.Location;
import uk.ac.cam.teamdelta.Logger;
import uk.ac.cam.teamdelta.andy.ImageFetcher;
import uk.ac.cam.teamdelta.frank.RouteFinder;
import uk.ac.cam.teamdelta.frank.RoutePlanner;
import uk.ac.cam.teamdelta.peter.ImageProc;

import java.net.MalformedURLException;
import java.util.Collections;

public class Engine {

    private Location m_location;
    private Direction m_direction;
    private ImageFetcher m_fetcher;
    private ImageProc m_proc;
    private RouteFinder m_routeFinder;

    public Engine(Location location_query, ImageProcParams params) {
        Logger.debug("starting engine");
        //FIXME: snap to nearest location
        m_location = location_query;
        m_direction = new Direction(0);
        m_fetcher = new ImageFetcher();
        m_proc = ImageProc.getImageProc(params);
        m_routeFinder = new RoutePlanner();
    }

    //update state to the next appropriate location
    //return frame at the new location
    public Frame nextFrame(Direction d) {
        try {
            JunctionInfo ji = m_routeFinder.getNextPosition(m_location,m_direction);
            Logger.debug(ji.getNextLocation().getLatitude() + ", " + ji.getNextLocation().getLongitude());
            m_location = ji.getNextLocation();
            Logger.debug("fetching images");
            ImageInputSet input = m_fetcher.sendGet(640, 480, m_location.getLatitude(),
                    m_location.getLongitude(), 60, (int)m_direction.getDegrees() , 0);
            Logger.debug("processing images");
            ImageOutputSet processed = m_proc.process(input, false);
            Logger.debug("frame ready");
            return new Frame(processed, ji);
        } catch (MalformedURLException e) {
            Logger.error("Got malformed URL");
            return new Frame(null, null);
        }
    }

    public void stop() {
        Logger.debug("stopping engine");
    }

    public Location getLocation() {
        return m_location;
    }

    public Direction getDirection() {
        return m_direction;
    }
}
