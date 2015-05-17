import java.util.Queue;

/**
 * @ author DivyaP
 */
public class StackElement {
	private final Node node;
	private int reachBack;
	private Queue<Node> children; 
	private StackElement parent;
	
	public StackElement(Node n, int rb, StackElement elem){
		this.node = n;
		this.reachBack = rb;
		this.parent = elem;
	}

	/**
	 * @return the reachBack
	 */
	public int getReachBack() {
		return this.reachBack;
	}

	/**
	 * @param reachBack the reachBack to set
	 */
	public void setReachBack(int reachBack) {
		this.reachBack = reachBack;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return this.node;
	}

	/**
	 * @return the children
	 */
	public Queue<Node> getChildren() {
		return this.children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(Queue<Node> children) {
		this.children = children;
	}

	/**
	 * @return the parent
	 */
	public StackElement getParent() {
		return this.parent;
	}
	
	public void addChild(Node child){
		if(this.children != null){
			this.children.add(child);
		}
	}

}
