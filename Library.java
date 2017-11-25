package tourguide;

import java.util.ArrayList;

public static class Library {
    
    public ArrayList<Tour> tours;
    
    //public Library(ArrayList<Tour> tours) { // is this needed?
    //    this.tours = tours;
    //}

    public void addTour(Tour tour) {
        tours.add(tour);        
    }
    
    public ArrayList<Tour> getTours() { // why can't this be called?
        return tours;
    }
    
    
}
