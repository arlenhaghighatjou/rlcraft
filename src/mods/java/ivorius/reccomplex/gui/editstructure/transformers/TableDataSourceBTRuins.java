package ivorius.reccomplex.gui.editstructure.transformers;

import ivorius.ivtoolkit.gui.FloatRange;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDirections;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.gui.table.cell.TableCellFloatRange;
import ivorius.reccomplex.gui.table.cell.TableCellFloatSlider;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.scale.Scales;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerRuins;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBTRuins extends TableDataSourceSegmented {
   private TransformerRuins transformer;

   public TableDataSourceBTRuins(TransformerRuins transformer, TableNavigator navigator, TableDelegate delegate) {
      this.transformer = transformer;
      this.addSegment(0, new TableDataSourceTransformer(transformer, delegate, navigator));
      this.addSegment(1, new Supplier[]{() -> {
         return new TitledCell(new TableCellTitle("decayTitle", IvTranslations.get("reccomplex.transformer.ruins.decay.title")));
      }, () -> {
         TableCellFloatRange cell = new TableCellFloatRange("decay", new FloatRange(transformer.minDecay, transformer.maxDecay), 0.0F, 1.0F, "%.4f");
         cell.setScale(Scales.pow(5.0F));
         cell.addListener((val) -> {
            transformer.minDecay = val.getMin();
            transformer.maxDecay = val.getMax();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.ruins.decay.base"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.ruins.decay.base.tooltip", new Object[0]));
      }, () -> {
         TableCellFloatSlider cell = new TableCellFloatSlider("decayChaos", transformer.decayChaos, 0.0F, 1.0F);
         cell.setScale(Scales.pow(3.0F));
         cell.addListener((val) -> {
            transformer.decayChaos = val.floatValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.ruins.decay.chaos"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.ruins.decay.chaos.tooltip", new Object[0]));
      }, () -> {
         TableCellFloatSlider cell = new TableCellFloatSlider("decayValueDensity", transformer.decayValueDensity, 0.0F, 1.0F);
         cell.setScale(Scales.pow(3.0F));
         cell.addListener((val) -> {
            transformer.decayValueDensity = val.floatValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.ruins.decay.density"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.ruins.decay.density.tooltip", new Object[0]));
      }, () -> {
         TableCellEnum<EnumFacing> cell = new TableCellEnum<EnumFacing>("decaySide", transformer.decayDirection, TableDirections.getDirectionOptions(EnumFacing.VALUES));
         cell.addListener((val) -> {
            transformer.decayDirection = val;
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.ruins.decay.direction"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.ruins.decay.direction.tooltip", new Object[0]));
      }});
      this.addSegment(2, new Supplier[]{() -> {
         return new TitledCell(new TableCellTitle("otherTitle", IvTranslations.get("reccomplex.transformer.ruins.other.title")));
      }, () -> {
         TableCellFloatSlider cell = new TableCellFloatSlider("erosion", transformer.blockErosion, 0.0F, 1.0F);
         cell.setScale(Scales.pow(3.0F));
         cell.addListener((val) -> {
            transformer.blockErosion = val.floatValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.ruins.erosion"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.ruins.erosion.tooltip", new Object[0]));
      }, () -> {
         TableCellFloatSlider cell = new TableCellFloatSlider("vines", transformer.vineGrowth, 0.0F, 1.0F);
         cell.setScale(Scales.pow(3.0F));
         cell.addListener((val) -> {
            transformer.vineGrowth = val.floatValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.ruins.vines"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.ruins.vines.tooltip", new Object[0]));
      }, () -> {
         TableCellFloatSlider cell = new TableCellFloatSlider("cobwebs", transformer.cobwebGrowth, 0.0F, 1.0F);
         cell.setScale(Scales.pow(3.0F));
         cell.addListener((val) -> {
            transformer.cobwebGrowth = val.floatValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.ruins.cobwebs"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.ruins.cobwebs.tooltip", new Object[0]));
      }, () -> {
         TableCellBoolean cell = new TableCellBoolean("gravity", transformer.gravity);
         cell.addListener((val) -> {
            transformer.gravity = val.booleanValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.ruins.gravity"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.ruins.gravity.tooltip", new Object[0]));
      }});
   }

   public TransformerRuins getTransformer() {
      return this.transformer;
   }

   public void setTransformer(TransformerRuins transformer) {
      this.transformer = transformer;
   }
}
