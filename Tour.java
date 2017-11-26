package tourguide;

import java.util.ArrayList;

public class Tour {
    
    private String id;
    private String title;
    private Annotation annotation; // this is the longer description of the tour
    private ArrayList<Waypoint> waypoints;
    private ArrayList<Leg> legs;
    
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
    
    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }
    
    public void addWaypoint(Location location, Annotation annotation) { // add a waypoint in order to the waypoints arraylist
        Waypoint addthis = new Waypoint(annotation, location); 
        int i = waypoints.size();
        waypoints.add(i, addthis);
    }
    
    public void addLeg(Annotation annotation) { // add a leg in order to the legs ArrayList
        Leg addthis = new Leg(annotation);
        legs.add(addthis);
    }
    public Waypoint getWaypoint(int step) {
        
    }
    public Leg getLeg(int step) {
        
    }
}
