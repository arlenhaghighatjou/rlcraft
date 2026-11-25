package ivorius.reccomplex.gui.editstructure.transformers;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceBlockState;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerPillar;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBTPillar extends TableDataSourceSegmented {
   private TransformerPillar transformer;

   public TableDataSourceBTPillar(TransformerPillar transformer, TableNavigator navigator, TableDelegate delegate) {
      this.transformer = transformer;
      this.addSegment(0, new TableDataSourceTransformer(transformer, delegate, navigator));
      this.addSegment(1, new Supplier[]{() -> {
         TableCellBoolean cellGenUpwards = new TableCellBoolean("generateUpwards", transformer.generateUpwards, IvTranslations.format("reccomplex.transformer.pillar.generateUpwards.true", new Object[0]), IvTranslations.format("reccomplex.transformer.pillar.generateUpwards.false", new Object[0]));
         cellGenUpwards.addListener((cell) -> {
            transformer.generateUpwards = ((Boolean)cellGenUpwards.getPropertyValue()).booleanValue();
         });
         return new TitledCell(IvTranslations.format("reccomplex.transformer.pillar.generateUpwards", new Object[0]), cellGenUpwards);
      }});
      this.addSegment(2, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.sources"), IvTranslations.getLines("reccomplex.transformer.block.source.tooltip"), transformer.sourceMatcher, (Object)null));
      this.addSegment(3, new TableDataSourceBlockState(transformer.destState, (state) -> {
         transformer.destState = state;
      }, navigator, delegate, IvTranslations.get("reccomplex.transformer.pillar.dest.block"), IvTranslations.get("reccomplex.transformer.pillar.dest.metadata")));
   }

   public TransformerPillar getTransformer() {
      return this.transformer;
   }

   public void setTransformer(TransformerPillar transformer) {
      this.transformer = transformer;
   }
}
