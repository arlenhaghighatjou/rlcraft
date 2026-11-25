package invtweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.jetbrains.annotations.NotNull;

public class InvTweaksGuiModNotWorking extends InvTweaksGuiSettingsAbstract {
   public InvTweaksGuiModNotWorking(Minecraft mc_, GuiScreen parentScreen_, InvTweaksConfig config_) {
      super(mc_, parentScreen_, config_);
   }

   public void drawScreen(int i, int j, float f) {
      super.drawScreen(i, j, f);
      int x = this.width / 2;
      this.drawCenteredString(this.obf.getFontRenderer(), I18n.format("invtweaks.help.bugsorting.pt1", new Object[0]), x, 80, 12303291);
      this.drawCenteredString(this.obf.getFontRenderer(), I18n.format("invtweaks.help.bugsorting.pt2", new Object[0]), x, 95, 12303291);
      this.drawCenteredString(this.obf.getFontRenderer(), I18n.format("invtweaks.help.bugsorting.pt3", new Object[0]), x, 110, 12303291);
      this.drawCenteredString(this.obf.getFontRenderer(), I18n.format("invtweaks.help.bugsorting.pt4", new Object[0]), x, 150, 16777113);
   }

   protected void actionPerformed(@NotNull GuiButton guibutton) {
      switch(guibutton.id) {
      case 200:
         this.obf.displayGuiScreen(new InvTweaksGuiSettings(this.mc, this.parentScreen, this.config));
      default:
      }
   }
}
