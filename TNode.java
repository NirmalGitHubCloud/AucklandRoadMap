import java.util.*;
/**
 * @author DivyaP
 */
public class TNode {

	private final char letter;               
	private Set<Road> roads = new HashSet<Road>();
	private Map<Character, TNode> children = null;

	public TNode(char ch) {
		this.letter = ch;
	}

	public TNode getChild(char ch) {
		if (this.children == null) {
			this.children = new HashMap<Character, TNode>();
		}
		TNode child = this.children.get(ch);
		if (child == null) {
			child = new TNode(ch);
			this.children.put(ch, child);
		}
		return child;
	}

	public void addRoad(Road r){
		this.roads.add(r);
	}
	
	public Set<Road> getRoads(){
		if(this.roads.isEmpty()){
			return null;
		}
		return this.roads;
	}

	public char getChar() {
		return this.letter;
	}	
	
	public Map<Character, TNode> getChildren(){
		return this.children;
	}
	
	public TNode getNode(char ch) {                  // returns child node with given character (or null if it doesn't exist)
		if(this.children != null){
			return this.children.get(ch);
		}
		return null;
	}
}
