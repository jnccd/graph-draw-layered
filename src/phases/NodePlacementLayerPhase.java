package phases;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.elk.graph.util.ElkGraphUtil;

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
        var longEdges = Help.getGraphProp(layoutGraph).longEdges;
        
        for (var n : nodes) {
            Help.getProp(n).layerIndex = layers.get(Help.getProp(n).layer).indexOf(n);
        }
        
        // Keep dummy nodes of long edges in the same y coords
        boolean cleanIteration = false;
        while (!cleanIteration) {
            cleanIteration = true;
            for (var longEdge : longEdges) {
                var highest = longEdge.dummyNodes.stream().
                        map(x -> Help.getProp(x).layerIndex).
                        max(Integer::compare).get();
                
                for (var n : longEdge.dummyNodes) {
                    if (Help.getProp(n).layerIndex < highest) {
                        cleanIteration = false;
                        var dummyFillerNode = ElkGraphUtil.createNode(layoutGraph);
                        
                        dummyFillerNode.setWidth(n.getWidth());
                        dummyFillerNode.setHeight(n.getHeight());
                        dummyFillerNode.setIdentifier("Dummy_Filler_" + n.getIdentifier());
                        
                        layers.get(Help.getProp(n).layer).add(Help.getProp(n).layerIndex, dummyFillerNode);
                        
                        for (var ln : layers.get(Help.getProp(n).layer)) {
                            Help.getProp(ln).layer = Help.getProp(n).layer;
                            Help.getProp(ln).layerIndex = layers.get(Help.getProp(ln).layer).indexOf(ln);
                        }
                        
                        monitor.logGraph(layoutGraph, "Added " + n.getIdentifier());
                    }
                }
            }
        }
        
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
