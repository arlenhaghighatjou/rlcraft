package bq_standard.handlers;

import bq_standard.client.gui.editors.GuiEditLootGroup;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
   public GuiHandler() {
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return ID == 0 ? new GuiEditLootGroup((GuiScreen)null) : null;
   }
}
