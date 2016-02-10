package uk.ac.cam.teamdelta.robert;
import java.util.Collections;
import java.net.MalformedURLException;
import uk.ac.cam.teamdelta.*;
import uk.ac.cam.teamdelta.andy.*;
import uk.ac.cam.teamdelta.peter.*;

public class Engine {
    private Location m_location;
    private Direction m_direction;
    private ImageFetcher m_fetcher;
    private ImageProc m_proc;

    public Engine(Location location_query, ImageProcParams params) {
        Logger.debug("starting engine");
        //FIXME: snap to nearest location
        m_location = location_query;
        m_direction = new Direction(0);
        m_fetcher = new ImageFetcher();
        m_proc = ImageProc.getImageProc(params);
    }

    //update state to the next appropriate location
    //return frame at the new location
    public Frame nextFrame(UserInput l) {
        try {
            JunctionInfo ji = new JunctionInfo(m_location, Collections.emptySet());
            Logger.debug("fetching images");
            ImageInputSet input = m_fetcher.sendGet(640, 480, m_location.getLatitude(), m_location.getLongitude(), 30, 0);
            Logger.debug("processing images");
            ImageOutputSet processed = m_proc.process(input, false);
            Logger.debug("frame ready");
            return new Frame(processed, ji);
        } catch(MalformedURLException e) {
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
