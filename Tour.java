package tourguide;

import java.util.ArrayList;

public class Tour {
    
    private String id;
    private String title;
    private Annotation annotation; // this is the longer description of the tour
    private  ArrayList<Waypoint> waypoints;
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
    public ArrayList<Leg> getLegs() {
        return legs;
    }
    
    public void addWaypoint(Location location, Annotation annotation) {
        Waypoint addthis = new Waypoint(annotation, location);
        int i = waypoints.size();
        waypoints.add(i, addthis);
    }
    
    public void addLeg(Annotation annotation) {
        
    }
    public Waypoint getWaypoint(int stage) {
        return waypoints.get(stage - 1);
    }
    public Leg getLeg(int stage) {
        return legs.get(stage);
   }
}  


        