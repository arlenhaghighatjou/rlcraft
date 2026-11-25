package ivorius.reccomplex.gui.editstructure;

import ivorius.reccomplex.gui.editstructure.preset.TableDataSourcePresettedList;
import ivorius.reccomplex.gui.editstructure.preset.TableDataSourcePresettedObject;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.presets.PresettedList;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedDimensionMatcher;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceDimensionGenList extends TableDataSourceSegmented {
   public TableDataSourceDimensionGenList(PresettedList<WeightedDimensionMatcher> list, TableDelegate delegate, TableNavigator navigator) {
      this.addSegment(0, (new TableDataSourcePresettedObject(list, "dimension_preset", delegate, navigator)).withApplyPresetAction(() -> {
         this.addPresetSegments(list, delegate, navigator);
      }));
      this.addPresetSegments(list, delegate, navigator);
   }

   public void addPresetSegments(PresettedList<WeightedDimensionMatcher> list, final TableDelegate delegate, TableNavigator navigator) {
      this.addSegment(1, new TableDataSourcePresettedList<WeightedDimensionMatcher>(list, delegate, navigator) {
         public String getDisplayString(WeightedDimensionMatcher generationInfo) {
            return String.format("%s%s: %.1f", generationInfo.getDisplayString(), TextFormatting.RESET, generationInfo.getActiveGenerationWeight());
         }

         public WeightedDimensionMatcher newEntry(String actionID) {
            return new WeightedDimensionMatcher("", (Double)null);
         }

         @Nonnull
         public TableCell entryCell(boolean enabled, WeightedDimensionMatcher weightedDimensionMatcher) {
            return TableCells.edit(enabled, this.navigator, delegate, () -> {
               return new TableDataSourceDimensionGen(weightedDimensionMatcher, this.tableDelegate);
            });
         }
      });
   }

   @Nonnull
   public String title() {
      return "Dimensions";
   }
}
