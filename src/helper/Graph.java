package helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.graph.ElkNode;

public class Graph {
    public List<Node> nodes = new ArrayList<Node>();
    public List<Edge> edges = new ArrayList<Edge>();
    
    public void removeNode(Node n) {
        nodes.remove(n);
        for (int i = 0; i < edges.size(); i++) {
            edges.get(i).sources.remove(n);
            if (edges.get(i).sources.size() == 0) {
                for (var n2 : nodes)
                    n2.incoming.remove(edges.get(i));
                edges.remove(i);
            }
        }
    }
    
    public static Graph fromElk(ElkNode graph) {
        Graph g = new Graph();
        
        for (var n : graph.getChildren())
            g.nodes.add(new Node(new ArrayList<Edge>(), new ArrayList<Edge>(), n));
        
        for (var e : graph.getContainedEdges())
            g.edges.add(new Edge(
                    g.nodes.stream().filter(x -> e.getSources().contains(x.parent)).
                    collect(Collectors.toList()), 
                    g.nodes.stream().filter(x -> e.getTargets().contains(x.parent)).
                    collect(Collectors.toList()), e));
        
        for (var n : g.nodes) {
            n.incoming = g.edges.stream().filter(x -> x.targets.contains(n)).
                    collect(Collectors.toList());
            n.outgoing = g.edges.stream().filter(x -> x.sources.contains(n)).
                    collect(Collectors.toList());
        }
        
        return g;
    }
}
