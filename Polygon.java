import java.awt.*;
import java.util.ArrayList;

/**
 * @ author DivyaP
 */
public class Polygon {
	private String type = "";
	private ArrayList<Location> points;
	
	public Polygon(){
		this.points = new ArrayList<Location>();
	}
	
	public void setType(String t){
		this.type = t;
	}
	
	public ArrayList<Location> getPoints(){
		return this.points;
	}
	
	public void setLocations(String[] parts){
		ArrayList<Double> loc = new ArrayList<Double>();
		for(int i = 0; i<parts.length; i++){
			// store all double values from given string array
			if (parts[i] != null && parts[i].length() != 0 && !parts[i].startsWith("Data")){
				loc.add(Double.parseDouble(parts[i]));
			}
		}
		// get latitude and longitude pairs and change them to locations
		for(int i = 0; i<loc.size() - 1; i = i+2){
			points.add(Location.newFromLatLon(loc.get(i), loc.get(i+1)));
		}
	}
	
	public void draw(Graphics g, Location origin, double scale){	
		int[] xPoint = new int[this.points.size()];
		int[] yPoint = new int[this.points.size()];
		for(int i = 0; i<xPoint.length; i++){
			Point p = this.points.get(i).asPoint(origin, scale);
			xPoint[i] = p.x;
			yPoint[i] = p.y;
		}
		g.setColor(this.getColor(this.type));
		g.fillPolygon(xPoint, yPoint, xPoint.length);   
	}
	
	private Color getColor(String type) {
		switch (type) {
		case "0x1e" : { return Color.green.brighter(); }   
		case "0x13" : { return Color.lightGray; }          // buildings
		case "0x16" : { return Color.green.darker(); }     // forests
		case "0x17" : { return Color.green.darker(); }    
		case "0x19" : { return Color.gray.brighter(); }    // sports grounds
		case "0x28" : { return Color.cyan.darker(); }      // ocean
		case "0x3c" : { return Color.blue.darker(); }      // rivers/lakes
		case "0x3e" : { return Color.blue.darker(); }      
		case "0x40" : { return Color.blue.darker(); }      
		case "0x45" : { return Color.blue.brighter(); }   
		case "0x47" : { return Color.blue.brighter(); }    
		case "0x48" : { return Color.cyan; }       
		case "0x50" : { return Color.green.darker().darker(); }   
		default: {return Color.green;}                     // other
		}
	}
}
