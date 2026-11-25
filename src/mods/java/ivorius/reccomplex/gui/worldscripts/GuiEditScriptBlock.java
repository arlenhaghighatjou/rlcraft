package ivorius.reccomplex.gui.worldscripts;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.block.TileEntityBlockScript;
import ivorius.reccomplex.gui.table.screen.GuiScreenEditTable;
import ivorius.reccomplex.network.PacketEditTileEntity;
import java.util.function.Consumer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEditScriptBlock extends GuiScreenEditTable<TableDataSourceScriptBlock> {
   public GuiEditScriptBlock(TileEntityBlockScript tileEntity) {
      TileEntityBlockScript copy = (TileEntityBlockScript)TileEntity.create(tileEntity.getWorld(), tileEntity.writeToNBT(new NBTTagCompound()));
      this.setDataSource(new TableDataSourceScriptBlock(copy, this, this), (ds) -> {
         tileEntity.readFromNBT(ds.script.writeToNBT(new NBTTagCompound()));
         RecurrentComplex.network.sendToServer(new PacketEditTileEntity(ds.script));
      });
   }
}
