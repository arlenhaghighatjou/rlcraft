package vazkii.quark.base.client.gui.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleLoader;

public class GuiConfigModule extends GuiConfigBase {
   private static final int FEATURES_PER_PAGE = 12;
   private final Module module;
   private final List<Feature> features;
   private int page = 0;
   private final int totalPages;
   private GuiButton left;
   private GuiButton right;

   public GuiConfigModule(GuiScreen parent, Module module) {
      super(parent);
      this.module = module;
      this.features = new ArrayList();
      List var10001 = this.features;
      this.features.getClass();
      module.forEachFeature(var10001::add);
      Collections.sort(this.features);
      this.totalPages = (this.features.size() - 1) / 12 + 1;
   }

   public void initGui() {
      super.initGui();
      this.title = this.title + " - " + I18n.format("quark.config.module." + this.module.name, new Object[0]) + " (" + this.features.size() + ")";
      int x = this.width / 2 - 100;
      int y = this.height / 6 + 167;
      this.buttonList.add(this.backButton = new GuiButton(0, x, y, 200, 20, I18n.format("gui.done", new Object[0])));
      if (this.totalPages > 1) {
         x = this.width / 2;
         y = this.height / 6 - 12;
         this.buttonList.add(this.left = new GuiButton(0, x - 40, y, 20, 20, "<"));
         this.buttonList.add(this.right = new GuiButton(0, x + 20, y, 20, 20, ">"));
      }

      this.addFeatureButtons();
   }

   public void addFeatureButtons() {
      int startX = this.width / 2 - 195;
      int startY = this.height / 6 + 20;
      this.buttonList.removeIf((b) -> {
         return b instanceof GuiButtonConfigSetting || b instanceof GuiButtonFeatureSettings;
      });
      int start = this.page * 12;

      for(int i = start; i < Math.min(start + 12, this.features.size()); ++i) {
         int j = i - start;
         int x = startX + j % 2 * 200;
         int y = startY + j / 2 * 22;
         Feature feature = (Feature)this.features.get(i);
         this.buttonList.add(new GuiButtonConfigSetting(x + 150, y, feature.prop, true, feature.getFeatureIngameConfigName()));
         if (ModuleLoader.config.hasCategory(feature.configCategory)) {
            this.buttonList.add(new GuiButtonFeatureSettings(x + 170, y, feature.configCategory));
         }
      }

      if (this.left != null) {
         this.left.enabled = this.page > 0;
         this.right.enabled = this.page < this.totalPages - 1;
      }

   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      if (button instanceof GuiButtonFeatureSettings) {
         GuiButtonFeatureSettings featureButton = (GuiButtonFeatureSettings)button;
         this.mc.displayGuiScreen(new GuiConfigCategory(this, featureButton.category));
      } else if (button == this.left || button == this.right) {
         if (button == this.left) {
            this.page = Math.max(this.page - 1, 0);
         } else {
            this.page = Math.min(this.page + 1, this.totalPages - 1);
         }

         this.addFeatureButtons();
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.totalPages > 1) {
         int x = this.width / 2;
         int y = this.height / 6 - 7;
         this.drawCenteredString(this.mc.fontRenderer, this.page + 1 + "/" + this.totalPages, x, y, 16777215);
      }

      if (mayRequireRestart) {
         String s = I18n.format("quark.config.needrestart", new Object[0]);
         this.drawCenteredString(this.mc.fontRenderer, s, this.width / 2, this.backButton.y + 22, 16776960);
      }

   }
}
