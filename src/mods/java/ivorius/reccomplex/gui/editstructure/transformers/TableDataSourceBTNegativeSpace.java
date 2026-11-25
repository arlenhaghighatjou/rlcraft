package ivorius.reccomplex.gui.editstructure.transformers;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerNegativeSpace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBTNegativeSpace extends TableDataSourceSegmented {
   private TransformerNegativeSpace transformer;

   public TableDataSourceBTNegativeSpace(TransformerNegativeSpace transformer, TableNavigator navigator, TableDelegate delegate) {
      this.transformer = transformer;
      this.addSegment(0, new TableDataSourceTransformer(transformer, delegate, navigator));
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.sources"), IvTranslations.getLines("reccomplex.transformer.block.source.tooltip"), transformer.sourceMatcher, (Object)null));
      this.addSegment(2, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.destinations"), IvTranslations.getLines("reccomplex.transformer.block.dest.tooltip"), transformer.destMatcher, (Object)null));
   }

   public TransformerNegativeSpace getTransformer() {
      return this.transformer;
   }

   public void setTransformer(TransformerNegativeSpace transformer) {
      this.transformer = transformer;
   }
}
