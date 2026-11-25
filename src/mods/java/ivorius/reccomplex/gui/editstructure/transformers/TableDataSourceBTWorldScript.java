package ivorius.reccomplex.gui.editstructure.transformers;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.worldscripts.multi.TableDataSourceWorldScriptMulti;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerWorldScript;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBTWorldScript extends TableDataSourceSegmented {
   private TransformerWorldScript transformer;
   private TableNavigator navigator;
   private TableDelegate delegate;

   public TableDataSourceBTWorldScript(TransformerWorldScript transformer, TableNavigator navigator, TableDelegate delegate) {
      this.transformer = transformer;
      this.navigator = navigator;
      this.delegate = delegate;
      this.addSegment(0, new TableDataSourceTransformer(transformer, delegate, navigator));
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.sources"), IvTranslations.getLines("reccomplex.transformer.block.source.tooltip"), transformer.sourceMatcher, (Object)null));
      this.addSegment(2, new TableDataSourceWorldScriptMulti(transformer.script, Minecraft.getMinecraft().player.getPosition(), delegate, navigator));
   }

   public TransformerWorldScript getTransformer() {
      return this.transformer;
   }

   public void setTransformer(TransformerWorldScript transformer) {
      this.transformer = transformer;
   }
}
