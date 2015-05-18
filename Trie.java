import java.util.*;
/**
 * @author DivyaP
 */
public class Trie {

	private final TNode root;

    public Trie(){
        root = new TNode((char)0);    // assigns null character for the root
    }

	public void addRoad(String word, Road r){
		TNode currentNode = this.root;
		for (char ch : word.toLowerCase().toCharArray()) {        // change to lower case to allow matching in trie
			currentNode = currentNode.getChild(ch);
		}
		currentNode.addRoad(r);
	}

    public boolean containsRoad (String word){                   // checks if trie contains road
        TNode currentNode = this.root;
        for (char ch : word.toLowerCase().toCharArray()) {        
            currentNode = currentNode.getNode(ch);
            if (currentNode == null) {
                break;
            }
        }
        return (currentNode != null && currentNode.getRoads() != null);
    }
    
    public Set<Road> getRoadswithPrefix(String prefix){
    	Set<Road> roads = new HashSet<Road>();
    	TNode currentNode = this.root;
        for (char ch : prefix.toLowerCase().toCharArray()) {      
            currentNode = currentNode.getNode(ch);
            if (currentNode == null) {
                break;
            }
        }
        TNode n = this.getSameRoad(currentNode, roads);
        if(n != null){
        	currentNode = n;
        }
        this.getRoads(currentNode, roads);
    	return roads;
    }

    public void getRoads(TNode node, Set<Road> roads) {        // recursive method to get all roads of a node
        if (node == null) {
            return;
        }
        if (node.getRoads() != null) {                           // adds road associated with node
        	for(Road r : node.getRoads()){
          	  roads.add(r);
        	}
        }
        if(node.getChildren() != null){                         // then gets all roads associated with children of the node
        	for (TNode n : node.getChildren().values()) {
        		getRoads(n, roads);
        	}
        }
    }    
    
    /**
     * If search prefix matches to the given node, and the given node reads 
     * a street name completely (eg.state highway 1), then trie must return roads 
     * with that exact street name. The ',' character signifies the end of a street
     * name and start of city name. Hence the method finds if current node has a child
     * holding ',' and returns it 
     */
    public TNode getSameRoad(TNode node, Set<Road> roads) {        
        if (node == null) {
            return null;
        }
        if (node.getRoads() != null) {                           // adds road associated with node
        	for(Road r : node.getRoads()){
            	  roads.add(r);
          	}
        }
        return node.getNode(',');                               // gets TNode that holds ',' character 
    }    
}
