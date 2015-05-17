import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.*;
import java.util.*;
/**
 * @ author DivyaP
 */
public class Mapper extends GUI {
	public static final int NODE_SIZE = 4;
	private Location ORIGIN;
	private Location OLD_ORIGIN;                             // needed to allow mouse panning feature
	private double SCALE;
	private Node SELECTED_NODE;                              // selected node or start node for A* Search
	private Set<Road> SELECTED_ROADS = new HashSet<Road>();
	private Point PRESSED; 
	public static Trie ROAD_TREE = new Trie();               // to store trie of all road names
	
	//fields for A* Search
	private boolean A_SEARCH = false;
	private Node NODE_GOAL;
	private ArrayList<Segment> PATH = new ArrayList<Segment>();
	
	// fields for Articulation Points
	private boolean POINTS = false;
	private Set<Node> ART_POINTS = new HashSet<Node>();
	private Set<Node> VISITED;

	@Override
	protected void redraw(Graphics g) {		
		//draw polygons using polygon data to make map look nice
		for (Polygon p : RoadMap.getPoly()){
			p.draw(g, this.ORIGIN, this.SCALE);
		}
		
		//draw nodes and edges of graph
		for (Node n : RoadMap.getAllNodes().values()){
			g.setColor(Color.black);
			n.draw(g, this.ORIGIN, this.SCALE);
			g.setColor(Color.gray.darker());
			for(Segment s : n.getAllEdges()){
				s.draw(g, this.ORIGIN, this.SCALE);
			}
		}
		//if selected node, highlight red
		g.setColor(Color.red);
		if(this.SELECTED_NODE != null){
			this.SELECTED_NODE.draw(g, this.ORIGIN, this.SCALE);
		}
		//if selected roads, highlight blue
		g.setColor(Color.blue);
		if(!this.SELECTED_ROADS.isEmpty()){
			for(Road r : this.SELECTED_ROADS){
				for(Segment s : r.getSegments()){
					s.draw(g, this.ORIGIN, this.SCALE);
				}
			}
		}
		//if A Search, then highlight path as well
		if(this.A_SEARCH){
			g.setColor(Color.red);
			if(this.NODE_GOAL != null){
				this.NODE_GOAL.draw(g, this.ORIGIN, this.SCALE);
			}
			g.setColor(Color.orange);
			for(Segment s : this.PATH){
				s.draw(g, this.ORIGIN, this.SCALE);
			}
		}
		
		// if articulation points button pressed, highlight points
		if(this.POINTS){
			g.setColor(Color.pink);
			for(Node n : this.ART_POINTS){
				n.draw(g, this.ORIGIN, this.SCALE);
			}
		}
	}

	@Override
	protected void onClick(MouseEvent e) {
		this.SELECTED_NODE = this.selectNode(e);
		this.NODE_GOAL = null;
		this.OLD_ORIGIN = this.ORIGIN;                               //resets origin location to current point
	}

	@Override
	protected void onSearch() { 
		// reset a* search and articulation points to false so those roads/nodes are not highlighted
		this.A_SEARCH = false;
		this.POINTS = false;
		
		String name = getSearchBox().getText();
		this.SELECTED_ROADS = Mapper.ROAD_TREE.getRoadswithPrefix(name);
		getTextOutputArea().setText("Roads starting with " + name + ":");
		Set<String> roadNames = new HashSet<String>();    // to ensure that the same road names are not displayed more than once
		int roadNum = 0;                                  // to ensure only up to 10 possible names are printed
		for (Road r : this.SELECTED_ROADS) {
			roadNames.add(r.getRoadName());
			roadNum++;
			if(roadNum >= 10){
				break;
			}
		}
		for(String s : roadNames){
			getTextOutputArea().append("\n" + s + ", ");
		}
	}

