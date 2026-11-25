package ivorius.reccomplex.gui;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RCConfigGuiFactory implements IModGuiFactory {
   public RCConfigGuiFactory() {
   }

   public void initialize(Minecraft minecraftInstance) {
   }

   public boolean hasConfigGui() {
      return true;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new RCConfigGuiFactory.ConfigGui(parentScreen);
   }

   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return null;
   }

   public static class ConfigGui extends GuiConfig {
      public ConfigGui(GuiScreen parentScreen) {
         super(parentScreen, getConfigElements(), "reccomplex", false, false, IvTranslations.format("reccomplex.configgui.title", new Object[0]));
      }

      private static List<IConfigElement> getConfigElements() {
         List<IConfigElement> list = new ArrayList();
         list.add((new DummyCategoryElement("reccomplex.configgui.general", "reccomplex.configgui.ctgy.general", RCConfigGuiFactory.ConfigGui.GeneralEntry.class)).setRequiresMcRestart(true));
         list.add(new DummyCategoryElement("reccomplex.configgui.balancing", "reccomplex.configgui.ctgy.balancing", RCConfigGuiFactory.ConfigGui.BalancingEntry.class));
         list.add(new DummyCategoryElement("reccomplex.configgui.visual", "reccomplex.configgui.ctgy.visual", RCConfigGuiFactory.ConfigGui.VisualEntry.class));
         list.add(new DummyCategoryElement("reccomplex.configgui.controls", "reccomplex.configgui.ctgy.controls", RCConfigGuiFactory.ConfigGui.ControlsEntry.class));
         list.add(new DummyCategoryElement("reccomplex.configgui.decoration", "reccomplex.configgui.ctgy.decoration", RCConfigGuiFactory.DecorationEntry.class));
         return list;
      }

      public static class BalancingEntry extends CategoryEntry {
         public BalancingEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
         }

         protected GuiScreen buildChildScreen() {
            return new GuiConfig(this.owningScreen, (new ConfigElement(RecurrentComplex.config.getCategory("balancing"))).getChildElements(), this.owningScreen.modID, "balancing", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, GuiConfig.getAbridgedConfigPath(RecurrentComplex.config.toString()));
         }
      }

      public static class ControlsEntry extends CategoryEntry {
         public ControlsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
         }

         protected GuiScreen buildChildScreen() {
            return new GuiConfig(this.owningScreen, (new ConfigElement(RecurrentComplex.config.getCategory("controls"))).getChildElements(), this.owningScreen.modID, "controls", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, GuiConfig.getAbridgedConfigPath(RecurrentComplex.config.toString()));
         }
      }

      public static class GeneralEntry extends CategoryEntry {
         public GeneralEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
         }

         protected GuiScreen buildChildScreen() {
            return new GuiConfig(this.owningScreen, (new ConfigElement(RecurrentComplex.config.getCategory("general"))).getChildElements(), this.owningScreen.modID, "general", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, GuiConfig.getAbridgedConfigPath(RecurrentComplex.config.toString()));
         }
      }

      public static class VisualEntry extends CategoryEntry {
         public VisualEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
         }

         protected GuiScreen buildChildScreen() {
            return new GuiConfig(this.owningScreen, (new ConfigElement(RecurrentComplex.config.getCategory("visual"))).getChildElements(), this.owningScreen.modID, "visual", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, GuiConfig.getAbridgedConfigPath(RecurrentComplex.config.toString()));
         }
      }
   }

   public static class DecorationEntry extends CategoryEntry {
      public DecorationEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
         super(owningScreen, owningEntryList, prop);
      }

      protected GuiScreen buildChildScreen() {
         return new GuiConfig(this.owningScreen, (new ConfigElement(RecurrentComplex.config.getCategory("decoration"))).getChildElements(), this.owningScreen.modID, "decoration", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, GuiConfig.getAbridgedConfigPath(RecurrentComplex.config.toString()));
      }
   }
}
