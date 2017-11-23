package tourguide;

public class ModeEnum {
    
    public enum Mode { BROWSE, FOLLOW, CREATE; }
    
    private static Mode name = Mode.BROWSE;
    
    public static Mode getMode() {
        return name;
    }
    public static void setMode(Mode n) {
        name = n;
    }
    
    public static boolean isBrowse() {
        if( getMode() == Mode.BROWSE ) {
            return true;
        }
        return false;
    }
    public static boolean isFollow() {
        if( getMode() == Mode.FOLLOW ) {
            return true;
        }
        return false;
    }
    public static boolean isCreate() {
        if( getMode() == Mode.CREATE ) {
            return true;
        }
        return false;
    }
}