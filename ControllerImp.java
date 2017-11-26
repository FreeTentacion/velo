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
    
    private Location currentLocation;
    private int currentStage;
    private boolean browseDetails;
    private Tour t;
    private Waypoint currentWaypoint;
    private double waypointRadius;
    private double waypointSeparation;
    private int legCounter = 0;
    private int waypointCounter = 0;
    private boolean prevAdditionWasWayp = false;
    private ArrayList<Tour> tours = new ArrayList<Tour>();
    private boolean isLeg = true;
    private Mode mode = Mode.BROWSE;

    private String startBanner(String messageName) {
        return  LS 
                + "-------------------------------------------------------------" + LS
                + "MESSAGE: " + messageName + LS
                + "-------------------------------------------------------------";
    }
    
    public ControllerImp(double waypointRadius, double waypointSeparation) { 
        this.waypointRadius = waypointRadius;
        this.waypointSeparation = waypointSeparation;
    }
        
    //--------------------------
    // Create tour mode
    //--------------------------

    // Some examples are shown below of use of logger calls.  The rest of the methods below that correspond 
    // to input messages could do with similar calls.
    
    @Override
    public Status startNewTour(String id, String title, Annotation annotation) {
        logger.fine(startBanner("startNewTour"));
        
        if ( mode != Mode.BROWSE ) {
            return new Status.Error("Current Mode is Invalid to Start New Tour.");
        }
        
        mode = Mode.CREATE;
        
        t  = new Tour(id, title, annotation);
        tours.add(t);
        currentStage = 0;
        return Status.OK;
    }

    @Override
    public Status addWaypoint(Annotation annotation) {
        logger.fine(startBanner("addWaypoint"));
        
        if ( mode != Mode.CREATE ) {
            return new Status.Error("Current Mode is Invalid for Adding Waypoint.");
        }
        if (legCounter != currentStage) {
            t.addLeg(Annotation.DEFAULT);
            currentStage++;
            legCounter++;
        }
        if (currentStage == 1) { // doesn't need to check if it's too close to the previous waypoint, because the previous waypoint doesn't exist.
            t.addWaypoint(currentLocation, annotation); 
            prevAdditionWasWayp = true;
            waypointCounter++;
            return Status.OK;
        }
        // the following if statements checks two waypoints aren't being added at the same stage
        if (t.getWaypoints().size() == currentStage) {
            return new Status.Error("Already have a waypoint for this stage");         
        }
        // the following code checks that the user is outside of the previous waypoint radius before creating a new one 
        
        currentWaypoint = t.getWaypoints().get(currentStage - 3); //
        
        Location l = currentWaypoint.getLocation();
        double wapnorth = l.getnorthing();
        double wapeast = l.geteasting();
        double cureast = currentLocation.geteasting();
        double curnorth = currentLocation.getnorthing();
        double separationDistance = Math.sqrt(((wapnorth-curnorth)*(wapnorth-curnorth))+((wapeast-cureast)*(wapeast-cureast)));
        if (separationDistance < waypointSeparation ) {
            return new Status.Error("Too close to insert a new Waypoint."); 
        }
        else {
            t.addWaypoint(currentLocation, annotation);
            
            
        }
        waypointCounter++;
        prevAdditionWasWayp = true;
        return Status.OK;
    }

    @Override
    public Status addLeg(Annotation annotation) {
        logger.fine(startBanner("addLeg"));
        
        if ( mode != Mode.CREATE ) {
            return new Status.Error("Current Mode is Invaild for Adding Leg");
        }
        
      
        t.addLeg(annotation);
        
        currentStage++;
        legCounter++;
        prevAdditionWasWayp = false;
        return Status.OK;
    }

    @Override
    public Status endNewTour() {
        logger.fine(startBanner("endNewTour"));
        
        
        if ( mode != Mode.CREATE ) {
            return new Status.Error("Current Mode is Invalid for Ending Tour");
        }
        
        if (prevAdditionWasWayp) {
            mode = Mode.BROWSE; 
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
        if ( mode != Mode.BROWSE ) {
            return new Status.Error("Current Mode is Invalid for Showing Tour Details");
        }
        for (int i = 0; i < tours.size(); i++) {
            if (tours.get(i).getId() == tourID) {
                browseDetails = true;
                return Status.OK;
            }
        }
        return new Status.Error("No tour corresponding to the inputted tourID");
    }
  
    @Override
    public Status showToursOverview() {
        logger.fine(startBanner("showToursOverview"));
        if ( mode != Mode.BROWSE ) {
            return new Status.Error("Current Mode is Invalid for Showing Tours Overview");
        }
        browseDetails = false;
        return Status.OK;
    }

    //--------------------------
    // Follow tour mode
    //--------------------------
    
    @Override
    public Status followTour(String id) {
        logger.fine(startBanner("followTour"));
        if ( mode != Mode.BROWSE ) {        
            Chunk.FollowWaypoint finalWayp = new Chunk.FollowWaypoint(t.getWaypoint(t.getWaypoints().size()).getannotation());
            finalWayp.toString();
            
            
            return new Status.Error("Current Mode is Invalid to Start Following tour");
        }
        
        mode = Mode.FOLLOW;
        
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
        
        // at what point do you get a message for the next leg
        
        for (int i = 1; i < t.getWaypoints().size(); i++) {
            //if within next waypoint radius then continue, otherwise take one off the counter?? 
            while (currentLocation.deltaFrom(t.getWaypoint(i).getLocation()).distance() > waypointRadius) {
                // do nothing
            }
            isLeg = false;
            
            while (currentLocation.deltaFrom(t.getWaypoint(i).getLocation()).distance() < waypointRadius) {
                // do nothing
            }
            isLeg = true;
        }
        return Status.OK;
    }

    @Override
    public Status endSelectedTour() {
        logger.fine(startBanner("endSelectedTour"));
        if ( mode != Mode.FOLLOW ) {
            return new Status.Error("Current Mode is Invalid to End Tour");
        }
        
        mode = Mode.BROWSE;
        browseDetails = false;
        return Status.OK;
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
        ArrayList<Chunk> output = new ArrayList<Chunk>();
        if (mode == Mode.CREATE) {
            Chunk.CreateHeader createOut = new Chunk.CreateHeader(t.getTitle(), t.getLegs().size(), t.getWaypoints().size());
            output.add(createOut);
        }
        else if (mode == Mode.FOLLOW) {
            Chunk.FollowHeader followOut = new Chunk.FollowHeader(t.getTitle(), currentStage, t.getWaypoints().size());
            
            if (isLeg) {
                Chunk.FollowLeg curLeg  = new Chunk.FollowLeg(t.getLeg(currentStage).getAnnotation());
                output.add(curLeg);
            }
            else {
                Chunk.FollowWaypoint curWayp = new Chunk.FollowWaypoint(t.getWaypoint(currentStage).getannotation());
                output.add(curWayp);
            }
            output.add(followOut);
        }
        else if (mode == Mode.BROWSE) {
            if (browseDetails) {
                Chunk.BrowseDetails detailsOut = new Chunk.BrowseDetails(t.getId(), t.getTitle(), t.getAnnotation());
                output.add(detailsOut);
            }
            else {
                Chunk.BrowseOverview overviewOut = new Chunk.BrowseOverview();
                for (int i = 0; i < tours.size(); i++) {
                    overviewOut.addIdAndTitle(tours.get(i).getId(), tours.get(i).getTitle());
                }
                output.add(overviewOut);
            }
        }
        return output;
    }
}
