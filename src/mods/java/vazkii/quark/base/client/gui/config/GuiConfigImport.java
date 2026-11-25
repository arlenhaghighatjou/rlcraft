package vazkii.quark.base.client.gui.config;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class GuiConfigImport extends GuiConfigBase {
   private boolean needsRestart = false;
   private int disabledFeatures;
   private GuiTextField textField;

   public GuiConfigImport(GuiScreen parent) {
      super(parent);
   }

   public void initGui() {
      super.initGui();
      this.title = this.title + " - " + I18n.format("quark.config.import", new Object[0]);
      int x = this.width / 2 - 100;
      int y = this.height / 6;
      GuiButton importButton = new GuiButton(1, x, y + 110, 200, 20, I18n.format("quark.config.import", new Object[0]));
      this.buttonList.add(this.backButton = new GuiButton(0, x, y + 167, 200, 20, I18n.format("gui.done", new Object[0])));
      this.buttonList.add(importButton);
      this.buttonList.add(new GuiButton(2, x, y + 132, 200, 20, I18n.format("quark.config.opensite", new Object[0])));
      this.textField = new GuiTextField(0, this.fontRenderer, x, y + 72, 200, 20);
      this.textField.setFocused(true);
      this.textField.setCanLoseFocus(false);
      this.textField.setMaxStringLength(Integer.MAX_VALUE);
      if (this.mc.world != null) {
         importButton.enabled = false;
      }

   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      switch(button.id) {
      case 1:
         if (!this.needsRestart) {
            this.doImport();
            if (this.needsRestart) {
               button.displayString = I18n.format("quark.config.close", new Object[0]);

               for(GuiButton b : this.buttonList) {
                  if (b != button) {
                     b.enabled = false;
                  }
               }
            }
         } else {
            FMLCommonHandler.instance().exitJava(0, false);
         }
         break;
      case 2:
         this.tryOpenWebsite("https://quark.vazkii.net");
         break;
      case 3:
         this.tryOpenWebsite("https://quark.vazkii.net");
      }

   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (!this.needsRestart) {
         super.keyTyped(typedChar, keyCode);
         this.textField.textboxKeyTyped(typedChar, keyCode);
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.textField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.textField.drawTextBox();
      int x = this.width / 2;

      for(int i = 0; i < 4; ++i) {
         int y = 50 + i * 10;
         String key = "quark.config.importinfo" + i;
         if (i == 3) {
            if (Minecraft.IS_RUNNING_ON_MAC) {
               key = key + "m";
            }

            y += 5;
         }

         this.drawCenteredString(this.mc.fontRenderer, I18n.format(key, new Object[0]), x, y, 16777215);
      }

      if (this.needsRestart) {
         String s;
         if (this.disabledFeatures == 1) {
            s = I18n.format("quark.config.disabledcount1", new Object[0]);
         } else {
            s = I18n.format("quark.config.disabledcount", new Object[]{this.disabledFeatures});
         }

         this.drawCenteredString(this.mc.fontRenderer, s, x, this.textField.y + 26, 65280);
      }

      if (this.mc.world != null) {
         this.drawCenteredString(this.mc.fontRenderer, I18n.format("quark.config.cantimport", new Object[0]), x, this.textField.y + 26, 16711680);
      }

   }

   private void doImport() {
      boolean changed = false;
      this.disabledFeatures = 0;
      String[] disables = this.textField.getText().trim().split(",");
      if (disables.length > 0) {
         Set<String> disabledSet = ImmutableSet.copyOf(disables);

         for(String name : ModuleLoader.featureClassnames.keySet()) {
            Feature f = (Feature)ModuleLoader.featureClassnames.get(name);
            boolean enabled = disabledSet.contains(name) != f.enabledByDefault;
            if (f.prop.getBoolean() != enabled) {
               f.prop.set(enabled);
            }

            if (f.prop.hasChanged()) {
               changed = true;
               if (!enabled) {
                  ++this.disabledFeatures;
               }
            }
         }
      }

      this.needsRestart = false;
      if (changed) {
         ModuleLoader.loadConfig();
         this.needsRestart = true;
      }

   }
}
