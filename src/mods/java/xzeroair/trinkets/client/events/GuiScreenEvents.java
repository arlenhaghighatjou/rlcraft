package xzeroair.trinkets.client.events;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.client.gui.TrinketGui;
import xzeroair.trinkets.client.gui.TrinketGuiButton;
import xzeroair.trinkets.client.gui.entityPropertiesGui.GuiEntityPropertiesButton;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.compat.baubles.BaublesHelperFunctions;
import xzeroair.trinkets.util.config.gui.ClientConfigPropertiesGui;

public class GuiScreenEvents {
   public GuiScreenEvents() {
   }

   @SubscribeEvent
   public void guiPostInit(Post event) {
      int ID = TrinketsConfig.CLIENT.GUI.button.ID;
      int X = TrinketsConfig.CLIENT.GUI.button.X;
      int Y = TrinketsConfig.CLIENT.GUI.button.Y;
      int Xoffset = TrinketsConfig.CLIENT.GUI.button.Xoffset;
      int Yoffset = TrinketsConfig.CLIENT.GUI.button.Yoffset;
      int Width = TrinketsConfig.CLIENT.GUI.button.bWidth;
      int Height = TrinketsConfig.CLIENT.GUI.button.bHeight;
      if (TrinketsConfig.SERVER.GUI.guiEnabled) {
         if (event.getGui() instanceof TrinketGui) {
            GuiContainer gui = (GuiContainer)event.getGui();
            event.getButtonList().add(new TrinketGuiButton(ID, gui, X + Xoffset, Y + Yoffset, Width, Height, I18n.format("gui.xat.button.close", new Object[0])));
            if (Loader.isModLoaded("baubles")) {
               event.getButtonList().add(new TrinketGuiButton(55, gui, 64, 9, 10, 10, I18n.format("button.baubles", new Object[0])));
            }
         } else if (Loader.isModLoaded("baubles")) {
            BaublesHelperFunctions.guiScreenEventHelper(event);
         } else if (event.getGui() instanceof GuiInventory) {
            GuiContainer gui = (GuiContainer)event.getGui();
            event.getButtonList().add(new TrinketGuiButton(ID, gui, X, Y, Width, Height, I18n.format("gui.xat.button.open", new Object[0])));
         }
      }

      if (TrinketsConfig.CLIENT.raceProperties.shown && event.getGui() instanceof GuiInventory) {
         ClientConfigPropertiesGui config = TrinketsConfig.CLIENT.raceProperties;
         GuiContainer gui = (GuiContainer)event.getGui();
         event.getButtonList().add(new GuiEntityPropertiesButton(config.button.ID, gui, config.button.X, config.button.Y, config.button.bWidth, config.button.bHeight, I18n.format("gui.xat.button.open", new Object[0])));
      }

   }
}
