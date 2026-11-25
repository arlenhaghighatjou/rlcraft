package ivorius.reccomplex.gui.editstructure;

import ivorius.reccomplex.gui.RCGuiTables;
import ivorius.reccomplex.gui.TableDataSourceBlockState;
import ivorius.reccomplex.gui.nbt.TableDataSourceNBTTagCompound;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBlockState;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceWeightedBlockState extends TableDataSourceSegmented {
   private WeightedBlockState weightedBlockState;

   public TableDataSourceWeightedBlockState(WeightedBlockState weightedBlockState, TableNavigator navigator, TableDelegate delegate) {
      this.weightedBlockState = weightedBlockState;
      this.addSegment(0, new Supplier[]{() -> {
         return RCGuiTables.defaultWeightElement((val) -> {
            weightedBlockState.weight = TableCells.toDouble(val);
         }, weightedBlockState.weight);
      }});
      this.addSegment(1, new TableDataSourceBlockState(weightedBlockState.state, (state) -> {
         weightedBlockState.state = state;
      }, navigator, delegate, "Block", "Metadata"));
      this.addSegment(2, tileEntitySegment(navigator, delegate, () -> {
         return weightedBlockState.tileEntityInfo;
      }, (val) -> {
         weightedBlockState.tileEntityInfo = val;
      }));
   }

   @Nonnull
   public static TableDataSource tileEntitySegment(TableNavigator navigator, TableDelegate delegate, Supplier<NBTTagCompound> supplier, Consumer<NBTTagCompound> consumer) {
      return TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceNBTTagCompound(delegate, navigator, (NBTTagCompound)supplier.get()) {
            @Nonnull
            public String title() {
               return "Tile Entity";
            }
         };
      }).enabled(() -> {
         return supplier.get() != null;
      }).addAction(() -> {
         consumer.accept(supplier.get() != null ? null : new NBTTagCompound());
         delegate.reloadData();
      }, () -> {
         return supplier.get() != null ? "Remove" : "Add";
      }, (Supplier)null).withTitle("Tile Entity").buildDataSource();
   }

   @Nonnull
   public String title() {
      return this.weightedBlockState.state.getBlock().getLocalizedName();
   }
}
