package layeredLayouting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import helper.EdgeRoutingProvider;
import layeredLayouting.options.LayeredLayoutingOptions;
import phases.CrossingMinimizationLayerPhase;
import phases.CycleBreakingLayerPhase;
import phases.EdgeRoutingLayerPhase;
import phases.LayerAssignmentLayerPhase;
import phases.LayerPhase;
import phases.NodePlacementLayerPhase;
import properties.NodeProperty;

/**
 * A simple layout algorithm class. This algorithm already supports a number of layout options, places nodes, and
 * routes edges.
 */
public class LayeredLayoutingLayoutProvider extends AbstractLayoutProvider {
    
    LayerPhase[] phases = new LayerPhase[] { 
            new CycleBreakingLayerPhase(), 
            new LayerAssignmentLayerPhase(),
            new CrossingMinimizationLayerPhase(),
            new NodePlacementLayerPhase(),
            new EdgeRoutingLayerPhase(),
            };

    @Override
    public void layout(ElkNode layoutGraph, IElkProgressMonitor progressMonitor) {
        // Start progress monitor
        progressMonitor.begin("LayeredLayouting", phases.length);
        progressMonitor.log("Algorithm began");
        
        // Apply all phases
        for (LayerPhase p : phases) {
            // Create a sub monitor
            IElkProgressMonitor monitor = progressMonitor.subTask(1);
            monitor.begin(p.getClass().getName(), 1);
            monitor.logGraph(layoutGraph, "Start: " + p.getClass().getName());
            
            try {
                p.apply(layoutGraph, monitor);
            } catch (Exception e) {
                progressMonitor.log(p.getClass().getName() + " had an error!");
                e.printStackTrace();
                break;
            }
            
            // Close the sub monitor
            monitor.done();
            progressMonitor.log(p.getClass().getName() + " done!");
        }
        
        // Enforce padding and set the size of the final diagram dynamically
        var nodes = layoutGraph.getChildren();
        var padding = layoutGraph.getProperty(LayeredLayoutingOptions.PADDING);
        for (var n : layoutGraph.getChildren()) {
            n.setX(n.getX() + padding.left);
            n.setY(n.getY() + padding.top);
        }
        layoutGraph.setWidth(nodes.stream().map(x -> x.getX()).max(Double::compare).get() + 
                padding.left + padding.right);
        layoutGraph.setWidth(nodes.stream().map(y -> y.getY()).max(Double::compare).get() + 
                padding.top + padding.bottom);
        
        // End the progress monitor
        progressMonitor.log("Algorithm executed");
        progressMonitor.logGraph(layoutGraph, "Final graph");
        progressMonitor.done();
    }
}
