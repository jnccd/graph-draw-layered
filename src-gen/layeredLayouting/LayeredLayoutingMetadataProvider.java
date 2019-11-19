package layeredLayouting;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class LayeredLayoutingMetadataProvider implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #REVERSE_INPUT}.
   */
  private static final boolean REVERSE_INPUT_DEFAULT = false;
  
  /**
   * True if nodes should be placed in reverse order of their
   * appearance in the graph.
   */
  public static final IProperty<Boolean> REVERSE_INPUT = new Property<Boolean>(
            "layeredLayouting.reverseInput",
            REVERSE_INPUT_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #LAYER_SPACING}.
   */
  private static final int LAYER_SPACING_DEFAULT = 25;
  
  /**
   * The spacing between layers.
   */
  public static final IProperty<Integer> LAYER_SPACING = new Property<Integer>(
            "layeredLayouting.layerSpacing",
            LAYER_SPACING_DEFAULT,
            null,
            null);
  
  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("layeredLayouting.reverseInput")
        .group("")
        .name("Reverse Input")
        .description("True if nodes should be placed in reverse order of their appearance in the graph.")
        .defaultValue(REVERSE_INPUT_DEFAULT)
        .type(LayoutOptionData.Type.BOOLEAN)
        .optionClass(Boolean.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    registry.register(new LayoutOptionData.Builder()
        .id("layeredLayouting.layerSpacing")
        .group("")
        .name("Layer Spacing")
        .description("The spacing between layers.")
        .defaultValue(LAYER_SPACING_DEFAULT)
        .type(LayoutOptionData.Type.INT)
        .optionClass(Integer.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS))
        .visibility(LayoutOptionData.Visibility.VISIBLE)
        .create()
    );
    new layeredLayouting.options.LayeredLayoutingOptions().apply(registry);
  }
}
