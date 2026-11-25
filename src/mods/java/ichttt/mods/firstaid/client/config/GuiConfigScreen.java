package ichttt.mods.firstaid.client.config;

import ichttt.mods.firstaid.client.ClientProxy;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfigScreen extends GuiConfig {
   private final List<IConfigEntry> removedEntries = new ArrayList();
   private static boolean isAdvanced = false;
   private GuiButton button;

   public GuiConfigScreen(GuiScreen parentScreen) {
      super(parentScreen, "firstaid", "First Aid");
   }

   public GuiConfigScreen(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title, String titleLine2) {
      super(parentScreen, configElements, modID, allRequireWorldRestart, allRequireMcRestart, title, titleLine2);
   }

   public void initGui() {
      this.entryList.listEntries.addAll(this.removedEntries);
      super.initGui();
      this.entryList.listEntries.removeAll(this.removedEntries);
      this.setupButton();
   }

   private void setupButton() {
      if (this.button != null) {
         this.buttonList.remove(this.button);
      }

      this.button = new GuiButton(1999, 0, 0, 80, 20, "Show " + (isAdvanced ? "less" : "more"));
      this.buttonList.add(this.button);
      if (isAdvanced) {
         this.entryList.listEntries.addAll(this.removedEntries);
         this.removedEntries.clear();
      } else {
         ListIterator<IConfigEntry> iterator = this.entryList.listEntries.listIterator();

         while(iterator.hasNext()) {
            IConfigEntry next = (IConfigEntry)iterator.next();
            if (ClientProxy.advancedConfigOptions.stream().anyMatch((entryx) -> {
               return entry.property.matches(next.getConfigElement());
            })) {
               this.removedEntries.add(next);
               iterator.remove();
            }
         }
      }

      for(int i = 0; i < this.entryList.listEntries.size(); ++i) {
         IConfigEntry guiEntry = (IConfigEntry)this.entryList.listEntries.get(i);
         if (guiEntry instanceof CategoryEntry && !(guiEntry instanceof CustomCategoryEntry)) {
            CategoryEntry entry = (CategoryEntry)guiEntry;
            this.entryList.listEntries.set(i, new CustomCategoryEntry(this, this.entryList, entry.getConfigElement()));
         }
      }

   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 1999) {
         isAdvanced = !isAdvanced;
         this.setupButton();
      } else if (button.id == 2000 && this.parentScreen instanceof GuiModList) {
         isAdvanced = false;
         super.actionPerformed(button);
      } else {
         super.actionPerformed(button);
      }

   }
}
