package phases;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.impl.ElkNodeImpl;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import helper.Help;
import helper.LongEdge;
import properties.NodeProperty;

public class LayerAssignmentLayerPhase implements LayerPhase {

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        basic(layoutGraph, monitor);
    }
    
    public void basic(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        var nodes = layoutGraph.getChildren();
        
        // topological sort
        List<ElkNode> zero = nodes.stream().
                filter(x -> x.getIncomingEdges().size() == 0).collect(Collectors.toList());
        for (var n : zero) {
            Help.getProp(n).layer = 0;
            topoSort(layoutGraph, n);
        }
        
        // sort nodes into graph layers
        var layers = Help.getGraphProp(layoutGraph).layers;
        int layerCount = nodes.stream().
                map(x -> x.getProperty(new Property<NodeProperty>("prop")).layer).
                max(Integer::compare).get() + 1;
        
        for (int i = 0; i < layerCount; i++)
            layers.add(new ArrayList<ElkNode>());
        
        for (var n : nodes) {
            if (Help.getProp(n).layer == -1)
                throw new Exception(n.toString());
            
            layers.get(Help.getProp(n).layer).add(n);
        }
        
        addDummyNodes(layoutGraph, monitor);
    }
    
    public void topoSort(ElkNode layoutGraph, ElkNode src) {
        for (var e : src.getOutgoingEdges()) {
            for (var n : e.getTargets()) {
                if (Help.getProp((ElkNode)n).layer < Help.getProp(src).layer + 1)
                    Help.getProp((ElkNode)n).layer = Help.getProp(src).layer + 1;
                
                topoSort(layoutGraph, (ElkNode)n);
            }
        }
    }
    
    public void addDummyNodes(ElkNode layoutGraph, IElkProgressMonitor monitor) {
        // assume we got no hyperedges for once
        
        var layers = Help.getGraphProp(layoutGraph).layers;
        var edges = layoutGraph.getContainedEdges();
        var nodes = layoutGraph.getChildren();
        
        for (int e = 0; e < edges.size(); e++) {
            var start = (ElkNode)edges.get(e).getSources().get(0);
            var end = (ElkNode)edges.get(e).getTargets().get(0);
            if (!Help.getProp(end).isDummy && !Help.getProp(start).isDummy && 
                    Help.getProp(end).layer - Help.getProp(start).layer > 1) {
                var curEdge = edges.get(e);

                monitor.logGraph(layoutGraph, "Edge " + curEdge.getSources().stream().
                        map(x -> x.getIdentifier()).
                        reduce((x,y) -> x + "," + y).get() + " -> " + 
                        curEdge.getTargets().stream().
                        map(x -> x.getIdentifier()).
                        reduce((x,y) -> x + "," + y).get() + " is too long!");
                
                List<ElkNode> dummies = new ArrayList<ElkNode>();
                List<ElkEdge> dummyEdges = new ArrayList<ElkEdge>();
                for (int i = Help.getProp(start).layer + 1; i < Help.getProp(end).layer; i++) {
                    var dummyNode = ElkGraphUtil.createNode(layoutGraph);
                    var toDummy = ElkGraphUtil.createEdge(layoutGraph);
                    
                    dummyNode.setWidth(start.getWidth());
                    dummyNode.setHeight(start.getHeight());
                    dummyNode.setIdentifier("Dummy_" + start.getIdentifier() + "_" + end.getIdentifier() + "_" + i);
                    
                    if (dummies.size() == 0)
                        toDummy.getSources().addAll(curEdge.getSources());
                    else
                        toDummy.getSources().add(dummies.get(dummies.size() - 1));
                    toDummy.getTargets().add(dummyNode);
                    
                    layers.get(i).add(0, dummyNode);
                    Help.getProp(dummyNode).layer = i;
                    
                    Help.getProp(dummyNode).isDummy = true;
                    Help.getProp(toDummy).isDummy = true;
                    
                    dummyEdges.add(toDummy);
                    dummies.add(dummyNode);
                    
                    nodes.add(dummyNode);
                    edges.add(toDummy);
                    
                    monitor.logGraph(layoutGraph, "Added " + dummyNode.getIdentifier());
                }
                
                var fromDumies = ElkGraphUtil.createEdge(layoutGraph);
                fromDumies.getSources().add(dummies.get(dummies.size() - 1));
                fromDumies.getTargets().addAll(curEdge.getTargets());
                Help.getProp(fromDumies).isDummy = true;
                dummyEdges.add(fromDumies);
                
                LongEdge le = new LongEdge();
                le.dummyEdges = dummyEdges;
                le.dummyNodes = dummies;
                le.e = curEdge;
                Help.getGraphProp(layoutGraph).longEdges.add(le);
                
                Help.getProp(curEdge).parent = le;
            }
        }
    }
}
