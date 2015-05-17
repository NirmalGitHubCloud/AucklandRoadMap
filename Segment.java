import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

/**
 * @ author DivyaP
 */
public class Segment {
	
	private final Road ROAD;
	private final double LENGTH;
	private final Node START_NODE;
	private final Node END_NODE;
	private List<Location> SEG_LOCATIONS; 
	
	public Segment(String[] parts){
		this.ROAD = RoadMap.getAllRoads().get(Integer.parseInt(parts[0]));
		this.ROAD.addSegment(this);
		this.LENGTH = Double.parseDouble(parts[1]);
		this.START_NODE = RoadMap.getAllNodes().get(Integer.parseInt(parts[2]));
		this.START_NODE.addOutEdge(this);	
		this.END_NODE = RoadMap.getAllNodes().get(Integer.parseInt(parts[3]));
		this.END_NODE.addInEdge(this);	

		// if not a oneway road, add segement as in and out of start/end node
		if(!this.ROAD.isOneWay()){
			this.START_NODE.addInEdge(this);
			this.END_NODE.addOutEdge(this);
		}
		
		//for articulation points search, need to store neighbouring nodes of all nodes
		this.START_NODE.addNeighbour(this.END_NODE);
		this.END_NODE.addNeighbour(this.START_NODE);

		this.SEG_LOCATIONS = new ArrayList<Location>();
		for(int i = 4; i<parts.length; i=i+2){
    		this.SEG_LOCATIONS.add(Location.newFromLatLon(Double.parseDouble(parts[i]), Double.parseDouble(parts[i+1])));    		
    	}
	}
	
	public void draw(Graphics g, Location origin, double scale){
		for(int i = 0; i<this.SEG_LOCATIONS.size() - 1; i++){	
		Point p1 = this.SEG_LOCATIONS.get(i).asPoint(origin, scale);
		Point p2 = this.SEG_LOCATIONS.get(i+1).asPoint(origin, scale);
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
	}
	
	public Road getRoad(){
		return this.ROAD;
	}
	
	public Node getStartNode() {
		return this.START_NODE;
	}

	public Node getEndNode() {
		return this.END_NODE;
	}

	public double getLength(){
		return this.LENGTH;
	}
	
	/**
	 * if time is true, will return a time value, 
	 * else returns a distance value
	 * 
	 * @param time
	 * @return
	 */
	public double addTravelled(boolean time){
		if(time){
			return (this.LENGTH/this.ROAD.getSpeed());
		}
		return this.getLength();		
	}
}
