package bettertabs.handlers;

import bettertabs.client.gui.BetterQuestingTab;
import bettertabs.client.gui.HatsTab;
import bettertabs.client.gui.LevelUpLegacyTab;
import bettertabs.client.gui.LevelUpReloadedTab;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TabHandler {
   public TabHandler() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void inventoryInit(Post event) {
      if (event.getGui() instanceof GuiInventory) {
         if (Loader.isModLoaded("betterquesting") && ConfigHandler.bqTabEnabled) {
            event.getButtonList().add(new BetterQuestingTab(7501, event.getGui().width / 2 + ConfigHandler.bqXOffset, event.getGui().height / 2 - 111, ""));
         }

         if (Loader.isModLoaded("levelup2") && ConfigHandler.lurTabEnabled) {
            event.getButtonList().add(new LevelUpReloadedTab(7502, event.getGui().width / 2 - 114, event.getGui().height / 2 + ConfigHandler.luYOffset, ""));
         }

         if (Loader.isModLoaded("levelup") && ConfigHandler.lulTabEnabled) {
            event.getButtonList().add(new LevelUpLegacyTab(7503, event.getGui().width / 2 - 114, event.getGui().height / 2 + ConfigHandler.luYOffset, ""));
         }

         if (Loader.isModLoaded("classyhats") && ConfigHandler.hatsTabEnabled) {
            event.getButtonList().add(new HatsTab(7504, event.getGui().width / 2 + ConfigHandler.hatsXOffset, event.getGui().height / 2 + ConfigHandler.hatsYOffset, ""));
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void renderTick(net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Post event) {
      if (event.getGui() instanceof GuiInventory) {
         int mouseY = event.getMouseY();
         int mouseX = event.getMouseX();
         if (Loader.isModLoaded("lycanitesmobs") && ConfigHandler.lycaniteTooltipEnabled) {
            int xMinLY = event.getGui().width / 2 + 55;
            int yMinLY = event.getGui().height / 2 - 110;
            if (xMinLY < mouseX && mouseX < xMinLY + 30 && yMinLY < mouseY && mouseY < yMinLY + 27) {
               event.getGui().drawHoveringText(I18n.format("bettertabs.tooltip.lycanitesmobs", new Object[0]), mouseX, mouseY);
            }
         }
      }

   }
}
