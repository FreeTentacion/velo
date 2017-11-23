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
    private int currentStep;
    private boolean browseDetails;
    private String browseDetailsTourId;
    private Tour t;
    private ArrayList<Waypoint> waypoints;

    private String startBanner(String messageName) {
        return  LS 
                + "-------------------------------------------------------------" + LS
                + "MESSAGE: " + messageName + LS
                + "-------------------------------------------------------------";
    }
    
    public ControllerImp(double waypointRadius, double waypointSeparation) {
    }
    
    public Location getcurrentlocation() {
        return currentLocation;
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
        currentStep = 0;
        
        return new Status.Error("unimplemented");
    }

    @Override
    public Status addWaypoint(Annotation annotation) {
        logger.fine(startBanner("addWaypoint"));
        
        if ( !ModeEnum.isCreate() ) {
            return new Status.Error("Current Mode is Invalid for Adding Waypoint.");
        }
        
       /* Waypoint wp = new Waypoint(annotation, currentLocation);
        t.addWaypoint(wp.near(currentLocation));
        */
        
        // the following code checks that the user is outside of the previous waypoint radius before creating a new one 
        //ControllerImp
        
        waypoints[currentStep];
        Displacement disone = new Displacement(east, north);
        if (disone.distance())
            
        // then call the method in the Tour class
        
        
        return new Status.Error("unimplemented");
    }

    @Override
    public Status addLeg(Annotation annotation) {
        logger.fine(startBanner("addLeg"));
        
        if ( !ModeEnum.isCreate() ) {
            return new Status.Error("Current Mode is Invaild for Adding Leg");
        }
                
        return new Status.Error("unimplemented");
    }

    @Override
    public Status endNewTour() {
        logger.fine(startBanner("endNewTour"));
        
        
        if ( !ModeEnum.isCreate() ) {
            return new Status.Error("Current Mode is Invalid for Ending Tour");
        }
        
        ModeEnum.setMode(Mode.BROWSE);
        
        return new Status.Error("unimplemented");
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
