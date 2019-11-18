package phases;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

public interface LayerPhase {
    void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception;
}
