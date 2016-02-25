package uk.ac.cam.teamdelta.robert;

import uk.ac.cam.teamdelta.Constants;
import uk.ac.cam.teamdelta.Direction;
import uk.ac.cam.teamdelta.JunctionInfo;
import uk.ac.cam.teamdelta.Location;

import java.util.HashMap;

import static uk.ac.cam.teamdelta.Logger.debug;
import static uk.ac.cam.teamdelta.Logger.error;

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
        this.children = new HashMap<>();
        this.location = location;
        this.direction = direction;
    }

    private int getDepth() {
        int depth = 0;
        for (Cache i = this; i != null; i = i.parent, ++depth) {
            ;
        }
        return depth;
    }

    public void run() {
        debug("running thread " + Thread.currentThread().getId());
        try {
            while (getDepth() > Constants.cacheDepth) {
                Thread.sleep(250);
            }
            debug("planning route...");
            JunctionInfo next = engine.getNextPosition(location, direction);
            debug("route planned");

            debug("processing frame");
            for (Direction d : next.getRoadDirections()) {
                debug("starting new cache operation for direction" + d.getDegrees());
                Cache c = new Cache(this, engine, next.getNextLocation(), d);
                children.put(d, c);
                c.setDaemon(true);
                c.start();
            }
            result = engine.processFrame(next);
            debug("cache finished");
        } catch (InterruptedException e) {
            debug("cache interrupted");
        }
    }

    public Frame getResult() {
        debug("getResult()");
        try {
            this.join();
            return result;
        } catch (InterruptedException e) {
            error("cache interrupted");
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    public Cache chooseDirection(Direction d) {
        debug("cache choosing direction: " + d.getDegrees());
        Cache correctThread = null;
        for (Direction entry : children.keySet()) {
            if (d.getDegrees() != entry.getDegrees()) {
                children.get(entry).interrupt();
            } else {
                correctThread = children.get(entry);
            }
        }

        this.parent = null;
        if (correctThread == null) {
            error("No directions found matching direction " + d.getDegrees());
            for (Direction entry : children.keySet()) {
                error(entry.getDegrees() + "");
            }
        }
        return correctThread;
    }
}
