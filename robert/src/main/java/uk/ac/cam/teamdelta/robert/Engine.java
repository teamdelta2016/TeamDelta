package uk.ac.cam.teamdelta.robert;

import uk.ac.cam.teamdelta.Constants;
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

public class Engine {
    private Location m_location;
    private Direction m_direction;
    private ImageFetcher m_fetcher;
    private ImageProc m_proc;
    private RouteFinder m_route_finder;
    private Frame m_frame;

    public Engine(Location location_query, ImageProcParams params) {
        Logger.debug("starting engine");
        m_fetcher = new ImageFetcher();
        m_proc = ImageProc.getImageProc(params);
        m_route_finder = new RoutePlanner();
        m_frame = null;

        Logger.debug("querying for initial direction");
        JunctionInfo query = m_route_finder.getInitialPosition(location_query);
        m_location = query.getNextLocation();
        m_direction = query.getRoadDirections().iterator().next();
        Logger.debug("engine started");
    }

    public void firstFrame() {
        Logger.debug("retrieving first frame");
        nextFrame(m_direction);
    }

    //update state to the next appropriate location
    //return frame at the new location
    public Frame nextFrame(Direction d) {
        try {
            m_direction = d;
            Logger.debug("planning route...");
            JunctionInfo info = m_route_finder.getNextPosition(m_location,m_direction);
            m_location = info.getNextLocation();
            Logger.debug("route planned");
            Logger.debug("fetching images...");
            ImageInputSet input = m_fetcher.sendGet(Constants.pictureWidth, Constants.pictureHeight, m_location.getLatitude(),
                                                    m_location.getLongitude(), 60, (int)m_direction.getDegrees() , 0);
            Logger.debug("images retrieved");
            Logger.debug("processing images...");
            ImageOutputSet processed = m_proc.process(input, false);
            Logger.debug("images processed, frame ready");
            m_frame = new Frame(processed, info);
            return m_frame;
        } catch (MalformedURLException e) {
            Logger.error("malformed URL");
            e.printStackTrace();
            System.exit(-1);
            return null;
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

    public Frame getCurrentFrame() {
        return m_frame;
    }
}
