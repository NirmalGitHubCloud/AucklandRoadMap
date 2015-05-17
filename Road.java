import java.util.*;
/**
 * @ author DivyaP
 */
public class Road {
	
	private final int ROAD_ID;
	private final int TYPE;
	private final String NAME;
	private final String CITY;
	private final boolean ONE_WAY;
	private final int SPEED;
	private final int ROAD_CLASS;
	private final boolean NO_CARS;
	private final boolean NO_PEDS;
	private final boolean NO_BIKES;
	private Set<Segment> SEGMENTS;
	
	public Road (String[] parts) {
		this.ROAD_ID = Integer.parseInt(parts[0]);
		this.TYPE = Integer.parseInt(parts[1]);
		this.NAME = parts[2];
		this.CITY = parts[3];
		this.ONE_WAY = parts[4].equals("1");
		this.SPEED = Integer.parseInt(parts[5]);
		this.ROAD_CLASS = Integer.parseInt(parts[6]);
		this.NO_CARS = parts[7].equals("1");
		this.NO_PEDS = parts[8].equals("1");
		this.NO_BIKES = parts[9].equals("1");  
		this.SEGMENTS = new HashSet<Segment>();
		Mapper.ROAD_TREE.addRoad(this.getRoadName(), this);  
	}
	
	public int getRoadID() {
		return this.ROAD_ID;
	}
	
	public String getName(){
		return this.NAME;
	}
	
	public String getRoadName(){
		return this.NAME + ", " + this.CITY;
	}
	
	public boolean isOneWay(){
		return this.ONE_WAY;
	}
	
	public boolean isNoCars(){
		return this.NO_CARS;
	}

	public Set<Segment> getSegments(){
		return this.SEGMENTS;
	}
	
	public void addSegment(Segment s){
		this.SEGMENTS.add(s);
	}
	
	public int getRoadClass(){
		return this.ROAD_CLASS;
	}
	
	public double getSpeed(){
		switch (this.SPEED) {
		case 0 : { return 5; }   
		case 1 : { return 20; }          
		case 2 : { return 40; }     
		case 3 : { return 60; }    
		case 4 : { return 80; }    
		case 5 : { return 100; }     
		case 6 : { return 110; }    
		default: { return 120; }       // no limit so use 120 as max speed
		}
	}
}
