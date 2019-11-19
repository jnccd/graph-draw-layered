package layeredLayouting.options;

import layeredLayouting.LayeredLayoutingLayoutProvider;
import layeredLayouting.LayeredLayoutingMetadataProvider;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class LayeredLayoutingOptions implements ILayoutMetaDataProvider {
  /**
   * The id of the LayeredLayouting algorithm.
   */
  public static final String ALGORITHM_ID = "layeredLayouting.LayeredLayouting";
  
  /**
   * True if nodes should be placed in reverse order of their
   * appearance in the graph.
   */
  public static final IProperty<Boolean> REVERSE_INPUT = LayeredLayoutingMetadataProvider.REVERSE_INPUT;
  
  /**
   * The spacing between layers.
   */
  public static final IProperty<Integer> LAYER_SPACING = LayeredLayoutingMetadataProvider.LAYER_SPACING;
  
  /**
   * Default value for {@link #PADDING} with algorithm "LayeredLayouting".
   */
  private static final ElkPadding PADDING_DEFAULT = new ElkPadding(10);
  
  /**
   * The padding to be left to a parent element's border when placing child elements. This can
   * also serve as an output option of a layout algorithm if node size calculation is setup
   * appropriately.
   */
  public static final IProperty<ElkPadding> PADDING = new Property<ElkPadding>(
                                CoreOptions.PADDING,
                                PADDING_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_EDGE_EDGE} with algorithm "LayeredLayouting".
   */
  private static final double SPACING_EDGE_EDGE_DEFAULT = 5;
  
  /**
   * Spacing to be preserved between any two edges. Note that while this can somewhat easily be satisfied
   * for the segments of orthogonally drawn edges, it is harder for general polylines or splines.
   */
  public static final IProperty<Double> SPACING_EDGE_EDGE = new Property<Double>(
                                CoreOptions.SPACING_EDGE_EDGE,
                                SPACING_EDGE_EDGE_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_EDGE_NODE} with algorithm "LayeredLayouting".
   */
  private static final double SPACING_EDGE_NODE_DEFAULT = 5;
  
  /**
   * Spacing to be preserved between nodes and edges.
   */
  public static final IProperty<Double> SPACING_EDGE_NODE = new Property<Double>(
                                CoreOptions.SPACING_EDGE_NODE,
                                SPACING_EDGE_NODE_DEFAULT);
  
  /**
   * Default value for {@link #SPACING_NODE_NODE} with algorithm "LayeredLayouting".
   */
  private static final double SPACING_NODE_NODE_DEFAULT = 10;
  
  /**
   * The minimal distance to be preserved between each two nodes.
   */
  public static final IProperty<Double> SPACING_NODE_NODE = new Property<Double>(
                                CoreOptions.SPACING_NODE_NODE,
                                SPACING_NODE_NODE_DEFAULT);
  
  /**
   * Layouter-specific algorithm factory.
   */
  public static class LayeredLayoutingFactory implements org.eclipse.elk.core.util.IFactory<AbstractLayoutProvider> {
    public AbstractLayoutProvider create() {
      AbstractLayoutProvider provider = new LayeredLayoutingLayoutProvider();
      provider.initialize("");
      return provider;
    }
    
    public void destroy(final AbstractLayoutProvider obj) {
      obj.dispose();
    }
  }
  
  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutAlgorithmData.Builder()
        .id("layeredLayouting.LayeredLayouting")
        .name("LayeredLayouting")
        .description("Nice layered algorithm")
        .providerFactory(new LayeredLayoutingFactory())
        .melkBundleName(null)
        .definingBundleId("layeredLayouting")
        .create()
    );
    registry.addOptionSupport(
        "layeredLayouting.LayeredLayouting",
        "layeredLayouting.reverseInput",
        REVERSE_INPUT.getDefault()
    );
    registry.addOptionSupport(
        "layeredLayouting.LayeredLayouting",
        "layeredLayouting.layerSpacing",
        LAYER_SPACING.getDefault()
    );
    registry.addOptionSupport(
        "layeredLayouting.LayeredLayouting",
        "org.eclipse.elk.padding",
        PADDING_DEFAULT
    );
    registry.addOptionSupport(
        "layeredLayouting.LayeredLayouting",
        "org.eclipse.elk.spacing.edgeEdge",
        SPACING_EDGE_EDGE_DEFAULT
    );
    registry.addOptionSupport(
        "layeredLayouting.LayeredLayouting",
        "org.eclipse.elk.spacing.edgeNode",
        SPACING_EDGE_NODE_DEFAULT
    );
    registry.addOptionSupport(
        "layeredLayouting.LayeredLayouting",
        "org.eclipse.elk.spacing.nodeNode",
        SPACING_NODE_NODE_DEFAULT
    );
  }
}
