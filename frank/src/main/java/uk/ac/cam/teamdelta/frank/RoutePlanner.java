package uk.ac.cam.teamdelta.frank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.cam.teamdelta.*;
import uk.ac.cam.teamdelta.frank.OsmDataMiner.OsmWay;

public class RoutePlanner implements RouteFinder {
    private static int earthRadius = 6378100; //meters
    private double singleMoveDist = 20;
    private double distTolerance = 0.1;
    private double standardAngleTolerance = 20;
    
    //Distance in meters between loc1 and loc2
    private static double dist(Location loc1, Location loc2) {
        double lat1 = loc1.getLatitude();
        double lon1 = loc1.getLongitude();
        double lat2 = loc2.getLatitude();
        double lon2 = loc2.getLongitude();
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = (Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2));
        double c = (2 * Math.atan2(Math.sqrt(Math.abs(a)), Math.sqrt(1-Math.abs(a))));
        double dist = (earthRadius * c);
        
        if (Double.isNaN(dist)) {
            return 0.0;
        }
        
        return Math.abs(dist);
    }
    
    //Bearing of the vector formed by loc1 and loc2
    private static Direction bearing(Location loc1, Location loc2){
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lonDiff= Math.toRadians(loc2.getLongitude()-loc1.getLongitude());
        double y= Math.sin(lonDiff)*Math.cos(lat2);
        double x=Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(lonDiff);

        return new Direction((Math.toDegrees(Math.atan2(y, x))+360)%360);
    }
    
    //Given a position and a bearing, this function returns a new position that we get by
    //moving the loc by distance meters in bearing direction
    private static Location moveByDist(Location loc, Direction bearing, double distance) {
        double lat1 = Math.toRadians(loc.getLatitude());
        double lon1 = Math.toRadians(loc.getLongitude());

        double lat2 = Math.asin( Math.sin(lat1)*Math.cos(distance/earthRadius) +
                        Math.cos(lat1) * Math.sin(distance/earthRadius) * Math.cos(Math.toRadians(bearing.getDegrees())));

        double lon2 = lon1 + Math.atan2(Math.sin(Math.toRadians(bearing.getDegrees())) * Math.sin(distance/earthRadius) * Math.cos(lat1),
                                 Math.cos(distance/earthRadius) - Math.sin(lat1) * Math.sin(lat2));
        return new Location(Math.toDegrees(lat2),Math.toDegrees(lon2));
    }
    
    //Angle between loc1 to loc2 and loc1 to loc3, assumes world to be flat, but it's
    //only used on range of couple 10s of meters, so it should be reasonably precise
    private static double angle(Location loc1, Location loc2, Location loc3) {
        double dist12 = dist(loc1, loc2);
        double dist13 = dist(loc1, loc3);
        double dist23 = dist(loc2, loc3);
        if ((2*dist12*dist13) == 0) {
            return 0.0;
        }
        double angle = Math.acos((dist12*dist12 + dist13*dist13 - dist23*dist23) / (2*dist12*dist13));
        return Math.toDegrees(angle);
    }
    
    //Distance of loc1 from segment formed by loc2 and loc3
    private static double distFromSegment(Location loc1, Location loc2, Location loc3) {
        double dist12 = dist(loc1, loc2);
        double angle2 = angle(loc2, loc1, loc3);
        double angle3 = angle(loc3, loc1, loc2);
        if (Math.abs(angle2) > 90) {
            return dist(loc1, loc2);
        }
        if (Math.abs(angle3) > 90) {
            return dist(loc1, loc3);
        }
        return dist12*Math.sin(Math.toRadians(angle2));
    }
    
    //Clips loc1 to the line formed by loc2 and loc3
    private static Location clipToSegment(Location loc1, Location loc2, Location loc3) {
        double dist12 = dist(loc1, loc2);
        double angle2 = angle(loc2, loc1, loc3);
        return moveByDist(loc2, bearing(loc2, loc3), dist12*Math.cos(Math.toRadians(angle2)));
    }
    
    public JunctionInfo getInitialPosition(Location current_position) {
        ClosestRoadInfo info = getInitialPosition(current_position, new Direction(0), 360, new HashSet<Integer>(), null);
        Set<Direction> dirSet = new HashSet<Direction>();
        dirSet.add(info.getInitialDirection());
        return new JunctionInfo(info.getInitialLocation(), dirSet, info.getInitialDirection());
    }
    
    //Helper class that is used to pass information from getInitialPosition (i.e. how the position is clipped to road) to getNextPosition
    //so that there is less code that's basically the same between the two
    private static class ClosestRoadInfo {
        private ArrayList<OsmWay> osmWays;
        private int minDistWay = -1;
        private int minDistNode = -1;
        private int segOrder = 0;
        private double segDist = 0;
        private Direction initialDirection;
        private Location initialLocation;
        
        ClosestRoadInfo(Location initialLocation, Direction initialDirection, ArrayList<OsmWay> osmWays, int minDistWay, int minDistNode, int segOrder, double segDist) {
            this.osmWays = osmWays;
            this.minDistWay = minDistWay;
            this.minDistNode = minDistNode;
            this.initialDirection = initialDirection;
            this.initialLocation = initialLocation;
            this.segOrder = segOrder;
            this.segDist = segDist;
        }
        
        public int getMinDistWay(){return minDistWay;}
        public int getMinDistNode(){return minDistNode;}
        public Direction getInitialDirection(){return initialDirection;}
        public Location getInitialLocation(){return initialLocation;}
        public int getSegmentOrder(){return segOrder;}
        public double getSegmentDistance(){return segDist;}
        public ArrayList<OsmWay> getOsmWays(){return osmWays;}
    }
    
    //Internal version of getInitialPosition that carries around more data - tolerance of how different can the expected bearing
    //and actual road bearing be, blacklist of roads that it got to follow, but didn't help to move the car (i.e. finished 
    //in the current junction) and a reference to data that was downloaded from OsmDataMiner, so we don't hit the network again
    private ClosestRoadInfo getInitialPosition(Location current_position, Direction current_direction, double angleTolerance, Set<Integer> blackList, ArrayList<OsmWay> roadData) {
        ClosestRoadInfo info = new ClosestRoadInfo(new Location(0,0), new Direction(0.0), null, -1, -1, 0, 0);
        OsmDataMiner miner = new OsmDataMiner();
        ArrayList<OsmWay> osmWays;
        if (roadData == null) {
            osmWays = new ArrayList<OsmWay>();
            try {
                osmWays = miner.getRoadData(current_position);
            } catch(Exception e) {
                Logger.debug("Data miner returned with an error: ");
                e.printStackTrace();
                return info;
            }
        } else {
            osmWays = roadData;
        }
        
        double minDist = 123456789;
        int minDistWay = -1;
        int minDistNode = -1;
        
        for (int i=0; i<osmWays.size(); i++) {
            if (blackList.contains(i)) {
                continue;
            }
            for (int j=0; j+1<osmWays.get(i).nodeCount(); j++) {
                double curDist = 
                        distFromSegment(current_position, osmWays.get(i).getNode(j).getLocation(),osmWays.get(i).getNode(j+1).getLocation());
                Direction curBearing =
                        bearing(osmWays.get(i).getNode(j).getLocation(),osmWays.get(i).getNode(j+1).getLocation());
                //Logger.debug("Angle to road:" + Math.abs(current_direction.getDegrees() - curBearing.getDegrees()));
                //Logger.debug("Dist to road:" + curDist);
                if (curDist < minDist && (
                         Math.abs(current_direction.getDegrees() - curBearing.getDegrees())<angleTolerance ||
                         Math.abs(current_direction.getDegrees() - curBearing.getDegrees())>(360 - angleTolerance) ||
                         Math.abs((current_direction.getDegrees() - curBearing.getDegrees() + 180.0)%360)<angleTolerance ||
                         Math.abs((current_direction.getDegrees() - curBearing.getDegrees() + 180.0)%360)>(360-angleTolerance)
                   )) {
                    minDist = curDist;
                    minDistWay = i;
                    minDistNode = j;
                }
            }
        }
        
        if (withinRange(minDist, 123456789, 0.1)) {
            Logger.debug("Failed to find closest road!");
            return info;
        }
        
        //If you want to see which road (way) was the query assigned to:
        /*Logger.debug("Minimal distance to road: " + minDist + ". Formed by:"); 
        Logger.debug(osmWays.get(minDistWay).getNode(minDistNode).getLat() + " " + osmWays.get(minDistWay).getNode(minDistNode).getLon());
        Logger.debug(osmWays.get(minDistWay).getNode(minDistNode+1).getLat() + " " + osmWays.get(minDistWay).getNode(minDistNode+1).getLon());*/
        
        Location newLoc = clipToSegment(current_position, osmWays.get(minDistWay).getNode(minDistNode).getLocation(),
                osmWays.get(minDistWay).getNode(minDistNode+1).getLocation());
        
        int segOrder = 0;
        double segDist = 0;
        Direction segDir = bearing(osmWays.get(minDistWay).getNode(minDistNode).getLocation(),osmWays.get(minDistWay).getNode(minDistNode+1).getLocation());
        
        if (Math.abs(current_direction.getDegrees() - segDir.getDegrees())<angleTolerance) {
            segOrder = 1;
            segDist = dist(newLoc, osmWays.get(minDistWay).getNode(minDistNode+1).getLocation());
        } else {
            segOrder = -1;
            segDist = dist(newLoc, osmWays.get(minDistWay).getNode(minDistNode).getLocation());
        }
        
        return new ClosestRoadInfo(newLoc, segDir, osmWays, minDistWay, minDistNode, segOrder, segDist);
    }
    
    public JunctionInfo getNextPosition(Location current_position, Direction current_direction) {
        return getNextPosition(current_position, current_direction, standardAngleTolerance, new HashSet<Integer>(), null);
    }
    
    //Internal version of getNextPosition which carries around more stuff, see internal version of getInitial position for more details
    public JunctionInfo getNextPosition(Location current_position, Direction current_direction, double angleTolerance, HashSet<Integer> blackList, ArrayList<OsmWay> roadData) {
        JunctionInfo info = new JunctionInfo(new Location(0.0, 0.0), new TreeSet<Direction>(), new Direction(0));
        
        double moveDist = singleMoveDist;
        
        ClosestRoadInfo initialInfo = getInitialPosition(current_position, current_direction, angleTolerance, blackList, roadData);
        if (initialInfo.getMinDistNode() == -1) {
            return info;
        }
        Location newLoc = initialInfo.getInitialLocation();
        Direction segDir = initialInfo.getInitialDirection();
        double segDist = initialInfo.getSegmentDistance();
        int segOrder = initialInfo.getSegmentOrder();
        int minDistNode = initialInfo.getMinDistNode();
        int minDistWay = initialInfo.getMinDistWay();
        ArrayList<OsmWay> osmWays = initialInfo.getOsmWays();
        
        if (segDist + distTolerance > moveDist) {
            Direction dir;
            if (segOrder == 1) {
                dir = segDir;
            } else {
                dir = new Direction((segDir.getDegrees()+180.0)%360);
            }
            newLoc = moveByDist(newLoc, dir, moveDist);
            Set<Direction> dirSet = new HashSet<Direction>();
            dirSet.add(dir);
            return new JunctionInfo(newLoc, dirSet, dir);
        } else {
            moveDist -= segDist;
        }
        
        int curWay = minDistWay;
        int curNode;
        if (segOrder == 1) {
            curNode = minDistNode+1;
        } else {
            curNode = minDistNode;
        }
        
        newLoc = osmWays.get(curWay).getNode(curNode).getLocation();
        
        while (moveDist > distTolerance) {
            //Logger.debug("Currently considering location: " + newLoc.getLatitude() + " " + newLoc.getLongitude());
            //Logger.debug("Way count: " + osmWays.get(curWay).getNode(curNode).getWayCount());
            
            //We reached a junction before we moved fully forward
            if (osmWays.get(curWay).getNode(curNode).getWayCount() > 1) {
                if (withinRange(moveDist, singleMoveDist, distTolerance/2)) {
                    blackList.add(curWay);
                    return getNextPosition(current_position, current_direction, standardAngleTolerance, blackList, osmWays);
                }
                OsmDataMiner.OsmNode juncNode = osmWays.get(curWay).getNode(curNode);
                Direction primary_dir;
                if (segOrder == 1) {
                    primary_dir = bearing(osmWays.get(curWay).getNode(curNode-1).getLocation(),osmWays.get(curWay).getNode(curNode).getLocation());
                } else {
                    primary_dir = bearing(osmWays.get(curWay).getNode(curNode+1).getLocation(),osmWays.get(curWay).getNode(curNode).getLocation());
                }
                Set<Direction> dirSet = new HashSet<Direction>();
                for (int i=0; i < juncNode.getWayCount(); i++) {
                    int wayNum = juncNode.getWay(i);
                    int nodePos = -1;
                    for (int j=0; j<osmWays.get(wayNum).nodeCount(); j++) {
                        if (osmWays.get(wayNum).getNode(j) == juncNode) {
                            nodePos = j;
                            break;
                        }
                    }
                    if (nodePos != -1) {
                        if (nodePos > 0) {
                            dirSet.add(bearing(osmWays.get(wayNum).getNode(nodePos).getLocation(),
                                    osmWays.get(wayNum).getNode(nodePos-1).getLocation()));
                        }
                        if (nodePos+1 < osmWays.get(wayNum).nodeCount()) {
                            dirSet.add(bearing(osmWays.get(wayNum).getNode(nodePos).getLocation(),
                                    osmWays.get(wayNum).getNode(nodePos+1).getLocation()));
                        }
                    }
                }
                return new JunctionInfo(newLoc, dirSet, primary_dir);
            }
            
            if (segOrder == 1) {
                if (curNode+1 < osmWays.get(curWay).nodeCount()) {
                    segDist = dist(osmWays.get(curWay).getNode(curNode).getLocation(),osmWays.get(curWay).getNode(curNode+1).getLocation());
                    segDir = bearing(osmWays.get(curWay).getNode(curNode).getLocation(),osmWays.get(curWay).getNode(curNode+1).getLocation());
                    //Answer is in this segment
                    if (segDist + distTolerance > moveDist) {
                        newLoc = moveByDist(newLoc, segDir, moveDist);
                        Set<Direction> dirSet = new HashSet<Direction>();
                        dirSet.add(segDir);
                        return new JunctionInfo(newLoc, dirSet, segDir);
                    } else {
                        newLoc = osmWays.get(curWay).getNode(curNode+1).getLocation();
                    }
                    curNode++;
                }else{
                    //Should only happen in dead ends
                    Direction dir = new Direction((segDir.getDegrees()+180)%360);
                    Set<Direction> dirSet = new HashSet<Direction>();
                    dirSet.add(dir);
                    return new JunctionInfo(newLoc, dirSet, dir);
                }
            } else {
                if (curNode > 0) {
                    segDist = dist(osmWays.get(curWay).getNode(curNode).getLocation(),osmWays.get(curWay).getNode(curNode-1).getLocation());
                    segDir = bearing(osmWays.get(curWay).getNode(curNode).getLocation(),osmWays.get(curWay).getNode(curNode-1).getLocation());
                    //Answer is in this segment
                    if (segDist + distTolerance > moveDist) {
                        newLoc = moveByDist(newLoc, segDir, moveDist);
                        Set<Direction> dirSet = new HashSet<Direction>();
                        dirSet.add(segDir);
                        return new JunctionInfo(newLoc, dirSet, segDir);
                    } else {
                        newLoc = osmWays.get(curWay).getNode(curNode-1).getLocation();
                    }
                    curNode--;
                }else{
                    //Should only happen in dead ends
                    Direction dir = new Direction((segDir.getDegrees()+180)%360);
                    Set<Direction> dirSet = new HashSet<Direction>();
                    dirSet.add(dir);
                    return new JunctionInfo(newLoc, dirSet, dir);
                }
            }
        }
        
        return info;
    }
    
    private static boolean withinRange(double realValue, double expectedValue, double tolerance) {
        return Math.abs(realValue-expectedValue)<tolerance;
    }
    
    //Tests of private helper functions
    private boolean testHelperFunctions() {
        boolean all_passed = true;
        all_passed &= withinRange(dist(new Location(52.208396, 0.118471), new Location(52.208350, 0.118434)), 5.7, distTolerance);
        all_passed &= withinRange(angle(new Location(52.208292, 0.118372), new Location(52.208350, 0.118434), new Location(52.208325, 0.118241)), 101, 3);
        all_passed &= withinRange(distFromSegment(new Location(52.208350, 0.118434), new Location(52.208292, 0.118372), new Location(52.208325, 0.118241)), 7.7, distTolerance);
        Location moved = moveByDist(new Location(52.208292, 0.118372), new Direction(90.0), 15);
        all_passed &= withinRange(moved.getLatitude(), 52.20829, 0.001) && withinRange(moved.getLongitude(), 0.11859, 0.001);
        all_passed &= withinRange(bearing(new Location(52.208292, 0.118372), new Location(52.20829, 0.11859)).getDegrees(), 90.8, 3);
        Location clipped = clipToSegment(new Location(52.208326, 0.118633), new Location(52.2083301, 0.1186437), new Location(52.2083815, 0.1185677));
        all_passed &= withinRange(clipped.getLatitude(), 52.208331, 0.001) && withinRange(clipped.getLongitude(), 0.118642, 0.001);
        return all_passed;
    }

    public static void main(String[] args) {
        RoutePlanner planner = new RoutePlanner();
        
        System.out.print("Test of helper functions running... ");
        Logger.debug(planner.testHelperFunctions()?"Succeeded":"FAILED");
        
        JunctionInfo info = planner.getNextPosition(new Location(52.211107, 0.106519), new Direction(0));
        Logger.debug("Next pos: ");
        Logger.debug(info.getNextLocation().getLatitude() + ", " + info.getNextLocation().getLongitude());
        Logger.debug("Primary pos:" + info.getPrimaryDirection().getDegrees() + " Closest pos: " + info.getClosestRoadDirection().getDegrees());
        for (Direction dir : info.getRoadDirections()) {
            Logger.debug(dir.getDegrees() + " ");
        }
        Logger.debug("");
    }

}