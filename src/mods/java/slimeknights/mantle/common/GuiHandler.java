package slimeknights.mantle.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
   public GuiHandler() {
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
      return te instanceof IInventoryGui ? ((IInventoryGui)te).createContainer(player.inventory, world, new BlockPos(x, y, z)) : null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
      return te instanceof IInventoryGui ? ((IInventoryGui)te).createGui(player.inventory, world, new BlockPos(x, y, z)) : null;
   }
}
