package vazkii.quark.base.client.gui.config;

import java.io.IOException;
import java.util.TreeSet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import vazkii.quark.base.client.ContributorRewardHandler;
import vazkii.quark.base.client.gui.GuiButtonColor;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleLoader;

public class GuiConfigRoot extends GuiConfigBase {
   private final boolean qEnabled = GlobalConfig.enableQButton;

   public GuiConfigRoot(GuiScreen parent) {
      super(parent);
   }

   public void initGui() {
      super.initGui();
      int startX = this.width / 2 - 175;
      int startY = this.height / 6 + 5;
      int i = 0;

      for(Module module : new TreeSet(ModuleLoader.moduleInstances.values())) {
         int x = startX + i % 2 * 180;
         int y = startY + i / 2 * 22;
         this.buttonList.add(new GuiButtonModule(x, y, module));
         this.buttonList.add(new GuiButtonConfigSetting(x + 150, y, module.prop, false));
         ++i;
      }

      int x = this.width / 2;
      int y = startY + 96;
      this.buttonList.add(new GuiButtonConfigSetting(x + 155, y + 13, GlobalConfig.qButtonProp, true, I18n.format("quark.config.enableq", new Object[0])));
      this.buttonList.add(new GuiButtonColor(3, x - 149, y + 44, 98, I18n.format("quark.config.opensite", new Object[0]), 4775356));
      this.buttonList.add(new GuiButtonColor(4, x - 49, y + 44, 98, I18n.format("quark.config.reddit", new Object[0]), 2070761));
      this.buttonList.add(new GuiButtonColor(5, x + 51, y + 44, 98, I18n.format("quark.config.donate", new Object[0]), 16345172));
      this.buttonList.add(new GuiButton(1, x - 149, y + 66, 98, 20, I18n.format("quark.config.general", new Object[0])));
      this.buttonList.add(new GuiButton(2, x - 49, y + 66, 98, 20, I18n.format("quark.config.import", new Object[0])));
      this.buttonList.add(this.backButton = new GuiButton(0, x + 51, y + 66, 98, 20, I18n.format("gui.done", new Object[0])));
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      String s = null;
      if (mayRequireRestart) {
         s = I18n.format("quark.config.needrestart", new Object[0]);
      } else if (this.qEnabled && !GlobalConfig.enableQButton) {
         s = I18n.format("quark.config.qdisabled", new Object[0]);
      }

      if (s != null) {
         this.drawCenteredString(this.mc.fontRenderer, s, this.width / 2, this.backButton.y + 22, 16776960);
      }

      if (ContributorRewardHandler.localPatronTier == 0) {
         if (ContributorRewardHandler.featuredPatron.isEmpty()) {
            s = I18n.format("quarkmisc.patronPlugNone", new Object[0]);
         } else {
            s = I18n.format("quarkmisc.patronPlug", new Object[]{ContributorRewardHandler.featuredPatron});
         }
      } else {
         s = "\u2665 " + I18n.format("quarkmisc.supportMessage", new Object[]{this.mc.getSession().getUsername()}) + " \u2665";
      }

      this.drawCenteredString(this.mc.fontRenderer, s, this.width / 2, 27, 16748416);
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      if (button instanceof GuiButtonModule) {
         GuiButtonModule moduleButton = (GuiButtonModule)button;
         this.mc.displayGuiScreen(new GuiConfigModule(this, moduleButton.module));
      } else {
         switch(button.id) {
         case 1:
            this.mc.displayGuiScreen(new GuiConfigCategory(this, "_global"));
            break;
         case 2:
            this.mc.displayGuiScreen(new GuiConfigImport(this));
            break;
         case 3:
            this.tryOpenWebsite("https://quark.vazkii.net");
            break;
         case 4:
            this.tryOpenWebsite("https://reddit.com/r/QuarkMod/");
            break;
         case 5:
            this.tryOpenWebsite("https://vazkii.net/#donate");
         }
      }

   }
}
