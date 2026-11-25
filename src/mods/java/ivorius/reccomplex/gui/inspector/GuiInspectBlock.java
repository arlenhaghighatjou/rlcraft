package ivorius.reccomplex.gui.inspector;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.table.screen.GuiScreenEditTable;
import ivorius.reccomplex.network.PacketInspectBlock;
import java.util.function.Consumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInspectBlock extends GuiScreenEditTable<TableDataSourceInspectBlock> {
   public GuiInspectBlock(BlockPos pos, IBlockState state, NBTTagCompound tileEntityData) {
      this.setDataSource(new TableDataSourceInspectBlock(pos, state, this, this, tileEntityData), (ds) -> {
         RecurrentComplex.network.sendToServer(new PacketInspectBlock(ds.tileEntityData, ds.pos, ds.state));
      });
   }
}
