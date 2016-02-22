package uk.ac.cam.teamdelta.robert;

import uk.ac.cam.teamdelta.Constants;
import uk.ac.cam.teamdelta.Direction;
import uk.ac.cam.teamdelta.JunctionInfo;
import uk.ac.cam.teamdelta.Location;
import uk.ac.cam.teamdelta.Logger;
import java.util.HashMap;

public class Cache extends Thread {
    private Cache parent;
    private Engine engine;
    private Frame result;
    private HashMap<Direction, Cache> children;
    private Location location;
    private Direction direction;

    public Cache(
        Cache parent,
        Engine engine,
        Location location,
        Direction direction) {
        this.parent = parent;
        this.engine = engine;
        this.result = null;
        this.children = new HashMap();
        this.location = location;
        this.direction = direction;
    }

    private int getDepth() {
        int depth = 0;
        for(Cache i = this; i != null; i = i.parent, ++depth);
        return depth;
    }

    public void run() {
        Logger.debug("running thread " + Thread.currentThread().getId());
        try {
            while(getDepth() > Constants.cacheDepth)
                Thread.sleep(250);
            Logger.debug("planning route...");
            JunctionInfo next = engine.getNextPosition(location, direction);
            Logger.debug("route planned");

            Logger.debug("processing frame");
            for(Direction d : next.getRoadDirections()) {
                Logger.debug("starting new cache operation for direction" + d.getDegrees());
                Cache c = new Cache(this, engine, next.getNextLocation(), d);
                children.put(d, c);
                c.start();
            }
            result = engine.processFrame(next);
            Logger.debug("cache finished");
        } catch(InterruptedException e) {
            Logger.debug("cache interrupted");
        }
    }

    public Frame getResult() {
        Logger.debug("getResult()");
        try {
            this.join();
            return result;
        } catch(InterruptedException e) {
            Logger.error("cache interrupted");
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    public Cache chooseDirection(Direction d) {
        Logger.debug("cache choosing direction: " + d.getDegrees());
        for(Direction entry : children.keySet())
            if(d.getDegrees() != entry.getDegrees())
                children.get(entry).interrupt();
        this.parent = null;
        return children.get(d);
    }
}
