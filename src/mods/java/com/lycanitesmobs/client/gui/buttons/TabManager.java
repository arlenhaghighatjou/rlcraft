package com.lycanitesmobs.client.gui.buttons;

import com.lycanitesmobs.LycanitesMobs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.fml.client.FMLClientHandler;

public class TabManager {
   public static ArrayList<Tab> tabList = new ArrayList();
   private static Minecraft mc = FMLClientHandler.instance().getClient();

   public TabManager() {
   }

   public static void registerTab(Tab tab) {
      tabList.add(tab);
   }

   public static ArrayList<Tab> getTabList() {
      return tabList;
   }

   public static void addTabsToInventory(GuiScreen gui) {
      if (LycanitesMobs.config.getBool("GUI", "Show Inventory Tabs", true, "Set to false to disable the GUI tabs.") && gui.getClass() == GuiInventory.class) {
         addTabsToList(gui.buttonList);
      }

   }

   public static void openInventoryGui() {
      GuiInventory inventory = new GuiInventory(mc.player);
      mc.displayGuiScreen(inventory);
      addTabsToInventory(inventory);
   }

   public static void updateTabValues(int cornerX, int cornerY, Class<?> selectedButton) {
      int count = 2;

      for(int i = 0; i < tabList.size(); ++i) {
         Tab t = (Tab)tabList.get(i);
         if (t.shouldAddToList()) {
            t.id = count;
            t.x = cornerX + (count - 2) * 28;
            t.y = cornerY - 28;
            t.enabled = !t.getClass().equals(selectedButton);
            ++count;
         }
      }

   }

   public static void addTabsToList(List buttonList) {
      for(Tab tab : tabList) {
         if (tab.shouldAddToList()) {
            buttonList.add(tab);
         }
      }

   }
}