	@Override
	protected void onMove(Move m) {	
		double dist = 35 * (Math.pow(this.SCALE, -0.7));       //calculates distance difference depending on scaling of map	
		if(Move.NORTH.equals(m)){
        	this.ORIGIN = this.ORIGIN.moveBy(0, dist);
		}
		else if (Move.SOUTH.equals(m)){
			this.ORIGIN = this.ORIGIN.moveBy(0, -dist);
		}
        else if (Move.EAST.equals(m)){
        	this.ORIGIN = this.ORIGIN.moveBy(dist, 0);			
		}
        else if (Move.WEST.equals(m)){
        	this.ORIGIN = this.ORIGIN.moveBy(-dist, 0);	
		}
        else if (Move.ZOOM_IN.equals(m)){      	
        	this.SCALE = this.SCALE + (10.0*this.SCALE/100);                   //zoom in by ten percent	
		}
        else if (Move.ZOOM_OUT.equals(m)){
        	this.SCALE = this.SCALE - (10.0*this.SCALE/100);                   //zoom out by ten percent			        	
		}				
		this.OLD_ORIGIN = this.ORIGIN;                                         //old origin updated
	}
	
	@Override
	protected void onPress(MouseEvent e){
		this.PRESSED = e.getPoint(); 
	}
	
	/**
	 * OnDrag method allows mouse to be used to drag the map
	 * (allows north, east, south, west movement of map with mouse)
	 */
	@Override	
	protected void onDrag(MouseEvent e){
		int xDist = (int)(this.PRESSED.getX() - e.getPoint().getX());
		int yDist = (int)(this.PRESSED.getY() - e.getPoint().getY());
		this.ORIGIN = Location.newFromPoint(new Point(xDist, yDist), this.OLD_ORIGIN, this.SCALE);	
	}
	
	/**
	 * OnScroll method allows scrolling to be used to zoom in and 
	 * out of the map.
	 */
	@Override
	protected void onScroll(MouseWheelEvent e){	
		this.SCALE = this.SCALE + (e.getPreciseWheelRotation()*this.SCALE/100);                   //zoom in by scroll amount	
	}
	
	@Override
	protected void aSearch(MouseEvent e1, MouseEvent e2, boolean time){
		this.POINTS = false;       // reset articulation points so they aren't highlighted
		
		// set the first mouse event as the selected start node
		if(e2 == null){
			this.SELECTED_NODE = this.selectNode(e1);
		}
		// set the second mouse event as the selected goal node
		else if(e1 == null){
			this.A_SEARCH = true;
			this.PATH.clear();
			this.NODE_GOAL = this.selectNode(e2);
		}
		// if both mouse events are given, do A* Search
		else {
			this.aStarSearch(time);
		}
	}
	
	/**
	 * A* Search algorithm that finds the shortest path from the selected
	 * start node to the goal node. Takes in a time parameter, and if true, 
	 * then will find the shortest path in terms of time taken, else finds 
	 * shortest path in terms of distance travelled
	 * 
	 * @param time if true then find shortest path in terms of time taken
	 */
	private void aStarSearch(boolean time) {
		PriorityQueue<ANode> fringe = new PriorityQueue<ANode>();
		fringe.add(new ANode(this.SELECTED_NODE, null, 0, estimate(this.SELECTED_NODE, null, time), new ArrayList<Segment>()));
		Set<Node> visited = new HashSet<Node>();
		ANode goal = null;
		while(!fringe.isEmpty()){
			ANode aNode = fringe.poll();
			Node n = aNode.getNode();
			if(!visited.contains(n)){
				visited.add(n);
				if(n.equals(this.NODE_GOAL)){              
					goal = aNode;
					break;
				}
				for(Segment s : n.getOutEdges()){
					if(!s.getRoad().isNoCars()){                                  // only allowed to go down path if cars are allowed
						Node neighbour = n.getOtherNode(s);                       // get neighbour node 
						if(neighbour != null && !visited.contains(neighbour) && this.notRestricted(aNode, n, s)){
							double costToNeigh = aNode.getCostToHere() + s.addTravelled(time);
							double estTotal = costToNeigh + estimate(neighbour, s, time);
							ArrayList<Segment> path = new ArrayList<Segment>(aNode.getPath());
							path.add(s);                                          // add segment to stored path
							fringe.add(new ANode(neighbour, n, costToNeigh, estTotal, path));
						}
					}
				}
			}
		}
		if(goal != null){                                  // if found goal node, get path
			this.PATH = goal.getPath();
		}
		this.printPathInfo(goal);                          // print path info
	}

