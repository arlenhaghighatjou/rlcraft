package codersafterdark.reskillable.client.gui.handler;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import codersafterdark.reskillable.base.ConfigHandler;
import codersafterdark.reskillable.client.base.RenderHelper;
import codersafterdark.reskillable.client.gui.GuiAbilities;
import codersafterdark.reskillable.client.gui.GuiSkillInfo;
import codersafterdark.reskillable.client.gui.GuiSkills;
import codersafterdark.reskillable.client.gui.button.GuiButtonInventoryTab;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InventoryTabHandler {
   public static String tooltip;
   public static int mx;
   public static int my;

   public InventoryTabHandler() {
   }

   public static void addTabs(GuiScreen currScreen, List<GuiButton> buttonList) {
      if (ConfigHandler.enableTabs) {
         int x = currScreen.width / 2 - 120;
         int y = currScreen.height / 2 - 76;
         if (currScreen instanceof GuiContainerCreative) {
            x -= 10;
            y += 15;
         }

         buttonList.add(new GuiButtonInventoryTab(82931, x, y, GuiButtonInventoryTab.TabType.INVENTORY, (gui) -> {
            return (gui instanceof GuiInventory || gui instanceof GuiContainerCreative);
         }));
         buttonList.add(new GuiButtonInventoryTab(82932, x, y + 29, GuiButtonInventoryTab.TabType.SKILLS, (gui) -> {
            return (gui instanceof GuiSkills || gui instanceof GuiSkillInfo);
         }));
         buttonList.add(new GuiButtonInventoryTab(82933, x, y + 58, GuiButtonInventoryTab.TabType.ABILITIES, (gui) -> {
            return (gui instanceof GuiAbilities);
         }));
      }
   }

   public static void register(EventBus bus) {
      bus.register(Post.class, InventoryTabHandler::initGui);
      bus.register(Pre.class, InventoryTabHandler::performAction);
      bus.register(RenderTickEvent.class, InventoryTabHandler::finishRenderTick);
      bus.register(PotionShiftEvent.class, InventoryTabHandler::onPotionShiftEvent);
   }

   @SideOnly(Side.CLIENT)
   public static void initGui(Post event) {
      if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative) {
         addTabs(event.getGui(), event.getButtonList());
      }

   }

   @SideOnly(Side.CLIENT)
   public static void performAction(Pre event) {
      if (event.getButton() instanceof GuiButtonInventoryTab) {
         GuiButtonInventoryTab tab = (GuiButtonInventoryTab)event.getButton();
         Minecraft mc = Minecraft.getMinecraft();
         switch(tab.type) {
         case INVENTORY:
            mc.displayGuiScreen(new GuiInventory(mc.player));
            break;
         case SKILLS:
            mc.displayGuiScreen(new GuiSkills());
            break;
         case ABILITIES:
            mc.displayGuiScreen(new GuiAbilities());
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static void finishRenderTick(RenderTickEvent event) {
      if (event.phase == Phase.END && tooltip != null) {
         RenderHelper.renderTooltip(mx, my, Collections.singletonList(tooltip));
         tooltip = null;
      }

   }

   @SideOnly(Side.CLIENT)
   public static void onPotionShiftEvent(PotionShiftEvent event) {
      if (ConfigHandler.enableTabs) {
         event.setCanceled(true);
      }

   }

   public static int getPotionOffset() {
      return ConfigHandler.enableTabs ? 156 : 124;
   }
}
