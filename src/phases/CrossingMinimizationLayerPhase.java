package phases;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkShape;

import helper.Help;

public class CrossingMinimizationLayerPhase implements LayerPhase {

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) {
        basic(layoutGraph, monitor);
    }
    
    public void basic(ElkNode layoutGraph, IElkProgressMonitor monitor) { 
        var nodes = layoutGraph.getChildren();
        var layers = Help.getGraphProp(layoutGraph).layers;
        var edges = layoutGraph.getContainedEdges();
        
        for (int l = 1; l < layers.size(); l++) {
            var curLay = layers.get(l);
            for (var n : curLay) {
                List<ElkEdge> incoming = edges.stream().filter(x -> x.getTargets().contains(n)).collect(Collectors.toList());
                List<ElkShape> s = new ArrayList<ElkShape>();
                final int fl = l;
                for (var in : incoming) {
                    s.addAll(in.getSources().stream().filter(x -> Help.getProp((ElkNode)x).layer == fl - 1).
                            collect(Collectors.toList()));
                }
                s = s.stream().distinct().collect(Collectors.toList());
                
                Help.getProp(n).barycenterVal = s.stream().map(x -> layers.get(fl - 1).indexOf(x)).
                        reduce(0, (a, b) -> a + b) / s.size();
            }
        }
        
        for (ArrayList<ElkNode> l : layers) {
            l.sort((x, y) -> Double.compare(Help.getProp(x).barycenterVal, Help.getProp(y).barycenterVal));
        }
    }
}
