package vazkii.quark.base.network;

import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import vazkii.quark.misc.client.gui.GuiNoteBlock;
import vazkii.quark.oddities.client.gui.GuiBackpackInventory;
import vazkii.quark.oddities.client.gui.GuiMatrixEnchanting;
import vazkii.quark.oddities.inventory.ContainerBackpack;
import vazkii.quark.oddities.inventory.ContainerMatrixEnchanting;
import vazkii.quark.oddities.tile.TileMatrixEnchanter;

public class GuiHandler implements IGuiHandler {
   public GuiHandler() {
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      switch(ID) {
      case 2:
         return new ContainerBackpack(player);
      case 3:
         return new ContainerMatrixEnchanting(player.inventory, (TileMatrixEnchanter)world.getTileEntity(new BlockPos(x, y, z)));
      default:
         return null;
      }
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      switch(ID) {
      case 0:
         TileEntitySign sign = (TileEntitySign)world.getTileEntity(new BlockPos(x, y, z));
         if (sign != null) {
            return new GuiEditSign(sign);
         }
      default:
         return null;
      case 1:
         return new GuiNoteBlock((TileEntityNote)world.getTileEntity(new BlockPos(x, y, z)));
      case 2:
         return new GuiBackpackInventory(player);
      case 3:
         return new GuiMatrixEnchanting(player.inventory, (TileMatrixEnchanter)world.getTileEntity(new BlockPos(x, y, z)));
      }
   }
}
