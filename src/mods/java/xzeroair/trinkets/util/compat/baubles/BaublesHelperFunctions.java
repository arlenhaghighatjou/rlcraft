package xzeroair.trinkets.util.compat.baubles;

import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import xzeroair.trinkets.client.gui.TrinketGui;
import xzeroair.trinkets.client.gui.TrinketGuiButton;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.trinketcontainer.OpenTrinketGui;
import xzeroair.trinkets.util.TrinketsConfig;

public class BaublesHelperFunctions {
   public BaublesHelperFunctions() {
   }

   public static ResourceLocation getBaublesResourceLocation() {
      return GuiPlayerExpanded.background;
   }

   public static void guiScreenEventHelper(Post event) {
      if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiPlayerExpanded) {
         GuiContainer gui = (GuiContainer)event.getGui();
         int ID = TrinketsConfig.CLIENT.GUI.button.ID;
         int X = TrinketsConfig.CLIENT.GUI.button.X;
         int Y = TrinketsConfig.CLIENT.GUI.button.Y;
         int Width = TrinketsConfig.CLIENT.GUI.button.bWidth;
         int Height = TrinketsConfig.CLIENT.GUI.button.bHeight;
         event.getButtonList().add(new TrinketGuiButton(ID, gui, X, Y, Width, Height, I18n.format("gui.xat.button.open", new Object[0])));
      }

   }

   public static void mousePressedHelper(GuiContainer gui, int ID) {
      if (gui instanceof GuiPlayerExpanded) {
         NetworkHandler.sendToServer(new OpenTrinketGui());
      } else if (gui instanceof TrinketGui && ID == 55) {
         PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory());
      } else if (ID != 9999) {
         ((TrinketGui)gui).displayNormalInventory();
         NetworkHandler.sendToServer(new OpenTrinketGui(99));
      }

   }
}
