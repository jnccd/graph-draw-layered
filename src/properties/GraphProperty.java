package properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.graph.ElkNode;

import helper.LongEdge;

public class GraphProperty {
    public ArrayList<ArrayList<ElkNode>> layers = new ArrayList<ArrayList<ElkNode>>();
    public List<LongEdge> longEdges = new ArrayList<LongEdge>();
}
