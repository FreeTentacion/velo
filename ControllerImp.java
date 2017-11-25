/**
 * 
 */
package tourguide;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import tourguide.ModeEnum.Mode;

/**
 * @author pbj
 */
public class ControllerImp implements Controller {
    private static Logger logger = Logger.getLogger("tourguide");
    private static final String LS = System.lineSeparator();
    
    private Mode mode;
    private Location currentLocation;
    private int currentStage;
    private boolean browseDetails;
    private String browseDetailsTourId;
    private Tour t;
    private Waypoint currentWaypoint;
    private ArrayList<Waypoint> waypoints;
    private double waypointradius;
    private double waypointSeparation;
    private int legcounter = 0;
    private boolean lastinstructioncreatewaypoint = false;

    private String startBanner(String messageName) {
        return  LS 
                + "-------------------------------------------------------------" + LS
                + "MESSAGE: " + messageName + LS
                + "-------------------------------------------------------------";
    }
    
    public ControllerImp(double waypointRadius, double waypointSeparation) { // What's the difference between these two again?
        this.waypointradius = waypointRadius;
        this.waypointSeparation = waypointSeparation;
    }
    
    public Location getcurrentlocation() {
        return currentLocation;
    }
    
    // Not sure if this is needed
    public ArrayList<Waypoint> getWaypoints() { 
        return waypoints;
    }
    //--------------------------
    // Create tour mode
    //--------------------------

    // Some examples are shown below of use of logger calls.  The rest of the methods below that correspond 
    // to input messages could do with similar calls.
    
    @Override
    public Status startNewTour(String id, String title, Annotation annotation) {
        logger.fine(startBanner("startNewTour"));
        
        if ( !ModeEnum.isBrowse() ) {
            return new Status.Error("Current Mode is Invalid to Start New Tour.");
        }
        
        ModeEnum.setMode(Mode.CREATE);
        
        t  = new Tour(id, title, annotation);
        currentStage = 0;
        
        addLeg(annotation); // adds the first leg, with the annotation directing the user to the starting location
        currentStage++;
        return Status.OK;
    }

    @Override
    public Status addWaypoint(Annotation annotation) {
        logger.fine(startBanner("addWaypoint"));
        
        if ( !ModeEnum.isCreate() ) {
            return new Status.Error("Current Mode is Invalid for Adding Waypoint.");
        }
        
        if (currentStage == 1) { // doesn't need to check if it's too close to the previous waypoint, because the previous waypoint doesn't exist.
            t.addWaypoint(currentLocation, annotation); // the add waypoint method in tour needs to update the array list in this class
            lastinstructioncreatewaypoint = true;
            return Status.OK;
        }
        // the following if statements checks two waypoints aren't being added at the same stage
        if (waypoints.size() == currentStage) {
            return new Status.Error("Already have a waypoint for this stage");  // again assuming this then cuts the method off here          
        }
        // the following code checks that the user is outside of the previous waypoint radius before creating a new one 
        
        currentWaypoint = waypoints.get(currentStage); // current waypoint
        // get current location, and find the distance between currentWaypoint's location and that of the current location
        Location l = currentWaypoint.getLocation();
        double wapnorth = l.getnorthing();
        double wapeast = l.geteasting();
        double cureast = currentLocation.geteasting();
        double curnorth = currentLocation.getnorthing();
        double separationDistance = Math.sqrt(((wapnorth-curnorth)*(wapnorth-curnorth))+((wapeast-cureast)*(wapeast-cureast)));
        if (separationDistance < waypointSeparation ) {
            return new Status.Error("Too close to insert a new Waypoint."); // would this method just cut here if this error occurs?
        }
        else {
            t.addWaypoint(currentLocation, annotation);
            
            
        }
        lastinstructioncreatewaypoint = true;
        return Status.OK;
    }

    @Override
    public Status addLeg(Annotation annotation) {
        logger.fine(startBanner("addLeg"));
        
        if ( !ModeEnum.isCreate() ) {
            return new Status.Error("Current Mode is Invaild for Adding Leg");
        }
        if (legcounter>=currentStage) {
            return new Status.Error("There is already a leg for the respective waypoint");
        }
        
        t.addLeg(annotation);
        
        currentStage++; // now on two
        legcounter++; // legcounter on one
        
        // does there need to be code so that a new leg can only be added after a new waypoint?
        lastinstructioncreatewaypoint = false;
        return Status.OK;
    }

    @Override
    public Status endNewTour() {
        logger.fine(startBanner("endNewTour"));
        
        
        if ( !ModeEnum.isCreate() ) {
            return new Status.Error("Current Mode is Invalid for Ending Tour");
        }
        
        if (lastinstructioncreatewaypoint) {
            ModeEnum.setMode(Mode.BROWSE); // does this fulfill this "the app should return to the overview sub-mode of the browse mode."
            // output chunk containing title, number of legs, number of waypoints (added so far)
            Chunk c = new Chunk().new CreateHeader(t.getTitle(), legcounter, currentStage -1 ); // error here? Constructor for chunk?
            c.toString(); // outputs the chunk, lord knows why peanut butter jelly called these chunks
            return Status.OK;
        }
        else {
            return new Status.Error("Tour creation cannot end on a leg");
        }
    }

    //--------------------------
    // Browse tours mode
    //--------------------------

    @Override
    public Status showTourDetails(String tourID) {
        if ( !ModeEnum.isBrowse() ) {
            return new Status.Error("Current Mode is Invalid for Showing Tour Details");
        }        
        
        return new Status.Error("unimplemented");
    }
  
    @Override
    public Status showToursOverview() {
        if ( !ModeEnum.isBrowse() ) {
            return new Status.Error("Current Mode is Invalid for Showing Tours Overview");
        }
        return new Status.Error("unimplemented");
    }

    //--------------------------
    // Follow tour mode
    //--------------------------
    
    @Override
    public Status followTour(String id) {
        if ( !ModeEnum.isBrowse() ) {
            return new Status.Error("Current Mode is Invalid to Start Following tour");
        }
        
        ModeEnum.setMode(Mode.FOLLOW);
        
        return new Status.Error("unimplemented");
    }

    @Override
    public Status endSelectedTour() {
        if ( !ModeEnum.isFollow() ) {
            return new Status.Error("Current Mode is Invalid to End Tour");
        }
        
        return new Status.Error("unimplemented");
    }

    //--------------------------
    // Multi-mode methods
    //--------------------------
    @Override
    public void setLocation(double easting, double northing) {
        currentLocation = new Location(easting, northing);
    }

    @Override
    public List<Chunk> getOutput() {
        return new ArrayList<Chunk>();
    }


}
