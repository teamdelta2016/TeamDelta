package uk.ac.cam.teamdelta.frank;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.ac.cam.teamdelta.*;

//Note: this data miner took inspiration (and some parts of code) from http://wiki.openstreetmap.org/wiki/Java_Access_Example example,
//which was released under public domain

public class OsmDataMiner {
    private static final String OVERPASS_API = "http://www.overpass-api.de/api/interpreter";
    
    public static Document getXMLFile(String location) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        return docBuilder.parse(location);
    }
    
    public static ArrayList<OsmWay> getNodesAndRoads(Document xmlDocument) {
        TreeMap<String,OsmNode> osmNodes = new TreeMap<String,OsmNode>();

        //Finds all nodes so we can group them into wWays later
        Node osmRoot = xmlDocument.getFirstChild();
        NodeList osmXMLNodes = osmRoot.getChildNodes();
        for (int i = 1; i < osmXMLNodes.getLength(); i++) {
            Node item = osmXMLNodes.item(i);
            if (item.getNodeName().equals("node")) {
                NamedNodeMap attributes = item.getAttributes();
                NodeList tagXMLNodes = item.getChildNodes();
                Map<String, String> tags = new HashMap<String, String>();
                for (int j = 1; j < tagXMLNodes.getLength(); j++) {
                    Node tagItem = tagXMLNodes.item(j);
                    NamedNodeMap tagAttributes = tagItem.getAttributes();
                    if (tagAttributes != null) {
                        tags.put(tagAttributes.getNamedItem("k").getNodeValue(), tagAttributes.getNamedItem("v")
                                .getNodeValue());
                    }
                }
                Node namedItemID = attributes.getNamedItem("id");
                Node namedItemLat = attributes.getNamedItem("lat");
                Node namedItemLon = attributes.getNamedItem("lon");
                Node namedItemVersion = attributes.getNamedItem("version");

                String id = namedItemID.getNodeValue();
                String latitude = namedItemLat.getNodeValue();
                String longitude = namedItemLon.getNodeValue();
                ArrayList<Integer> ways = new ArrayList<Integer>();

                osmNodes.put(id, new OsmNode(id, Double.parseDouble(latitude), Double.parseDouble(longitude), tags, ways));
            }
        }
        
        ArrayList<OsmWay> osmWays = new ArrayList<OsmWay>();
        
        //Adds already stored Nodes into Ways
        int wayCounter = 0;
        for (int i = 1; i < osmXMLNodes.getLength(); i++) {
            Node item = osmXMLNodes.item(i);
            if (item.getNodeName().equals("way")) {
                NamedNodeMap attributes = item.getAttributes();
                
                Node namedItemID = attributes.getNamedItem("id");
                String id = namedItemID.getNodeValue();
                
                osmWays.add(new OsmWay(id));
                
                NodeList tagXMLNodes = item.getChildNodes();
                Map<String, String> tags = new HashMap<String, String>();
                for (int j = 1; j < tagXMLNodes.getLength(); j++) {
                    Node tagItem = tagXMLNodes.item(j);
                    if (tagItem.getNodeName().equals("tag")) {
                        NamedNodeMap tagAttributes = tagItem.getAttributes();
                        if (tagAttributes != null) {
                            osmWays.get(osmWays.size()-1).addTag(tagAttributes.getNamedItem("k").getNodeValue(),
                                    tagAttributes.getNamedItem("v").getNodeValue());
                        }
                    } else if (tagItem.getNodeName().equals("nd")) {
                        NamedNodeMap tagAttributes = tagItem.getAttributes();
                        if (tagAttributes != null) {
                            //Adds the node as found in loop above
                            OsmNode wayMember = osmNodes.get(tagAttributes.getNamedItem("ref").getNodeValue());
                            wayMember.addWay(wayCounter);
                            osmWays.get(osmWays.size()-1).addNode(wayMember);
                        }
                    }
                }
                wayCounter++;
            }
        }
        
        return osmWays;
    }
    
    public static ArrayList<OsmWay> getNodesAndRoadsInRadius(Location location, int radius) throws IOException,
        SAXException, ParserConfigurationException {
        
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        
        //Should find all ways that are contained in or cross the given radius, and are tagged with "highway" - so they
        //should mostly be roads
        System.out.println("called with: " + "(node(around:" + radius +","+ lat +"," + lon + ");way[\"highway\"](around:"
                +radius +","+ lat + "," + lon + ");node(w)->.x;);out;");
        return OsmDataMiner.getNodesAndRoads(getNodesViaOverpass(
                "(node(around:" + radius +","+ lat +"," + lon + ");way[\"highway\"](around:"
                +radius +","+ lat + "," + lon + ");node(w)->.x;);out;"));
    }
    
    public static Document getNodesViaOverpass(String query) throws IOException, ParserConfigurationException, SAXException {
        String hostname = OVERPASS_API;
        String queryString = query;
        
        URL osm = new URL(hostname);
        HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
        printout.writeBytes("data=" + URLEncoder.encode(queryString, "utf-8"));
        printout.flush();
        printout.close();

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        return docBuilder.parse(connection.getInputStream());
    }
    
    public static class OsmNode {
        private String id;
        private Location location;
        private Map<String, String> tags;
        private ArrayList<Integer> ways;
        
        public OsmNode(String id, double latitude, double longitude, Map<String, String> tags, ArrayList<Integer> ways) {
            this.id = id;
            this.location = new Location(latitude, longitude);
            this.tags = tags;
            this.ways = ways;
        }
        
        public String getId() {return id;}
        public Location getLocation() {return location;}
        public double getLat() {return location.getLatitude();}
        public double getLon() {return location.getLongitude();}
        public int getWay(int i) {return ways.get(i);}
        public int getWayCount() {return ways.size();}
        
        public void addWay(int i) {ways.add(i);}
    }
    
    public static class OsmWay {
        private String id;
        private Map<String, String> tags;
        
        private final ArrayList<OsmNode> nodesAlongWay; 
        OsmWay(String id) {
            this.id = id;
            this.tags = new TreeMap<String, String>();
            this.nodesAlongWay = new ArrayList<OsmNode>();
        }
        
        public void addNode(OsmNode node) {nodesAlongWay.add(node);}        
        public void addTag(String k, String v) {tags.put(k,v);}
        
        public String getId() {return id;}
        public OsmNode getNode(int pos) {return nodesAlongWay.get(pos);}
        public int nodeCount() {return nodesAlongWay.size();}
        public Map<String,String> getTags() {return tags;}
    }
    
    private void prettyPrint(OsmWay osmWay) {
        System.out.println("Way " + osmWay.getId() + ":");
        
        Map<String, String> tags = osmWay.getTags();
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            System.out.println("  Tag: " + entry.getKey() + ": " + entry.getValue());
        }
        
        for (int i=0; i<osmWay.nodeCount(); i++) {
            System.out.println("  Node: " + osmWay.getNode(i).getLat() + ", " + osmWay.getNode(i).getLon());
            if (osmWay.getNode(i).getWayCount() > 1) {
                System.out.print("    Member of:");
                for (int j=0; j<osmWay.getNode(i).getWayCount(); j++) {
                    System.out.print(" " + osmWay.getNode(i).getWay(j));
                }
                System.out.println();
            }
        }
    }
    
    public ArrayList<OsmWay> getRoadData(Location location) throws IOException, SAXException, ParserConfigurationException { 
        ArrayList<OsmWay> osmWays = getNodesAndRoadsInRadius(location, 20);
        /*for (OsmWay osmWay : osmWays) {
            prettyPrint(osmWay);
        }*/
        return osmWays;
    }
}
