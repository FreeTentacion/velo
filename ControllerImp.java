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
    private double waypointRadius;
    private double waypointSeparation;
    private int legcounter = 0;
    private boolean lastinstructioncreatewaypoint = false;
    private ArrayList<Tour> tours;

    private String startBanner(String messageName) {
        return  LS 
                + "-------------------------------------------------------------" + LS
                + "MESSAGE: " + messageName + LS
                + "-------------------------------------------------------------";
    }
    
    public ControllerImp(double waypointRadius, double waypointSeparation) { // What's the difference between these two again?
        this.waypointRadius = waypointRadius;
        this.waypointSeparation = waypointSeparation;
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
        currentStage = 0;
        return Status.OK;
    }

    @Override
    public Status addWaypoint(Annotation annotation) {
        logger.fine(startBanner("addWaypoint"));
        
        if ( !ModeEnum.isCreate() ) {
            return new Status.Error("Current Mode is Invalid for Adding Waypoint.");
        }
        
        if (currentStage == 0) {
            return new Status.Error("Must add a leg first");
        }
        
        if (currentStage == 1) { // doesn't need to check if it's too close to the previous waypoint, because the previous waypoint doesn't exist.
            t.addWaypoint(currentLocation, annotation); // the add waypoint method in tour needs to update the array list in this class
            lastinstructioncreatewaypoint = true;
            return Status.OK;
        }
        // the following if statements checks two waypoints aren't being added at the same stage
        if (waypoints.size() == currentStage) {
            return new Status.Error("Already have a waypoint for this stage");         
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
        
        currentStage++;
        legcounter++;
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
            // output chunk containing title, number of legs, number of waypoints (added so far)
            Chunk.CreateHeader c = new Chunk.CreateHeader(t.getTitle(), legcounter, currentStage -1 );
            c.toString(); // outputs the chunk
            ModeEnum.setMode(Mode.BROWSE); 
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
        logger.fine(startBanner("showTourDetails"));
        if ( !ModeEnum.isBrowse() ) {
            return new Status.Error("Current Mode is Invalid for Showing Tour Details");
        }
        
        // Not using the library class
        
        
        
        for (int i = 0; i < tours.size(); i++) {
            if (tours.get(i).getId() == tourID) {
                Chunk.OverviewLine found = new Chunk.OverviewLine(tourID, tours.get(i).getTitle());
                found.toString();
                return Status.OK;
            }
        }
        
        
        return new Status.Error("No tour corresponding to the inputted tourID");
    }
  
    @Override
    public Status showToursOverview() {
        logger.fine(startBanner("showToursOverview"));
        if ( !ModeEnum.isBrowse() ) {
            return new Status.Error("Current Mode is Invalid for Showing Tours Overview");
        }
        
        Chunk.BrowseOverview returnthis = new Chunk.BrowseOverview();
        for (int i = 0 ; i < tours.size(); i++) {
            returnthis.addIdAndTitle(tours.get(i).getId(), tours.get(i).getTitle()); //adds each tour to the arraylist
        }
        
        returnthis.toString(); // prints the arraylist out
        
        return Status.OK;
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
        
        // where are we calling all of the data from if it were to exist?
        
        //for loop for each waypoint, leg combination
        //hardcode final waypoint
        // possibly calls "end selected tour" << not this
        
        
        for (int i = 0 ; i < tours.size(); i++) {
            if (id == tours.get(i).getId()) {
                t = tours.get(i);
            }
            else {
                return new Status.Error("There is no tour for this id");
            }
        }
        
        Chunk.FollowLeg zeroLeg  = new Chunk.FollowLeg(t.getLeg(0).getAnnotation());
        zeroLeg.toString(); // hardcoding the first leg
        
        // at what point do you get a message for the next leg
        
        for (int i = 1; i < t.getWaypoints().size(); i++) {
            //if within next waypoint radius then continue, otherwise take one off the counter?? 
            if (currentLocation.deltaFrom(t.getWaypoint(i).getLocation()).distance() > waypointRadius) {
                // something that holds until the user is within the next waypoint radius.
            }
            Chunk.FollowWaypoint curWayp = new Chunk.FollowWaypoint(t.getWaypoint(i).getannotation());
            curWayp.toString();
            Chunk.FollowLeg curLeg  = new Chunk.FollowLeg(t.getLeg(i).getAnnotation());
            curLeg.toString(); // what to do with these strings once we get them here
        }
        
        Chunk.FollowWaypoint finalWayp = new Chunk.FollowWaypoint(t.getWaypoint(t.getWaypoints().size()).getannotation());
        finalWayp.toString();
        
        
        return Status.OK;
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
