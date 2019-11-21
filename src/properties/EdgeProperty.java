package properties;

import helper.LongEdge;

public class EdgeProperty {
    public boolean isReversed = false;
    public boolean isDummy = false;
    
    // layerAssignment
    public boolean visited = false;
    
    // dummyNode stuff
    public LongEdge parent = null;
}