	private double estimate(Node node, Segment seg, boolean time) {
		double distance = node.getSearchLocation().distance(this.NODE_GOAL.getSearchLocation());
		if(time){
			double speed = 120;      // use high average speed (underestimate total cost = admissible)
			if(seg == null) {
				return distance/speed; 
			}
			// assign speed determined by road class (higher class = faster/better route)
			switch (seg.getRoad().getRoadClass()){
			case 0: {speed = 100; break;}    // residental
			case 1: {speed = 105; break;}    // collector
			case 2: {speed = 110; break;}    // arterial
			case 3: {speed = 115; break;}    // principal highways
			case 4: {speed = 120; break;}    // major highways
			}
			return distance/speed;
		}
		return distance;
	}

	@Override
	protected void artPoints(){
		this.POINTS = true;       // set true so articulation points will be highlighted
		this.A_SEARCH = false;    // reset A* Search so a* path is not highlighted
		
		// only search through graph for articulation points if they haven't been found already
		if(this.ART_POINTS.isEmpty()){
			this.VISITED = new HashSet<Node>();
			for(Node n : RoadMap.getAllNodes().values()){
				// graph may be disconnected, hence must go through every node
				// use node as root only if it has not been visited before
				if(!this.VISITED.contains(n)){
					int numSubtrees = 0;
					n.setDepth(0);
					for(Node neighbour : n.getNeighbours()){
						if(neighbour.getDepth() == Integer.MAX_VALUE){
							this.iterArtPts(neighbour, n);
							numSubtrees++;
						}
					}
					if(numSubtrees > 1){
						this.ART_POINTS.add(n);
					}
				}
				this.VISITED.add(n);
			}
		}
	}

	/**
	 * iterative approach to finding articulation points
	 * 
	 * @param first
	 * @param root
	 */
	private void iterArtPts(Node first, Node root) {
		Stack<StackElement> stack = new Stack<StackElement>();
		stack.push(new StackElement(first, 1, (new StackElement(root, 0, null))));
		
		while(!stack.isEmpty()){
			StackElement elem = stack.peek();
			Node node = elem.getNode();
			// if node has not yet been checked for children, make a new children queue, and add children
			if(elem.getChildren() == null){
				node.setDepth(elem.getReachBack());
				elem.setChildren(new ArrayDeque<Node>());
				for(Node neighbour : node.getNeighbours()){
					if(!neighbour.equals(elem.getParent().getNode())){
						elem.addChild(neighbour);
					}
				}
			}
			// process children
			else if(!elem.getChildren().isEmpty()){
				Node child = elem.getChildren().poll();
				if(child.getDepth()<Integer.MAX_VALUE){
					elem.setReachBack(Math.min(elem.getReachBack(), child.getDepth()));
				}
				else {
					stack.push(new StackElement(child, node.getDepth()+1, elem));
				}
			}
			// all children have been processed so check if parent of node is an articulation point
			else {
				if(!node.equals(first)){
					if(elem.getReachBack() >= elem.getParent().getNode().getDepth()){
						this.ART_POINTS.add(elem.getParent().getNode());
					}
					elem.getParent().setReachBack(Math.min(elem.getParent().getReachBack(), elem.getReachBack()));
				}
				this.VISITED.add(elem.getNode());
				stack.pop();
			}
		}
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons, File restrictions) {
		getTextOutputArea().setText("Loading...");
		RoadMap.readNodes(nodes);
		RoadMap.readRoads(roads);
		RoadMap.readSegments(segments);
		RoadMap.readPolygons(polygons);
		RoadMap.readRestrictions(restrictions);
		this.setScaleAndOrigin();   
		this.ART_POINTS = new HashSet<Node>();
		getTextOutputArea().setText("Files Loaded");
	}
	
	// -----------------------------------------------------------
	// HELPER METHODS
	// -----------------------------------------------------------
	
