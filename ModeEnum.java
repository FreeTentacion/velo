package tourguide;

public class ModeEnum {
    
    public enum Mode { BROWSE, FOLLOW, CREATE; }
    
    private Mode name;
    
    public ModeEnum(Mode name) {
        this.name = name;
    }
    
}