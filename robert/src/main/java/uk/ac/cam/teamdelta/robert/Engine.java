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

    private Location location;
    private Direction direction;
    private ImageFetcher fetcher;
    private ImageProc proc;
    private RouteFinder routeFinder;
    private Frame frame;
    private Cache cache;

    public Engine(Location location_query, ImageProcParams params) {
        Logger.debug("starting engine");
        fetcher = new ImageFetcher();
        proc = ImageProc.getImageProc(params);
        routeFinder = new RoutePlanner();
        frame = null;

        Logger.debug("querying for initial direction");
        JunctionInfo query = routeFinder.getInitialPosition(location_query);
        location = query.getNextLocation();
        direction = query.getRoadDirections().iterator().next();
        cache = new Cache(null, this, location, direction);
        cache.start();
        Logger.debug("engine started");
    }

    public synchronized Frame processFrame(JunctionInfo next) {
        try {
            Logger.debug("fetching images...");
            ImageInputSet input = fetcher.sendGet(
                    Constants.pictureWidth,
                    Constants.pictureHeight,
                    next.getNextLocation().getLatitude(),
                    next.getNextLocation().getLongitude(),
                    Constants.fov,
                    (int) next.getPrimaryDirection().getDegrees(),
                    0);
            Logger.debug("images retrieved");
            Logger.debug("processing images...");
            boolean isJunction = next.getRoadDirections().size() > 1;
            ImageOutputSet processed = proc.process(input, isJunction);
            Logger.debug("images processed, frame ready");
            return new Frame(processed, next);
        } catch (MalformedURLException e) {
            Logger.error("malformed URL");
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    public synchronized JunctionInfo getNextPosition(Location location, Direction direction) {
        return routeFinder.getNextPosition(location, direction);
    }

    public void firstFrame() {
        Logger.debug("retrieving first frame");
        frame = cache.getResult();
        // nextFrame(direction);
    }

    //update state to the next appropriate location
    //return frame at the new location
    public Frame nextFrame(Direction d) {
        Logger.debug("asking for next frame from " + cache.hashCode());
        cache = cache.chooseDirection(d);
        frame = cache.getResult();
        return frame;
    }

    public void stop() {
        cache = null;
        Logger.debug("stopping engine");
    }

    public Location getLocation() {
        return location;
    }

    public Direction getDirection() {
        return direction;
    }

    public Frame getCurrentFrame() {
        return frame;
    }
}
