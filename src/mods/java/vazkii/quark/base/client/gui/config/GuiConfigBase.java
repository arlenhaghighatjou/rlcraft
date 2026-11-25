package vazkii.quark.base.client.gui.config;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Property;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.ModuleLoader;

public class GuiConfigBase extends GuiScreen {
   protected String title;
   protected final GuiScreen parent;
   private static final List<Property> restartRequiringProperties = new LinkedList();
   public static boolean mayRequireRestart = false;
   public GuiButton backButton;
   public String targetUrl;

   public GuiConfigBase(GuiScreen parent) {
      this.parent = parent;
   }

   public void initGui() {
      super.initGui();
      this.buttonList.clear();
      this.title = I18n.format("quark.config.title", new Object[0]);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, TextFormatting.BOLD + this.title, this.width / 2, 15, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1) {
         this.returnToParent();
      }

   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      if (this.backButton != null && button == this.backButton) {
         this.returnToParent();
      }

      if (button instanceof GuiButtonConfigSetting) {
         GuiButtonConfigSetting configButton = (GuiButtonConfigSetting)button;
         configButton.prop.set(!configButton.prop.getBoolean());
         if (configButton.prop.requiresMcRestart()) {
            if (restartRequiringProperties.contains(configButton.prop)) {
               restartRequiringProperties.remove(configButton.prop);
            } else {
               restartRequiringProperties.add(configButton.prop);
            }

            mayRequireRestart = !restartRequiringProperties.isEmpty();
         }

         ModuleLoader.loadConfig();
      }

   }

   void returnToParent() {
      this.mc.displayGuiScreen(this.parent);
      if (this.mc.currentScreen == null) {
         this.mc.setIngameFocus();
      }

   }

   void tryOpenWebsite(String url) {
      this.targetUrl = url;
      GuiConfirmOpenLink gui = new GuiConfigLink(this, url);
      this.mc.displayGuiScreen(gui);
   }

   public void confirmClicked(boolean result, int id) {
      if (id == 0) {
         try {
            if (result) {
               this.openWebLink(new URI(this.targetUrl));
            }
         } catch (URISyntaxException var4) {
            var4.printStackTrace();
         }

         this.mc.displayGuiScreen(this);
      }

   }

   private void openWebLink(URI url) {
      try {
         Class<?> desktopClass = Class.forName("java.awt.Desktop");
         Object object = desktopClass.getMethod("getDesktop").invoke((Object)null);
         desktopClass.getMethod("browse", URI.class).invoke(object, url);
      } catch (Throwable var4) {
         Throwable throwable = var4.getCause();
         Quark.LOG.warn("Couldn't open link: {}", throwable == null ? "<UNKNOWN>" : throwable.getMessage());
      }

   }
}
