package net.silentchaos512.lib.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.silentchaos512.lib.guidebook.gui.GuiGuide;
import net.silentchaos512.lib.guidebook.gui.GuiMainPage;
import net.silentchaos512.lib.guidebook.misc.GuideBookUtils;
import net.silentchaos512.lib.item.ItemGuideBookSL;

public final class GuiHandlerLibF implements IGuiHandler {
   public GuiHandlerLibF() {
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      ItemGuideBookSL item = ItemGuideBookSL.getBookById(ID);
      if (item != null) {
         if (item.forcedPage != null) {
            GuiGuide gui = GuideBookUtils.createBookletGuiFromPage(item.book, (GuiScreen)null, item.forcedPage);
            item.forcedPage = null;
            return gui;
         } else {
            return item.book.lastViewedPage != null ? item.book.lastViewedPage : new GuiMainPage(item.book, (GuiScreen)null);
         }
      } else {
         return null;
      }
   }
}
