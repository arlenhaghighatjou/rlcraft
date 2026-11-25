package com.blamejared.ctgui.client;

import com.blamejared.ctgui.MTRecipe;
import com.blamejared.ctgui.api.GuiRegistry;
import com.blamejared.ctgui.client.gui.craftingtable.ContainerCraftingTable;
import com.blamejared.ctgui.client.gui.craftingtable.GuiCraftingTable;
import com.blamejared.ctgui.client.gui.furnace.ContainerFurnace;
import com.blamejared.ctgui.client.gui.furnace.GuiFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {
   public GuiHandler() {
      NetworkRegistry.INSTANCE.registerGuiHandler(MTRecipe.INSTANCE, this);
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      String var7 = GuiRegistry.getName(ID);
      byte var8 = -1;
      switch(var7.hashCode()) {
      case -1521450164:
         if (var7.equals("craftingtable")) {
            var8 = 0;
         }
         break;
      case -505639592:
         if (var7.equals("furnace")) {
            var8 = 1;
         }
      }

      switch(var8) {
      case 0:
         return new ContainerCraftingTable(player.inventory);
      case 1:
         return new ContainerFurnace(player.inventory);
      default:
         return null;
      }
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      String var7 = GuiRegistry.getName(ID);
      byte var8 = -1;
      switch(var7.hashCode()) {
      case -1521450164:
         if (var7.equals("craftingtable")) {
            var8 = 0;
         }
         break;
      case -505639592:
         if (var7.equals("furnace")) {
            var8 = 1;
         }
      }

      switch(var8) {
      case 0:
         return new GuiCraftingTable(new ContainerCraftingTable(player.inventory));
      case 1:
         return new GuiFurnace(new ContainerFurnace(player.inventory));
      default:
         return null;
      }
   }
}
