package tourguide;

import java.util.ArrayList;

public class Tour {
    
    private String id;
    private String title;
    private Annotation annotation; // this is the longer description of the tour
    private  ArrayList<Waypoint> waypoints;
    // an arraylist of waypoints?
    
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
    
    public void addWaypoint(Location location, Annotation annotation) {
        Waypoint n = (annotation, location); // how do you define a waypoint with the given parameters 
        int i = waypoints.size();
        waypoints.add(i, n);
    }
    
    public void addLeg(Annotation annotation) {
        
    }
    public Waypoint getWaypoint(int step) {
        
    }
    public Leg getLeg(int step) {
        
    }
}
