package phases;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.Property;
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
}
