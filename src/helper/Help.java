package helper;

import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.Property;

import properties.EdgeProperty;
import properties.GraphProperty;
import properties.NodeProperty;

public class Help {
    public static NodeProperty getProp(ElkNode e) {
        Property<NodeProperty> prop = new Property<NodeProperty>("prop");
        
        if (e.hasProperty(prop))
            return e.getProperty(prop);
        
        e.setProperty(prop, new NodeProperty());
        return e.getProperty(prop);
    }
    public static EdgeProperty getProp(ElkEdge e) {
        Property<EdgeProperty> prop = new Property<EdgeProperty>("prop");
        
        if (e.hasProperty(prop))
            return e.getProperty(prop);
        
        e.setProperty(prop, new EdgeProperty());
        return e.getProperty(prop);
    }
    public static GraphProperty getGraphProp(ElkNode e) {
        Property<GraphProperty> prop = new Property<GraphProperty>("graph-prop");
        
        if (e.hasProperty(prop))
            return e.getProperty(prop);
        
        e.setProperty(prop, new GraphProperty());
        return e.getProperty(prop);
    }
}
