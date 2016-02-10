package uk.ac.cam.teamdelta.frank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.cam.teamdelta.*;
import uk.ac.cam.teamdelta.frank.OsmDataMiner.OsmWay;

public class RoutePlanner implements RouteFinder {
    private static int earthRadius = 6378100; //meters
    private double singleMoveDist = 40;
    
    private static double dist(Location loc1, Location loc2) {
        double lat1 = loc1.getLatitude();
        double lon1 = loc1.getLongitude();
        double  lat2 = loc2.getLatitude();
        double lon2 = loc2.getLongitude();
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = (Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2));
        double c = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
        double dist = (earthRadius * c);

        return Math.abs(dist);
    }
    
    private static double bearing(Location loc1, Location loc2){
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lonDiff= Math.toRadians(loc2.getLongitude()-loc1.getLongitude());
        double y= Math.sin(lonDiff)*Math.cos(lat2);
        double x=Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(lonDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }
    
    private static Location moveByDist(Location loc, Direction bearing, double distance) {
        double lat1 = Math.toRadians(loc.getLatitude());
        double lon1 = Math.toRadians(loc.getLongitude());

        double lat2 = Math.asin( Math.sin(lat1)*Math.cos(distance/earthRadius) +
                        Math.cos(lat1) * Math.sin(distance/earthRadius) * Math.cos(Math.toRadians(bearing.getDegrees())));

        double lon2 = lon1 + Math.atan2(Math.sin(Math.toRadians(bearing.getDegrees())) * Math.sin(distance/earthRadius) * Math.cos(lat1),
                                 Math.cos(distance/earthRadius) - Math.sin(lat1) * Math.sin(lat2));
        return new Location(Math.toDegrees(lat2),Math.toDegrees(lon2));
    }
    
    //angle between 1-2 and 1-3
    private static double angle(Location loc1, Location loc2, Location loc3) {
        double dist12 = dist(loc1, loc2);
        double dist13 = dist(loc1, loc3);
        double dist23 = dist(loc2, loc3);
        double angle = Math.acos((dist12*dist12 + dist13*dist13 - dist23*dist23) / (2*dist12*dist13));
        return Math.toDegrees(angle);
    }
    
    //Distance of 1 from segment 2-3
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
    
    //Clips (i.e. places) 1 on line 2-3
    private static Location clipToSegment(Location loc1, Location loc2, Location loc3) {
        double dist12 = dist(loc1, loc2);
        double angle2 = angle(loc2, loc1, loc3);
        return moveByDist(loc2, new Direction(bearing(loc2, loc3)), dist12*Math.cos(Math.toRadians(angle2)));
    }
    
    public JunctionInfo getNextPosition(Location current_position, Direction current_direction) {
        JunctionInfo info = new JunctionInfo(new Location(0.0, 0.0), new TreeSet<Direction>());
        OsmDataMiner miner = new OsmDataMiner();
        ArrayList<OsmWay> osmWays = new ArrayList<OsmWay>();
        try {
            osmWays = miner.getRoadData(current_position);
        } catch(Exception e) {
            System.out.println("Data miner returned with an error: ");
            e.printStackTrace();
            return info;
        }
        
        double minDist = 123456789;
        int minDistWay = -1;
        int minDistNode = -1;
        
        for (int i=0; i<osmWays.size(); i++) {
            for (int j=0; j+1<osmWays.get(i).nodeCount(); j++) {
                double curDist = 
                        distFromSegment(current_position, osmWays.get(i).getNode(j).getLocation(),osmWays.get(i).getNode(j+1).getLocation());
                double curBearing =
                        bearing(osmWays.get(i).getNode(j).getLocation(),osmWays.get(i).getNode(j+1).getLocation());
                if (curDist < minDist && (
                         Math.abs(current_direction.getDegrees() - curBearing)<20.0 ||
                         Math.abs(current_direction.getDegrees() - curBearing)>340.0 ||
                         Math.abs((current_direction.getDegrees() - curBearing + 180.0)%360)<20.0 ||
                         Math.abs((current_direction.getDegrees() - curBearing + 180.0)%360)>340.0
                   )) {
                    minDist = curDist;
                    minDistWay = i;
                    minDistNode = j;
                }
            }
        }
        
        //If you want to see which way was the query assigned to:
        /*System.out.println("Minimal distance: " + minDist); 
        System.out.println(osmWays.get(minDistWay).getNode(minDistNode).getLat() + " " + osmWays.get(minDistWay).getNode(minDistNode).getLon());
        System.out.println(osmWays.get(minDistWay).getNode(minDistNode+1).getLat() + " " + osmWays.get(minDistWay).getNode(minDistNode+1).getLon());*/
        
        Location newLoc = clipToSegment(current_position, osmWays.get(minDistWay).getNode(minDistNode).getLocation(),
                osmWays.get(minDistWay).getNode(minDistNode+1).getLocation());
        
        double moveDist = singleMoveDist;
        int segOrder = 0;
        double segDist = 0;
        double segDir = bearing(osmWays.get(minDistWay).getNode(minDistNode).getLocation(),osmWays.get(minDistWay).getNode(minDistNode+1).getLocation());
        
        if (Math.abs(current_direction.getDegrees() - segDir)<20.0) {
            segOrder = 1;
            segDist = dist(newLoc, osmWays.get(minDistWay).getNode(minDistNode+1).getLocation());
        } else {
            segOrder = -1;
            segDist = dist(newLoc, osmWays.get(minDistWay).getNode(minDistNode).getLocation());
        }
        
        if (segDist + 0.1 > moveDist) {
            Direction dir;
            if (segOrder == 1) {
                dir = new Direction(segDir);
            } else {
                dir = new Direction((segDir+180.0)%360);
            }
            newLoc = moveByDist(newLoc, dir, moveDist);
            Set<Direction> dirSet = new HashSet<Direction>();
            dirSet.add(dir);
            return new JunctionInfo(newLoc, dirSet);
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
        
        while (moveDist > 0.1) {
            if (segOrder == 1) {
                if (curNode+1 < osmWays.get(curWay).nodeCount()) {
                    segDist = dist(osmWays.get(curWay).getNode(curNode).getLocation(),osmWays.get(curWay).getNode(curNode+1).getLocation());
                    segDir = bearing(osmWays.get(curWay).getNode(curNode).getLocation(),osmWays.get(curWay).getNode(curNode+1).getLocation());
                    //Answer is in this segment
                    if (segDist + 0.1 > moveDist) {
                        Direction dir = new Direction(segDir);
                        newLoc = moveByDist(newLoc, dir, moveDist);
                        Set<Direction> dirSet = new HashSet<Direction>();
                        dirSet.add(dir);
                        return new JunctionInfo(newLoc, dirSet);
                    } else {
                        newLoc = osmWays.get(curWay).getNode(curNode+1).getLocation();
                    }
                    //We reached a junction before we moved fully forward
                    if (osmWays.get(curWay).getNode(curNode+1).getWayCount() > 1) {
                        OsmDataMiner.OsmNode juncNode = osmWays.get(curWay).getNode(curNode+1);
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
                                    dirSet.add(new Direction(
                                            bearing(osmWays.get(wayNum).getNode(nodePos).getLocation(),
                                                    osmWays.get(wayNum).getNode(nodePos-1).getLocation())));
                                }
                                if (nodePos+1 < osmWays.get(wayNum).nodeCount()) {
                                    dirSet.add(new Direction(
                                            bearing(osmWays.get(wayNum).getNode(nodePos).getLocation(),
                                                    osmWays.get(wayNum).getNode(nodePos+1).getLocation())));
                                }
                            }
                        }
                        return new JunctionInfo(newLoc, dirSet);
                    }
                    curNode++;
                }else{
                    //Should only happen in dead ends
                    Direction dir = new Direction((segDir+180)%360);
                    Set<Direction> dirSet = new HashSet<Direction>();
                    dirSet.add(dir);
                    return new JunctionInfo(newLoc, dirSet);
                }
            } else {
                if (curNode > 0) {
                    segDist = dist(osmWays.get(curWay).getNode(curNode).getLocation(),osmWays.get(curWay).getNode(curNode-1).getLocation());
                    segDir = bearing(osmWays.get(curWay).getNode(curNode).getLocation(),osmWays.get(curWay).getNode(curNode-1).getLocation());
                    //Answer is in this segment
                    if (segDist + 0.1 > moveDist) {
                        Direction dir = new Direction(segDir);
                        newLoc = moveByDist(newLoc, dir, moveDist);
                        Set<Direction> dirSet = new HashSet<Direction>();
                        dirSet.add(dir);
                        return new JunctionInfo(newLoc, dirSet);
                    } else {
                        newLoc = osmWays.get(curWay).getNode(curNode-1).getLocation();
                    }
                    //We reached a junction before we moved fully forward
                    if (osmWays.get(curWay).getNode(curNode-1).getWayCount() > 1) {
                        OsmDataMiner.OsmNode juncNode = osmWays.get(curWay).getNode(curNode-1);
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
                                    dirSet.add(new Direction(
                                            bearing(osmWays.get(wayNum).getNode(nodePos).getLocation(),
                                                    osmWays.get(wayNum).getNode(nodePos-1).getLocation())));
                                }
                                if (nodePos+1 < osmWays.get(wayNum).nodeCount()) {
                                    dirSet.add(new Direction(
                                            bearing(osmWays.get(wayNum).getNode(nodePos).getLocation(),
                                                    osmWays.get(wayNum).getNode(nodePos+1).getLocation())));
                                }
                            }
                        }
                        return new JunctionInfo(newLoc, dirSet);
                    }
                    curNode--;
                }else{
                    //Should only happen in dead ends
                    Direction dir = new Direction((segDir+180)%360);
                    Set<Direction> dirSet = new HashSet<Direction>();
                    dirSet.add(dir);
                    return new JunctionInfo(newLoc, dirSet);
                }
            }
        }
        
        return info;
    }

    public static void main(String[] args) {
        RoutePlanner planner = new RoutePlanner();
        
        //Simple tests of helper functions, expected values ~5.7m ~100deg ~7.7m ~52.20829, 0.11859 ~90 ~52.208331, 0.118642
        /*System.out.println(dist(new Location(52.208396, 0.118471), new Location(52.208350, 0.118434))); //ca 6m
        System.out.println(angle(new Location(52.208292, 0.118372), new Location(52.208350, 0.118434), new Location(52.208325, 0.118241))); //ca 100 deg
        System.out.println(distFromSegment(new Location(52.208350, 0.118434), new Location(52.208292, 0.118372), new Location(52.208325, 0.118241))); //ca 8 m
        Location moved = moveByDist(new Location(52.208292, 0.118372), new Direction(90.0), 15);
        System.out.println(moved.getLatitude() + ", " + moved.getLongitude());
        System.out.println(bearing(new Location(52.208292, 0.118372), new Location(52.20829, 0.11859))); //ca 90deg
        Location clipped = clipToSegment(new Location(52.208326, 0.118633), new Location(52.2083301, 0.1186437), new Location(52.2083815, 0.1185677));
        System.out.println(clipped.getLatitude() + ", " + clipped.getLongitude());*/
        
        //planner.getNextPosition(new Location(52.208396, 0.118471), new Direction(0));
        //JunctionInfo info = planner.getNextPosition(new Location(52.208326, 0.118633), new Direction(315));
        //JunctionInfo info = planner.getNextPosition(new Location(52.207374, 0.118183), new Direction(90));
        JunctionInfo info = planner.getNextPosition(new Location(52.20670, 0.1110736), new Direction(90));
        System.out.println("Next pos: ");
        System.out.println(info.getNextLocation().getLatitude() + ", " + info.getNextLocation().getLongitude());
        for (Direction dir : info.getRoadDirections()) {
            System.out.print(dir.getDegrees() + " ");
        }
        System.out.println();
    }

}