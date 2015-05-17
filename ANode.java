import java.util.ArrayList;

/**
 * @ author DivyaP
 */
public class ANode implements Comparable<ANode> {
	private Node node;
	private Node parent;
	private double costToHere;
	private double totalEstCost;
	private ArrayList<Segment> path;             // store path taken to node so far so don't need to track back segments from parent node
	
	public ANode(Node n, Node p, double costHere, double totalEst, ArrayList<Segment> path){
		this.node = n;
		this.parent = p;
		this.costToHere = costHere;
		this.totalEstCost = totalEst;
		this.path = path;
		
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @return the parent
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * @return the path
	 */
	public ArrayList<Segment> getPath() {
		return path;
	}

	/**
	 * @return the costToHere
	 */
	public double getCostToHere() {
		return costToHere;
	}

	/**
	 * @return the totalEstCost
	 */
	public double getTotalEstCost() {
		return totalEstCost;
	}
	
	/**
	 * @return the most recent segment covered in the path
	 */
	public Segment getFromSegment(){
		if(this.path.isEmpty()){
			return null;
		}
		return this.path.get(this.path.size() - 1);
	}

	@Override
	public int compareTo(ANode a1) {
		double diff = this.totalEstCost - a1.getTotalEstCost();
		if(diff<0){
			return -1;
		}
		if(diff>0){
			return 1;
		}
		return 0;
	}
}
