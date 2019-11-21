package phases;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.impl.ElkNodeImpl;

import helper.Graph;
import helper.Help;
import helper.Node;

public class CycleBreakingLayerPhase implements LayerPhase {

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        advanced(layoutGraph, monitor); // TODO: Add advanced
    }
    
    public void advanced(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        
        Graph g = Graph.fromElk(layoutGraph);
        List<Node> sourciest = new ArrayList<Node>();
        List<Node> sinkiest = new ArrayList<Node>();

        if (hasCycle(layoutGraph))
            while (g.nodes.size() > 0) {
                var sourcy = g.nodes.stream().filter(x -> x.incoming.size() == 0).findFirst();
                if (sourcy.isPresent()) {
                    g.removeNode(sourcy.get());
                    sourciest.add(sourcy.get());
                } else {
                    var sinky = g.nodes.stream().filter(x -> x.outgoing.size() == 0).findFirst();
                    if (sinky.isPresent()) {
                        g.removeNode(sinky.get());
                        sinkiest.add(sinky.get());
                    } else {
                        sourcy = g.nodes.stream().max(
                                (x, y) -> x.outgoing.size() - x.incoming.size() - 
                                    y.outgoing.size() + y.incoming.size());
                        
                        for (var e : sourcy.get().incoming) {
                            Help.getProp(e.parent).isReversed = true;
                            reverse(e.parent);
                            
                            monitor.logGraph(layoutGraph, "Reversed Edge: " + 
                                e.parent.getSources().stream().
                                map(x -> x.getIdentifier()).
                                reduce((x,y) -> x + "," + y).get() + " -> " + 
                                e.parent.getTargets().stream().
                                map(x -> x.getIdentifier()).
                                reduce((x,y) -> x + "," + y).get());
                        }
                        
                        g.removeNode(sourcy.get());
                        sourciest.add(sourcy.get());
                    }
                }
            }
        
        if (hasCycle(layoutGraph))
            System.out.println("Did I miss anything?");
    }
    
    void basic(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        if (hasCycle(layoutGraph))
            throw new Exception("CYCLES!!!! AHHHHHHHHHHHHHHHHHHHH");
    }

    boolean hasCycle(ElkNode layoutGraph) {
        for (var node : layoutGraph.getChildren()) {
            if (!Help.getProp(node).visiting && hasCycle(layoutGraph, node)) {
                return true;
            }
        }
        return false;
    }
    
    boolean hasCycle(ElkNode layoutGraph, ElkNode sourceNode) {
        Help.getProp(sourceNode).visiting = true;

        for (var e : sourceNode.getOutgoingEdges())
            for (var n : e.getTargets())
                if (n.getClass().equals(ElkNodeImpl.class)) {
                    if (Help.getProp((ElkNode) n).visiting)
                        return true;
                    else if (Help.getProp((ElkNode) n).visited && hasCycle(layoutGraph, (ElkNode) n))
                        return true;
                }

        Help.getProp(sourceNode).visiting = false;
        Help.getProp(sourceNode).visited = true;
        return false;
    }
    
    void reverse(ElkEdge e) {
        List<ElkConnectableShape> srcs = new ArrayList<ElkConnectableShape>();
        for (var s : e.getSources())
            srcs.add(s);
        e.getSources().clear();
        
        e.getSources().addAll(e.getTargets());
        e.getTargets().clear();
        
        e.getTargets().addAll(srcs);
    }
}
