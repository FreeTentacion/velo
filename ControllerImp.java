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
    private int followStage = 0;
    private boolean browseDetails;
    private Tour tour;
    private Tour selectedTour;
    private Waypoint currentWaypoint;
    private double waypointRadius;
    private double waypointSeparation;
    private boolean prevAdditionWasWayp = false;
    private ArrayList<Tour> tours = new ArrayList<Tour>();
    private Mode mode = Mode.BROWSE;
    int x = 0;

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
        
        tour  = new Tour(id, title, annotation);
        tours.add(tour);
        currentStage = 0;
        return Status.OK;
    }

    @Override
    public Status addWaypoint(Annotation annotation) {
        logger.fine(startBanner("addWaypoint"));
        
        if ( mode != Mode.CREATE ) {
            return new Status.Error("Current Mode is Invalid for Adding Waypoint.");
        }
        if (currentStage == 0 || currentStage == tour.getWaypoints().size()) {
            tour.addLeg(Annotation.DEFAULT);
            currentStage++;
        }
        if (currentStage == 1) { // doesn't need to check if it's too close to the previous waypoint, because the previous waypoint doesn't exist.
            tour.addWaypoint(currentLocation, annotation); 
            prevAdditionWasWayp = true;
            return Status.OK;
        }
        // the following if statements checks two waypoints aren't being added at the same stage
        if (tour.getWaypoints().size() == currentStage) {
            return new Status.Error("Already have a waypoint for this stage");         
        }
        // the following code checks that the user is outside of the previous waypoint radius before creating a new one 
        
        currentWaypoint = tour.getWaypoints().get(currentStage - 2); //
        
        Location l = currentWaypoint.getLocation();
        double wapnorth = l.getNorthing();
        double wapeast = l.getEasting();
        double cureast = currentLocation.getEasting();
        double curnorth = currentLocation.getNorthing();
        double separationDistance = Math.sqrt(((wapnorth-curnorth)*(wapnorth-curnorth))+((wapeast-cureast)*(wapeast-cureast)));
        if (separationDistance < waypointSeparation ) {
            return new Status.Error("Too close to insert a new Waypoint."); 
        }
        else {
            tour.addWaypoint(currentLocation, annotation);
            
            
        }
        prevAdditionWasWayp = true;
        return Status.OK;
    }

    @Override
    public Status addLeg(Annotation annotation) {
        logger.fine(startBanner("addLeg"));
        
        if ( mode != Mode.CREATE ) {
            return new Status.Error("Current Mode is Invaild for Adding Leg");
        }
        
      
        tour.addLeg(annotation);
        
        currentStage++;
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
                selectedTour = tours.get(i);
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
            return new Status.Error("Current Mode is Invalid to Start Following tour");
        }
        
        mode = Mode.FOLLOW;
        currentStage = 0;
        
        boolean ifFound = false;
        for (int i = 0 ; i < tours.size(); i++) {
            if (id == tours.get(i).getId()) {
                tour = tours.get(i);
                ifFound = true;
            }
        }
        if (!ifFound) {
            return new Status.Error("There is no tour for this id");
        }
        return Status.OK;
    }

    @Override
    public Status endSelectedTour() {
        logger.fine(startBanner("endSelectedTour"));
        if ( mode != Mode.FOLLOW ) {
            return new Status.Error("Current Mode is Invalid to End Tour");
        }
        followStage = 0;
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
            Chunk.CreateHeader createOut = new Chunk.CreateHeader(tour.getTitle(), tour.getLegs().size(), tour.getWaypoints().size());
            output.add(createOut);
        }
        else if (mode == Mode.FOLLOW) {
            boolean isWithinRadius = currentLocation.deltaFrom(tour.getWaypoint(followStage).getLocation()).distance() <= waypointRadius;
            if (tour.getWaypoints().size() == followStage + 1 && isWithinRadius) {
                Chunk.FollowHeader lastWaypHeader = new Chunk.FollowHeader(tour.getTitle(), followStage + 1, tour.getWaypoints().size());
                output.add(lastWaypHeader);
                Chunk.FollowWaypoint finalWayp = new Chunk.FollowWaypoint(tour.getWaypoint(followStage).getannotation());
                output.add(finalWayp);
            }
            else if (isWithinRadius) { 
                if (tour.getWaypoints().size() == followStage + 1) {
                    
                }
                double bearing = tour.getWaypoint(followStage + 1).getLocation().deltaFrom(currentLocation).bearing();
                double distance = tour.getWaypoint(followStage + 1).getLocation().deltaFrom(currentLocation).distance();
                Chunk.FollowHeader followOut = new Chunk.FollowHeader(tour.getTitle(), followStage + 1, tour.getWaypoints().size());
                output.add(followOut);
                Chunk.FollowWaypoint curWayp = new Chunk.FollowWaypoint(tour.getWaypoint(followStage).getannotation());
                output.add(curWayp);
                Chunk.FollowLeg curLeg  = new Chunk.FollowLeg(tour.getLeg(followStage + 1).getAnnotation());
                output.add(curLeg);
                Chunk.FollowBearing bearingNextWayp = new Chunk.FollowBearing(bearing, distance);
                output.add(bearingNextWayp);
                if (x == 3) {
                    followStage++;
                    x = 0;
                }
                else {
                    x++;
                }
            }
            else {
                double bearing = tour.getWaypoint(followStage).getLocation().deltaFrom(currentLocation).bearing();
                double distance = tour.getWaypoint(followStage).getLocation().deltaFrom(currentLocation).distance();
                Chunk.FollowHeader followOut = new Chunk.FollowHeader(tour.getTitle(), followStage, tour.getWaypoints().size());
                output.add(followOut);
                Chunk.FollowLeg curLeg  = new Chunk.FollowLeg(tour.getLeg(followStage).getAnnotation());
                output.add(curLeg);
                Chunk.FollowBearing bearingNextWayp = new Chunk.FollowBearing(bearing, distance);
                output.add(bearingNextWayp);
            }
        }
        else if (mode == Mode.BROWSE) {
            if (browseDetails) {
                Chunk.BrowseDetails detailsOut = new Chunk.BrowseDetails(selectedTour.getId(), selectedTour.getTitle(), selectedTour.getAnnotation());
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
