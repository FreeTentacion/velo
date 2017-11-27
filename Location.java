package tourguide;

public class Location {

    private double easting;
    private double northing;
    
    public Location(double easting, double northing) {      
                this.easting = easting;
                this.northing = northing;      
            }
    
    public Displacement deltaFrom(Location currentLocation) { // returns displacement from the next waypoint.
        Displacement d = new Displacement(this.easting - currentLocation.easting, 
                                          this.northing - currentLocation.northing);
        return d;
    }
    
    public double getEasting() {
        return easting;
    }
    
    public double getNorthing() {
        return northing;
    }
}