	/**
	 * Helper method to find the original maximum x and y and minimum x
	 * locations needed to set and change the scale and origin. 
	 */
	public void setScaleAndOrigin(){
		boolean loc = false;
		double maxX= Double.MIN_VALUE;
		double maxY= Double.MIN_VALUE;
		double minX = Double.MAX_VALUE;
		for(Node n : RoadMap.getAllNodes().values()){
			if(!loc){
				maxX = n.getNodeLocation().x;
				minX = n.getNodeLocation().x;
				maxY = n.getNodeLocation().y;	
				loc = true;
			}
			else {
				if(maxX < n.getNodeLocation().x) {
					maxX = n.getNodeLocation().x;
				}
				if(minX > n.getNodeLocation().x){
					minX = n.getNodeLocation().x;
				}
				if(maxY < n.getNodeLocation().y) {
					maxY = n.getNodeLocation().y;
				}
			}
		}
		Dimension area = getDrawingAreaDimension();
		this.SCALE = area.width / (maxX - minX);
		this.ORIGIN = new Location(minX, maxY);
		this.OLD_ORIGIN = this.ORIGIN;                              //initializes old origin value
	}
	
	/**
	 * finds the node closest to the clicked mouse position
	 * and returns it
	 * 
	 * @param e mouse event that stores clicked position of mouse
	 * @return closest node
	 */
	private Node selectNode(MouseEvent e){
		Location l = Location.newFromPoint(new Point(e.getX(), e.getY()), this.ORIGIN, this.SCALE);
		double minDist = Double.MAX_VALUE;
		Node selected = null;
		for (Node n : RoadMap.getAllNodes().values()){                  
			if(selected == null){
				selected = n;
			}
			if(n.getNodeLocation().distance(l) < minDist){
				selected = n;
				minDist = selected.getNodeLocation().distance(l);
			}
		}
		this.printNodeInfo(selected);
		return selected;
	}

	/**
	 * Prints node id and road intersection information about
	 * the given node (duplicate road names are discarded)
	 * 
	 * @param n
	 */
	public void printNodeInfo(Node n){
		getTextOutputArea().setText("Intersection ID: " + n.getNodeID());
		getTextOutputArea().append("\nRoads at Intersection:");
		Set<String> roads = new HashSet<String>();                       //prevent road names from being displayed twice
		for(Segment s : n.getAllEdges()){             
			roads.add(s.getRoad().getRoadName());                        //gets the road names of the segments attached to the node
		}	
		for(String name : roads){
			getTextOutputArea().append("\n" + name);
		}
	}
	
	/**
	 * checks if there is a restriction from the previous node to the 
	 * next node going through the current node and roads...
	 * 
	 * @param aNode 
	 * @param n intersection node
	 * @param s the segment that leads to the next node
	 * @return true if not restricted, false otherwise
	 */
	private boolean notRestricted(ANode aNode, Node n, Segment s) {
		return !n.restricted(aNode.getFromSegment(), s);
	}

	/**
	 * Prints the path info including road name, length and total
	 * time taken to get to a goal node. 
	 * Uses path stored in the given ANode
	 * 
	 * @param goal
	 */
	private void printPathInfo(ANode goal) {
		getTextOutputArea().setText("Path from " + this.SELECTED_NODE.getNodeID() + " to " + this.NODE_GOAL.getNodeID());
		
		// if no route from selected to goal, print message
		if(goal == null){                                      
			getTextOutputArea().append("\nNo Route Avaliable");   
			return;
		}
		
		double total = 0;
		double totTime = 0;
		int i = 0;
		while(i<this.PATH.size()){
			String name = this.PATH.get(i).getRoad().getName();
			double length = 0;
			while(i<this.PATH.size() && this.PATH.get(i).getRoad().getName().equals(name)){
				length = length + this.PATH.get(i).getLength();
				totTime = totTime + this.PATH.get(i).addTravelled(true); 
				i++;
			}
			String len = "" + length;
			getTextOutputArea().append("\n" + name + ": " + len.substring(0,5) + "km");
			total = total + length;
		}
		String tot = "" + total;
		String totTme = "" + totTime;
		getTextOutputArea().append("\nTotal Length: " + tot.substring(0, 5) + "km");
		getTextOutputArea().append("\nTotal Time: " + totTme.substring(0,5) + " hours");
	}
	
	public static void main(String[] args) {
		new Mapper();
	}
}
