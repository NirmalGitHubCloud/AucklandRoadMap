import java.awt.*;
import java.util.*;
/**
 * @ author DivyaP
 */
public class Node {

	private final int NODE_ID;
	private final Location NODE_LOCATION;
	private final Location SEARCH_LOCATION;     // more accurate in terms of calculating euclidean distance (NODE_LOCATION overestimates...)
	private Set<Segment> OUT_EDGES;
	private Set<Segment> IN_EDGES;
	private Set<Node> NEIGHBOURS;
	private int DEPTH = Integer.MAX_VALUE;
	private Map<Segment, Set<Segment>> RESTRICTIONS = new HashMap<Segment, Set<Segment>>();  // mapping 'from' segments to a set of 'to' segments
	
	public Node (String[] parts){
		this.NODE_ID = Integer.parseInt(parts[0]);
		this.NODE_LOCATION = Location.newFromLatLon(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		this.SEARCH_LOCATION = Location.searchLoc(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		this.OUT_EDGES = new HashSet<Segment>();
		this.IN_EDGES = new HashSet<Segment>();
		this.NEIGHBOURS = new HashSet<Node>();
	}	
	
	public Location getNodeLocation(){
		return this.NODE_LOCATION;
	}
	
	public Location getSearchLocation(){
		return this.SEARCH_LOCATION;
	}
	
	public int getNodeID() {
		return this.NODE_ID;
	}
	
	public Set<Segment> getOutEdges(){
		return this.OUT_EDGES;
	}
	
	public Set<Segment> getInEdges(){
		return this.IN_EDGES;
	}
	
	public int getDepth(){
		return this.DEPTH;
	}
	
	public void setDepth(int d){
		this.DEPTH = d;
	}
	
	public Set<Segment> getAllEdges(){
		Set<Segment> all = new HashSet<Segment>(this.OUT_EDGES);
		all.addAll(this.IN_EDGES);
		return all;
	}
		
	public void draw(Graphics g, Location origin, double scale) {
		Point p = this.NODE_LOCATION.asPoint(origin, scale);
		g.fillOval(p.x - Mapper.NODE_SIZE/2, p.y - Mapper.NODE_SIZE/2, Mapper.NODE_SIZE, Mapper.NODE_SIZE);
	}
	
	public void addOutEdge(Segment e){
		this.OUT_EDGES.add(e);
	}
	
	public void addInEdge(Segment e){
		this.IN_EDGES.add(e);
	}
	
	public void addNeighbour(Node n){
		this.NEIGHBOURS.add(n);
	}
	
	public Set<Node> getNeighbours(){
		return this.NEIGHBOURS;
	}
	
	public Node getOtherNode(Segment s){
		if(this.getAllEdges().contains(s)){
			Node start = s.getStartNode();
			Node end = s.getEndNode();
			if(this.equals(start)){
				return end;
			}
			return start;
		}
		return null;
	}
	
	/**
	 * Passes restriction information as a parameter and uses it to 
	 * get restrictions which are then stored in the restrictions map.
	 * RESTRICTIONS maps the segment 'from' to a set of segments 'to' which 
	 * indicates the restriction of following a path that as the segment 'from' 
	 * followed immediately by the segment 'to'
	 * 
	 * @param parts
	 */
	public void addRestriction(String[] parts){
		Node fromN = RoadMap.getAllNodes().get(Integer.parseInt(parts[0]));
		Node toN = RoadMap.getAllNodes().get(Integer.parseInt(parts[4]));
		Road fromR = RoadMap.getAllRoads().get(Integer.parseInt(parts[1]));
		Road toR = RoadMap.getAllRoads().get(Integer.parseInt(parts[3]));
		Segment s1 = connectingSeg(fromN, fromR);
		Segment s2 = connectingSeg(toN, toR);
		if(s1 != null && s2 != null){
			if(!this.RESTRICTIONS.containsKey(s1)){
				this.RESTRICTIONS.put(s1, new HashSet<Segment>());
			}
			this.RESTRICTIONS.get(s1).add(s2);
		}
	}
	
	/**
	 * Finds and returns the segment that connects this node to 
	 * the given node and is part of the given road
	 * 
	 * @param node
	 * @param road
	 * @return
	 */
	public Segment connectingSeg(Node node, Road road){
		for(Segment s : this.getAllEdges()){
			if(this.getOtherNode(s).equals(node) && s.getRoad().equals(road)){
				return s;
			}
		}		
		return null;
	}
	
	/**
	 * Checks if the restricted map shows a restriction between the
	 * two given segments
	 * 
	 * @param from the segment the path is coming from
	 * @param to the segment the path is going to
	 * @return true if restricted, false otherwise
	 */
	public boolean restricted(Segment from, Segment to){
		if(from != null && this.RESTRICTIONS.containsKey(from)){
			return this.RESTRICTIONS.get(from).contains(to);
		}
		return false;
	}
}
