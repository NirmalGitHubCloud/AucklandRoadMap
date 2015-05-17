import java.io.*;
import java.util.*;

/**
 * @ author DivyaP
 */
public class RoadMap {
	
	private static HashMap <Integer, Node> ALL_NODES = new HashMap<Integer, Node>();
	private static HashMap<Integer, Road> ALL_ROADS = new HashMap<Integer, Road>();
	private static ArrayList<Polygon> POLY = new ArrayList<Polygon>();

	public static Map<Integer, Node> getAllNodes() {
		return ALL_NODES;
	}
	
	public static Map<Integer, Road> getAllRoads(){
		return ALL_ROADS;
	}
	
	public static ArrayList<Polygon> getPoly(){
		return POLY;
	}

	public static void readNodes (File nodes){
		ALL_NODES = new HashMap<Integer, Node>();            		
		BufferedReader br = null;
		try {
		    br = new BufferedReader(new FileReader(nodes));
		    String line;		    
		    while ((line = br.readLine()) != null){
		    	String[] parts = line.split("\\t");
		    	Node n = new Node(parts);
		    	ALL_NODES.put(n.getNodeID(), n);		    	
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if(br != null) {br.close();}
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	public static void readRoads (File roads){
		ALL_ROADS = new HashMap<Integer, Road>();
		BufferedReader br = null;
		try {
		    br = new BufferedReader(new FileReader(roads));
		    String line = br.readLine();                               //for top line of .tab file (discards headings)
		    while ((line = br.readLine()) != null){
		    	String[] parts = line.split("\\t");	
		    	Road r = new Road (parts);
		    	ALL_ROADS.put(r.getRoadID(), r);
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if(br != null) {br.close();}
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	public static void readSegments (File segments){
		BufferedReader br = null;

		try {
		    br = new BufferedReader(new FileReader(segments));
		    String line = br.readLine();                               //for top line of .tab file (discards headings)
		    while ((line = br.readLine()) != null){
		    	String[] parts = line.split("\\t");		    	
		    	new Segment(parts);			    	
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if(br != null) {br.close();}
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	public static void readPolygons (File poly){
		BufferedReader br = null;
		try {
		    br = new BufferedReader(new FileReader(poly));
		    String line = br.readLine();                           //for top line of .tab file (discards headings)
		    while (line!= null){
		    	Polygon p = new Polygon();
		    	String type = "";
		    	while (line != null && !line.startsWith("[END]")){
		    		if (line.startsWith("Type")){
		    			p.setType(line.substring(5));
		    			type = line.substring(5);
		    		}
		    		if (line.startsWith("Data") && p.getPoints() != null){      // for more than one set of data points, a new polygon item is needed
		    			Polygon p1 = new Polygon(); 
		    			p1.setType(type);
		    			String[] parts = line.split("[,\\)\\(\\=]");	
		    			p1.setLocations(parts);
		    			POLY.add(p1);
		    		}	
		    		if (line.startsWith("Data")){
		    			String[] parts = line.split("[,\\)\\(\\=]");	
		    			p.setLocations(parts);
		    			POLY.add(p);
		    		}	
		    		line = br.readLine();
		    	}	
		    	line = br.readLine();
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if(br != null) {br.close();}
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	public static void readRestrictions (File restr){  		
		BufferedReader br = null;
		try {
		    br = new BufferedReader(new FileReader(restr));
		    String line = br.readLine();                               //for top line of .tab file (discards headings)		    
		    while ((line = br.readLine()) != null){
		    	String[] parts = line.split("\\t");	 
		    	int node = Integer.parseInt(parts[2]);
		    	ALL_NODES.get(node).addRestriction(parts);
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if(br != null) {br.close();}
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
}
