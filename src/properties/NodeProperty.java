package properties;

public class NodeProperty {
    public boolean isDummy = false;
    public int layer = -1;
    public int layerIndex = -1;
    
    // cycleBreaking
    public boolean visiting = false;
    public boolean visited = false;
    
    // crossMin
    public double barycenterVal;
}
