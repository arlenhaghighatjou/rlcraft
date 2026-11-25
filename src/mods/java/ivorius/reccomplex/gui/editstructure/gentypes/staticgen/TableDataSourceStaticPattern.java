package ivorius.reccomplex.gui.editstructure.gentypes.staticgen;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.cell.TableCellIntSlider;
import ivorius.reccomplex.gui.table.cell.TableCellIntTextField;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.StaticGeneration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceStaticPattern extends TableDataSourceSegmented {
   private StaticGeneration.Pattern pattern;
   private TableDelegate tableDelegate;

   public TableDataSourceStaticPattern(StaticGeneration.Pattern pattern, TableDelegate tableDelegate) {
      this.pattern = pattern;
      this.tableDelegate = tableDelegate;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellIntTextField cell = new TableCellIntTextField("repeatX", pattern.repeatX);
         cell.addListener((val) -> {
            pattern.repeatX = val.intValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.generationInfo.static.pattern.repeat.x"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.generationInfo.static.pattern.repeat.tooltip"));
      }, () -> {
         TableCellIntTextField cell = new TableCellIntTextField("repeatZ", pattern.repeatZ);
         cell.addListener((val) -> {
            pattern.repeatZ = val.intValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.generationInfo.static.pattern.repeat.z"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.generationInfo.static.pattern.repeat.tooltip"));
      }});
      this.addSegment(1, new Supplier[]{() -> {
         TableCellIntSlider cell = new TableCellIntSlider("shiftX", pattern.randomShiftX, 0, 10);
         cell.addListener((val) -> {
            pattern.randomShiftX = val.intValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.generationInfo.static.pattern.rshift.x"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.generationInfo.static.pattern.rshift.tooltip"));
      }, () -> {
         TableCellIntSlider cell = new TableCellIntSlider("shiftZ", pattern.randomShiftZ, 0, 10);
         cell.addListener((val) -> {
            pattern.randomShiftZ = val.intValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.generationInfo.static.pattern.rshift.z"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.generationInfo.static.pattern.rshift.tooltip"));
      }});
   }

   @Nonnull
   public String title() {
      return "Static Pattern";
   }
}
