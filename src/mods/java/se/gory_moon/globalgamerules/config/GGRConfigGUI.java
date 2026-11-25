package se.gory_moon.globalgamerules.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.SelectValueEntry;
import se.gory_moon.globalgamerules.GlobalGR;

public class GGRConfigGUI extends GuiConfig {
   public GGRConfigGUI(GuiScreen parentScreen) {
      super(parentScreen, getConfigElements(), "globalgamerules", false, false, GuiConfig.getAbridgedConfigPath(GlobalGR.getConfig().toString()));
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> list = new ArrayList();
      list.add(new DummyCategoryElement("gamerules", "ggr.configgui.rules", (new ConfigElement(GlobalGR.getConfig().getCategory("gamerules"))).getChildElements(), CategoryEntry.class));
      list.add(new DummyCategoryElement("misc", "ggr.configgui.misc", (new ConfigElement(GlobalGR.getConfig().getCategory("misc"))).getChildElements(), CategoryEntry.class));
      return list;
   }

   public static class DifficultyEntry extends SelectValueEntry {
      public DifficultyEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
         super(owningScreen, owningEntryList, prop, getSelectableValues());
      }

      private static Map<Object, String> getSelectableValues() {
         Map<Object, String> selectableValues = new HashMap();
         selectableValues.put("-1", I18n.format("ggr.configgui.diffDisabled", new Object[0]));

         for(EnumDifficulty ed : EnumDifficulty.values()) {
            selectableValues.put(String.valueOf(ed.getDifficultyId()), I18n.format(ed.getDifficultyResourceKey(), new Object[0]));
         }

         return selectableValues;
      }

      public void updateValueButtonText() {
         int val = Integer.parseInt(String.valueOf(this.currentValue));
         if (val > -1) {
            this.btnValue.displayString = I18n.format(EnumDifficulty.getDifficultyEnum(val).getDifficultyResourceKey(), new Object[0]);
         } else {
            this.btnValue.displayString = I18n.format("ggr.configgui.diffDisabled", new Object[0]);
         }

      }
   }
}
