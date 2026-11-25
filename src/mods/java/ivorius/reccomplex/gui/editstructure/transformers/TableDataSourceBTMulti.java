package ivorius.reccomplex.gui.editstructure.transformers;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.editstructure.TableDataSourceTransformerList;
import ivorius.reccomplex.gui.editstructure.preset.TableDataSourcePresettedObject;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.Transformer;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerMulti;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBTMulti extends TableDataSourceSegmented {
   private TransformerMulti transformer;
   private TableNavigator navigator;
   private TableDelegate delegate;

   public TableDataSourceBTMulti(TransformerMulti transformer, TableNavigator navigator, TableDelegate delegate) {
      this.transformer = transformer;
      this.navigator = navigator;
      this.delegate = delegate;
      this.addSegment(0, new TableDataSourceTransformer(transformer, delegate, navigator));
      this.addSegment(1, (new TableDataSourcePresettedObject(transformer.getData(), "transformer_preset", delegate, navigator)).withApplyPresetAction(() -> {
         this.addPresetSections(transformer, navigator, delegate);
      }).withCurrentOnTop(true));
      this.addPresetSections(transformer, navigator, delegate);
   }

   public void addPresetSections(final TransformerMulti transformer, TableNavigator navigator, TableDelegate delegate) {
      this.addSegment(2, new TableDataSourceTransformerList(transformer.getTransformers(), delegate, navigator) {
         public boolean canEditList() {
            return transformer.getData().isCustom();
         }
      });
      this.addSegment(3, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.transformer.multi.condition"), transformer.getEnvironmentMatcher(), (Object)null).enabled(() -> {
         return transformer.getData().isCustom();
      }));
   }

   public TransformerMulti getTransformer() {
      return this.transformer;
   }

   public void setTransformer(TransformerMulti transformer) {
      this.transformer = transformer;
   }
}
