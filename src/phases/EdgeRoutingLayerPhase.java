package phases;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

import helper.EdgeRoutingProvider;

public class EdgeRoutingLayerPhase implements LayerPhase {

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) {
        EdgeRoutingProvider.straightEdgeRouting(layoutGraph, monitor);
    }

}
