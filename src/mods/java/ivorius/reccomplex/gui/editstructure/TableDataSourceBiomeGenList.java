package ivorius.reccomplex.gui.editstructure;

import ivorius.reccomplex.gui.editstructure.preset.TableDataSourcePresettedList;
import ivorius.reccomplex.gui.editstructure.preset.TableDataSourcePresettedObject;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.presets.PresettedList;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBiomeMatcher;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBiomeGenList extends TableDataSourceSegmented {
   public TableDataSourceBiomeGenList(PresettedList<WeightedBiomeMatcher> list, TableDelegate tableDelegate, TableNavigator navigator) {
      this.addSegment(0, (new TableDataSourcePresettedObject(list, "biome_preset", tableDelegate, navigator)).withApplyPresetAction(() -> {
         this.addPresetSegments(list, tableDelegate, navigator);
      }));
      this.addPresetSegments(list, tableDelegate, navigator);
   }

   public void addPresetSegments(PresettedList<WeightedBiomeMatcher> list, TableDelegate tableDelegate, TableNavigator navigator) {
      this.addSegment(1, new TableDataSourcePresettedList<WeightedBiomeMatcher>(list, tableDelegate, navigator) {
         public String getDisplayString(WeightedBiomeMatcher weightedBiomeMatcher) {
            return String.format("%s%s: %.2f", weightedBiomeMatcher.getDisplayString(), TextFormatting.RESET, weightedBiomeMatcher.getActiveGenerationWeight());
         }

         public WeightedBiomeMatcher newEntry(String actionID) {
            return new WeightedBiomeMatcher("", (Double)null);
         }

         @Nonnull
         public TableCell entryCell(boolean enabled, WeightedBiomeMatcher weightedBiomeMatcher) {
            return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
               return new TableDataSourceBiomeGen(weightedBiomeMatcher, this.tableDelegate);
            });
         }
      });
   }

   @Nonnull
   public String title() {
      return "Biomes";
   }
}
