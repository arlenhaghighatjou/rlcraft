package ivorius.reccomplex.gui.editstructure.transformers;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellFloatSlider;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.scale.Scales;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerNatural;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBTNatural extends TableDataSourceSegmented {
   private TransformerNatural transformer;

   public TableDataSourceBTNatural(TransformerNatural transformer, TableNavigator navigator, TableDelegate delegate) {
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
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.natural.naturalExpansionDistance"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.natural.naturalExpansionDistance.tooltip", new Object[0]));
      }, () -> {
         TableCellFloatSlider cell = new TableCellFloatSlider("naturalExpansionRandomization", TableCells.toFloat(Double.valueOf(transformer.naturalExpansionRandomization)).floatValue(), 0.0F, 40.0F);
         cell.setScale(Scales.pow(5.0F));
         cell.addListener((val) -> {
            transformer.naturalExpansionRandomization = TableCells.toDouble(val).doubleValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.natural.naturalExpansionRandomization"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.natural.naturalExpansionRandomization.tooltip", new Object[0]));
      }});
   }

   public static TableCellString cellForBlock(String id, String block) {
      TableCellString cell = new TableCellString(id, block);
      cell.setShowsValidityState(true);
      setStateForBlockTextfield(cell);
      return cell;
   }

   public static void setStateForBlockTextfield(TableCellString cell) {
      cell.setValidityState(stateForBlock((String)cell.getPropertyValue()));
   }

   public static GuiValidityStateIndicator.State stateForBlock(String blockID) {
      return Block.REGISTRY.containsKey(new ResourceLocation(blockID)) ? GuiValidityStateIndicator.State.VALID : GuiValidityStateIndicator.State.INVALID;
   }

   public TransformerNatural getTransformer() {
      return this.transformer;
   }

   public void setTransformer(TransformerNatural transformer) {
      this.transformer = transformer;
   }
}
