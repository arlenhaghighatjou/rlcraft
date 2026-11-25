package ivorius.reccomplex.gui.inspector;

import ivorius.reccomplex.gui.TableDataSourceBlockPos;
import ivorius.reccomplex.gui.TableDataSourceBlockState;
import ivorius.reccomplex.gui.editstructure.TableDataSourceWeightedBlockState;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceInspectBlock extends TableDataSourceSegmented {
   public BlockPos pos;
   public IBlockState state;
   public NBTTagCompound tileEntityData;
   public TableNavigator navigator;
   public TableDelegate delegate;

   public TableDataSourceInspectBlock(BlockPos pos, IBlockState state, TableNavigator navigator, TableDelegate delegate, NBTTagCompound tileEntityData) {
      this.pos = pos;
      this.state = state;
      this.navigator = navigator;
      this.delegate = delegate;
      this.tileEntityData = tileEntityData;
      this.addSegment(0, TableDataSourceWeightedBlockState.tileEntitySegment(navigator, delegate, () -> {
         return this.tileEntityData;
      }, (val) -> {
         this.tileEntityData = val;
      }));
      this.addSegment(1, (new TableDataSourceBlockState(state, (instate) -> {
         this.state = instate;
      }, navigator, delegate)).setShowExtendedProperties(true));
      this.addSegment(2, new TableDataSourceBlockPos(pos, (blockPos) -> {
         this.pos = blockPos;
      }));
   }

   @Nonnull
   public String title() {
      return "Inspect Block";
   }
}
