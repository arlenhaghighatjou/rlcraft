package ivorius.reccomplex.gui.editstructure;

import ivorius.ivtoolkit.blocks.BlockStates;
import ivorius.reccomplex.gui.editstructure.preset.TableDataSourcePresettedList;
import ivorius.reccomplex.gui.editstructure.preset.TableDataSourcePresettedObject;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.presets.PresettedList;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBlockState;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceWeightedBlockStateList extends TableDataSourceSegmented {
   public TableDataSourceWeightedBlockStateList(PresettedList<WeightedBlockState> list, TableDelegate delegate, TableNavigator navigator) {
      this.addSegment(0, (new TableDataSourcePresettedObject(list, "block_preset", delegate, navigator)).withApplyPresetAction(() -> {
         this.addPresetSegments(list, delegate, navigator);
      }));
      this.addPresetSegments(list, delegate, navigator);
   }

   public void addPresetSegments(PresettedList<WeightedBlockState> list, final TableDelegate delegate, TableNavigator navigator) {
      TableDataSourcePresettedList<WeightedBlockState> listSource = new TableDataSourcePresettedList<WeightedBlockState>(list, delegate, navigator) {
         public String getDisplayString(WeightedBlockState entry) {
            return entry.state == null ? String.format("None (%.2f)", entry.getWeight()) : String.format("%s$%d (%.2f)", entry.state.getBlock().getLocalizedName(), BlockStates.toMetadata(entry.state), entry.getWeight());
         }

         public WeightedBlockState newEntry(String actionID) {
            return new WeightedBlockState((Double)null, Blocks.STONE.getDefaultState(), (NBTTagCompound)null);
         }

         @Nonnull
         public TableCell entryCell(boolean enabled, WeightedBlockState weightedBlockState) {
            return TableCells.edit(enabled, this.navigator, delegate, () -> {
               return new TableDataSourceWeightedBlockState(weightedBlockState, this.navigator, this.tableDelegate);
            });
         }

         public WeightedBlockState copyEntry(WeightedBlockState weightedBlockState) {
            return weightedBlockState.copy();
         }
      };
      listSource.setDuplicateTitle(TextFormatting.GREEN + "D");
      this.addSegment(1, listSource);
   }

   @Nonnull
   public String title() {
      return "Blocks";
   }
}
