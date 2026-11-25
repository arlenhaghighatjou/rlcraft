package ivorius.reccomplex.gui.editstructure.transformers;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerVillageSpecific;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBTBiomeSpecific extends TableDataSourceSegmented {
   private TransformerVillageSpecific transformer;
   private TableNavigator navigator;
   private TableDelegate tableDelegate;

   public TableDataSourceBTBiomeSpecific(TransformerVillageSpecific transformer, TableNavigator navigator, TableDelegate delegate) {
      this.transformer = transformer;
      this.navigator = navigator;
      this.tableDelegate = delegate;
      this.addSegment(0, new TableDataSourceTransformer(transformer, delegate, navigator));
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.sources"), IvTranslations.getLines("reccomplex.transformer.block.source.tooltip"), transformer.sourceMatcher, (Object)null));
   }

   public TransformerVillageSpecific getTransformer() {
      return this.transformer;
   }

   public void setTransformer(TransformerVillageSpecific transformer) {
      this.transformer = transformer;
   }
}
