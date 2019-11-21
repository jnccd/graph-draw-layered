package phases;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.Property;

import helper.Help;
import layeredLayouting.options.LayeredLayoutingOptions;
import properties.NodeProperty;

public class NodePlacementLayerPhase implements LayerPhase {

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        double nodeNodeSpacing = layoutGraph.getProperty(LayeredLayoutingOptions.SPACING_NODE_NODE);
        double edgeNodeSpacing = layoutGraph.getProperty(LayeredLayoutingOptions.SPACING_EDGE_NODE);
        double layerSpacing = layoutGraph.getProperty(LayeredLayoutingOptions.LAYER_SPACING);
        
        var nodes = layoutGraph.getChildren();
        var layers = Help.getGraphProp(layoutGraph).layers;
        
        double curX = 0, curY = 0;
        for (var l : layers)
        {
            double stackWidth = l.stream().map(x -> x.getWidth()).max(Double::compare).get();
            curY = 0;
            for (var n : l)
            {
                n.setX(curX);
                n.setY(curY);
                
                curY += n.getHeight() + nodeNodeSpacing;
                
                monitor.logGraph(layoutGraph, "Placed " + n.getIdentifier());
            }
            curX += stackWidth + nodeNodeSpacing + layerSpacing;
        }
    }

}
