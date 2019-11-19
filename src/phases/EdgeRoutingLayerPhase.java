package phases;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import helper.EdgeRoutingProvider;
import layeredLayouting.options.LayeredLayoutingOptions;

public class EdgeRoutingLayerPhase implements LayerPhase {

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) {
        double nodeNodeSpacing = layoutGraph.getProperty(LayeredLayoutingOptions.SPACING_NODE_NODE);
        double edgeNodeSpacing = layoutGraph.getProperty(LayeredLayoutingOptions.SPACING_EDGE_NODE);
        double layerSpacing = layoutGraph.getProperty(LayeredLayoutingOptions.LAYER_SPACING);
        
        for (ElkEdge edge : layoutGraph.getContainedEdges()) {
            ElkNode source = ElkGraphUtil.connectableShapeToNode(edge.getSources().get(0));
            ElkNode target = ElkGraphUtil.connectableShapeToNode(edge.getTargets().get(0));
            
            ElkEdgeSection section = ElkGraphUtil.firstEdgeSection(edge, true, true);
            
            section.setStartLocation(
                    source.getX() + source.getWidth(),
                    source.getY() + source.getHeight() / 2);
            section.setEndLocation(
                    target.getX(),
                    target.getY() + target.getHeight() / 2);
            
            ElkGraphUtil.createBendPoint(section, 
                    source.getX() + source.getWidth() + edgeNodeSpacing, 
                    source.getY() + source.getHeight() / 2);
            ElkGraphUtil.createBendPoint(section, 
                    target.getX() - edgeNodeSpacing,
                    target.getY() + target.getHeight() / 2);
            
            monitor.logGraph(layoutGraph, source.getIdentifier() + " -> " + target.getIdentifier());
        }
    }
}
