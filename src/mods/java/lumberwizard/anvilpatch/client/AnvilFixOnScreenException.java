package lumberwizard.anvilpatch.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;

public class AnvilFixOnScreenException extends CustomModLoadingErrorDisplayException {
   public AnvilFixOnScreenException(Throwable cause) {
      super(cause.getMessage(), cause);
   }

   public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {
   }

   public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
      if (errorScreen != null && fontRenderer != null) {
         errorScreen.drawCenteredString(fontRenderer, "Anvil Patch - Lawful is incompatible with AnvilFix!", errorScreen.width / 2, errorScreen.height / 2 - 20, 16777215);
         errorScreen.drawCenteredString(fontRenderer, "Please remove one of the mods.", errorScreen.width / 2, errorScreen.height / 2 - 10, 16777215);
      }
   }
}
