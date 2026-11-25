package betterquesting.handlers;

import betterquesting.blocks.TileSubmitStation;
import betterquesting.client.gui2.GuiQuestHelp;
import betterquesting.client.gui2.inventory.ContainerSubmitStation;
import betterquesting.client.gui2.inventory.GuiSubmitStation;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
   public GuiHandler() {
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
      return ID == 0 && tile instanceof TileSubmitStation ? new ContainerSubmitStation(player.inventory, (TileSubmitStation)tile) : null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
      if (ID == 0 && tile instanceof TileSubmitStation) {
         return new GuiSubmitStation((GuiScreen)null, player.inventory, (TileSubmitStation)tile);
      } else {
         return ID == 1 ? new GuiQuestHelp((GuiScreen)null) : null;
      }
   }
}
