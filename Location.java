package tourguide;

public class Location {

    private double easting;
    private double northing;
    
    public Location(double easting, double northing) {      
                this.easting = easting;
                this.northing = northing;      
            }
    
    public Displacement deltaFrom(Location waypointLocation) { // returns displacement from the next waypoint.
        Displacement d = new Displacement(this.easting - waypointLocation.easting, 
                                          this.northing - waypointLocation.northing);
        return d;
    }
    
    public double geteasting() {
        return easting;
    }
    
    public double getnorthing() {
        return northing;
    }
}
