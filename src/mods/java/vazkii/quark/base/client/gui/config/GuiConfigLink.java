package vazkii.quark.base.client.gui.config;

import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;

public class GuiConfigLink extends GuiConfirmOpenLink {
   private final GuiScreen parent;

   public GuiConfigLink(GuiScreen parentScreenIn, String url) {
      super(parentScreenIn, url, 0, true);
      this.parent = parentScreenIn;
   }

   protected void keyTyped(char typedChar, int keyCode) {
      if (keyCode == 1) {
         this.returnToParent();
      }

   }

   void returnToParent() {
      this.mc.displayGuiScreen(this.parent);
      if (this.mc.currentScreen == null) {
         this.mc.setIngameFocus();
      }

   }
}
