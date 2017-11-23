package tourguide;

public class Location {

    private double easting;
    private double northing;
    
    public Location(double easting, double northing) {      
                this.easting = easting;        
                this.northing = northing;      
            }
    
    public Displacement deltaFrom(Location location) { // returns displacement from the next waypoint.
        
    }
    
    public double geteasting() {
        return easting;
    }
    
    public double getnorthing() {
        return northing;
    }
}
