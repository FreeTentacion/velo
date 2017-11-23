package tourguide;

import java.util.ArrayList;

public class Tour {
    
    private String id;
    private String title;
    private Annotation annotation;
    
    public Tour(String id, String title, Annotation annotation) {
        this.id = id;
        this.title = title;
        this.annotation = annotation;
    }
    
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
        
      //  Waypoint.isNear
        
    }
    public void addLeg(Annotation annotation) {
        
    }
    public Waypoint getWaypoint(int step) {
        
    }
    public Leg getLeg(int step) {
        
    }
}
