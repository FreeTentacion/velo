package tourguide;

public class Waypoint {
    
    private Annotation annotation;
    private Location location;
    
    public Waypoint(Annotation annotation, Location location) {
        this.annotation = annotation;
        // nothing about the location in the constructor?
        this.location = location;
    }
    
    public Annotation getannotation() {
        return annotation;
    }
    
    public Location getLocation() {
        return location;
    }
    
    
    public boolean near(Location location) {
        
        return true;
        
        
        
    }
}
