package ivorius.reccomplex.gui.editstructure.transformers;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellFloatSlider;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.scale.Scales;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerNaturalAir;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBTNaturalAir extends TableDataSourceSegmented {
   private TransformerNaturalAir transformer;

   public TableDataSourceBTNaturalAir(TransformerNaturalAir transformer, TableNavigator navigator, TableDelegate delegate) {
      this.transformer = transformer;
      this.addSegment(0, new TableDataSourceTransformer(transformer, delegate, navigator));
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.sources"), IvTranslations.getLines("reccomplex.transformer.block.source.tooltip"), transformer.sourceMatcher, (Object)null));
      this.addSegment(2, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.destinations"), IvTranslations.getLines("reccomplex.transformer.block.dest.tooltip"), transformer.destMatcher, (Object)null));
      this.addSegment(3, new Supplier[]{() -> {
         TableCellFloatSlider cell = new TableCellFloatSlider("naturalExpansionDistance", TableCells.toFloat(Double.valueOf(transformer.naturalExpansionDistance)).floatValue(), 0.0F, 40.0F);
         cell.setScale(Scales.pow(5.0F));
         cell.addListener((val) -> {
            transformer.naturalExpansionDistance = TableCells.toDouble(val).doubleValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.naturalAir.naturalExpansionDistance"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.naturalAir.naturalExpansionDistance.tooltip", new Object[0]));
      }, () -> {
         TableCellFloatSlider cell = new TableCellFloatSlider("naturalExpansionRandomization", TableCells.toFloat(Double.valueOf(transformer.naturalExpansionRandomization)).floatValue(), 0.0F, 40.0F);
         cell.setScale(Scales.pow(5.0F));
         cell.addListener((val) -> {
            transformer.naturalExpansionRandomization = TableCells.toDouble(val).doubleValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.naturalAir.naturalExpansionRandomization"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.naturalAir.naturalExpansionRandomization.tooltip", new Object[0]));
      }});
   }

   public TransformerNaturalAir getTransformer() {
      return this.transformer;
   }

   public void setTransformer(TransformerNaturalAir transformer) {
      this.transformer = transformer;
   }
}
