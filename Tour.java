package tourguide;

public class Tour {
    
    private String id;
    private String title;
    private Annotation annotation;
    
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public Annotation getAnnotation() {
        return annotation;
    }
    
    public void addWaypoint(Location location, Annotation annotation) {
        
    }
    public void addLeg(Annotation annotation) {
        
    }
    public Waypoint getWaypoint(int step) {
        
    }
    public Leg getLeg(int step) {
        
    }
}
