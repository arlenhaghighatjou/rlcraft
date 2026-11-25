package vazkii.quark.base.client.gui.config;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiConfigFirstLoad extends GuiConfigBase {
   public GuiConfigFirstLoad(GuiScreen parent) {
      super(parent);
   }

   public void initGui() {
      super.initGui();
      this.title = this.title + " - " + I18n.format("quark.config.firstload", new Object[0]);
      int x = this.width / 2 - 100;
      int y = this.height / 6;
      this.buttonList.add(this.backButton = new GuiButton(0, x, y + 167, 200, 20, I18n.format("quark.config.skip", new Object[0])));
      this.buttonList.add(new GuiButton(1, x, y + 142, 98, 20, I18n.format("quark.config.configure", new Object[0])));
      this.buttonList.add(new GuiButton(2, x + 102, y + 142, 98, 20, I18n.format("quark.config.opensite", new Object[0])));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      switch(button.id) {
      case 1:
         this.mc.displayGuiScreen(new GuiConfigRoot(this.parent));
         break;
      case 2:
         this.tryOpenWebsite("https://quark.vazkii.net");
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      int x = this.width / 2;
      int y = this.height / 6;

      for(int i = 0; i < 10; ++i) {
         String s = I18n.format("quark.config.firstloadinfo" + i, new Object[0]);
         if (i == 5) {
            s = TextFormatting.RED.toString() + TextFormatting.UNDERLINE.toString() + s;
         }

         this.drawCenteredString(this.fontRenderer, s, x, y, 16777215);
         y += 10;
         if (i == 2 || i == 4) {
            y += 8;
         }
      }

   }
}
