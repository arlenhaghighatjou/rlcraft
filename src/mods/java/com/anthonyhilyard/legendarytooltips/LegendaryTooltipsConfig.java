package com.anthonyhilyard.legendarytooltips;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.anthonyhilyard.legendarytooltips.util.Selectors;
import com.anthonyhilyard.legendarytooltips.util.TextColor;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.relauncher.Side;

public class LegendaryTooltipsConfig extends Configuration {
   public static LegendaryTooltipsConfig INSTANCE;
   public static final String DEFAULT_START_COLOR = "#FF996922";
   public static final String DEFAULT_END_COLOR = "#FF5A3A1D";
   public static final String DEFAULT_BG_COLOR = "#F0160A00";
   public boolean nameSeparator;
   public boolean bordersMatchRarity;
   public boolean tooltipShadow;
   public boolean shineEffect;
   public boolean shineRepeat;
   public boolean shineSync;
   public int shineTicks;
   private final List<List<String>> itemSelectors = new ArrayList(16);
   private final List<Integer> framePriorities = new ArrayList();
   private String[] startColors = new String[16];
   private String[] endColors = new String[16];
   private String[] bgColors = new String[16];
   private final transient Map<ItemStack, Integer> frameLevelCache = new HashMap();

   public static void loadConfig(VFile2 file) {
      INSTANCE = new LegendaryTooltipsConfig(file);
   }

   private LegendaryTooltipsConfig(VFile2 file) {
      super(file);
      this.load();

      Map<String, ConfigCategory> ordered = new LinkedHashMap();
      ordered.put("visual_options", this.getCategory("visual_options"));
      ordered.put("definitions", this.getCategory("definitions"));
      ordered.put("priorities", this.getCategory("priorities"));
      ordered.put("colors", this.getCategory("colors"));
      this.categories = ordered;
      this.getCategory("visual_options").setComment(" Legendary Tooltips Configuration Instructions\n\n *** READ THIS FIRST ***\n\n By default, this mod does not apply special borders to most items.  It was designed to work well with mod packs\n where the available selection of items can vary widely, so it is up to the user or mod pack designer to customize as needed.\n There are many options available for setting up which custom borders (also called frames) apply to which items.  Follow these steps:\n   1. Decide which items you want to have custom borders, and which borders.  Note that each custom border has a number associated with it (starting at 0).\n   2. For each custom border you want to use, fill out the associated list in the \"definitions\" section.  This will be filled out with a list of \"selectors\",\n      each of which tell the mod what items have that border.  Please read the information above the definitions section for specifics.\n   3. Selectors for borders are checked in the order provided in the \"priorities\" section.  Once a match is found, that border is displayed.\n      For example, if border 0 had the selector \"%Diamond\" and border 1 had the selector \"diamond_sword\", they would both match for diamond swords.\n      In this case, whichever border number came first in the priority list would be the border that would get drawn in-game.\n   4. Optionally, border colors associated with custom borders can be set in the \"colors\" section.  The start color is the color at the top of the tooltip,\n      and the end color is the bottom, with a smooth transition between.  Please read the information above the color section for specifics.");
      List<String> definitionOrder = new ArrayList(16);
      List<String> colorOrder = new ArrayList(32);

      for(int i = 0; i < 16; ++i) {
         definitionOrder.add("level" + i + "_entries");
         colorOrder.add("level" + i + "_start_color");
         colorOrder.add("level" + i + "_end_color");
      }

      this.getCategory("definitions").setPropertyOrder(definitionOrder).setComment(" Entry types:\n   Item name - Use item name for vanilla items or include mod name for modded items.  Examples: minecraft:stick, iron_ore\n   Item Metadata - After an item name, you may include a colon and then a number to indicate metadata.  Examples: minecraft:wool:5, minecraft:sponge:1\n   Tag - $ followed by ore dictionary tag name.  Examples: $plankWood or $ingotIron\n   Mod name - @ followed by mod identifier.  Examples: @spoiledeggs\n   Rarity - ! followed by item's rarity.  Examples: !uncommon, !rare !epic\n   Item name color - # followed by color hex code, the hex code must match exactly.  Examples: #23F632\n   Display name - % followed by any text.  Will match any item with this text in its tooltip display name.  Examples: %Uncommon\n   Tooltip text - ^ followed by any text.  Will match any item with this text anywhere in the tooltip text (besides the name).  Examples: ^Legendary\n   NBT tag - & followed by tag name and optional comparator (=, >, <, or !=) and value, in the format <tag><comparator><value> or just <tag>.\n             Examples: &Damage=0, &Tier>1, &Enchantments, &map!=128");
      this.getCategory("priorities").setComment("Set border priorities here.  This should be a string containing a comma-separated list of numbers that\ncorrespond to border levels, with numbers coming first being higher priority.");
      this.getCategory("colors").setPropertyOrder(colorOrder).setComment("The start and end border colors and background colors of each level.\n Note that they can be entered as any one of: a hex color code in the format #AARRGGBB or #RRGGBB, OR a string color name.\n Examples: #FFFF00, #FF73D984, red, #FFCC00");
      this.nameSeparator = this.getBoolean("name_separator", "visual_options", true, "Whether item names in tooltips should have a line under them separating them from the rest of the tooltip.");
      this.bordersMatchRarity = this.getBoolean("borders_match_rarity", "visual_options", true, "If enabled, tooltip border colors will match item rarity colors (except for custom borders).");
      this.tooltipShadow = this.getBoolean("tooltip_shadow", "visual_options", true, "If enabled, tooltips will display a drop shadow.");
      this.shineEffect = this.getBoolean("shine_effect", "visual_options", true, "If enabled, items showing a custom border will have a special shine effect when hovered over.");
      this.shineRepeat = this.getBoolean("shine_repeat", "visual_options", false, "Whether or not to repeat the shine effect, or to only play it once.");
      this.shineSync = this.getBoolean("shine_sync", "visual_options", false, "Whether or not to sync horizontal and vertical shine, or delay vertical shine.");
      this.shineTicks = this.getInt("shine_ticks", "visual_options", 50, 20, 800, "The speed at which to play the shine effect, higher value is slower.");
      this.itemSelectors.add(Arrays.asList(this.getStringList("level0_entries", "definitions", new String[]{"!epic", "!rare"}, "")));

      for(int i = 1; i < 16; ++i) {
         this.itemSelectors.add(Arrays.asList(this.getStringList("level" + i + "_entries", "definitions", new String[0], "")));
      }

      this.framePriorities.clear();
      String[] priorities = this.getString("priorities", "priorities", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15", "").split(",");

      for(int i = 0; i < priorities.length; ++i) {
         this.framePriorities.add(Integer.parseInt(priorities[i].trim()));
      }
      this.startColors[0] = this.getString("level0_start_color", "colors", "#FF996922", "");
      this.endColors[0] = this.getString("level0_end_color", "colors", "#FF5A3A1D", "");
      this.bgColors[0] = this.getString("level0_bg_color", "colors", "#F0160A00", "");

      for(int i = 1; i < 16; ++i) {
         this.startColors[i] = this.getString("level" + i + "_start_color", "colors", "#FF996922", "");
         this.endColors[i] = this.getString("level" + i + "_end_color", "colors", "#FF5A3A1D", "");
         this.bgColors[i] = this.getString("level" + i + "_bg_color", "colors", "#F0160A00", "");
      }

      this.save();
   }

   public int getFrameLevelForItem(ItemStack item) {
      if (item.isEmpty()) {
         return -1;
      }

      if (this.frameLevelCache.containsKey(item)) {
         return ((Integer)this.frameLevelCache.get(item)).intValue();
      } else {
         for(int i = 0; i < 16; ++i) {
            if (i < this.framePriorities.size()) {
               int frameIndex = ((Integer)this.framePriorities.get(i)).intValue();
               if (frameIndex < this.itemSelectors.size()) {
                  for(String entry : (List<String>)this.itemSelectors.get(frameIndex)) {
                     if (Selectors.itemMatches(item, entry)) {
                        this.frameLevelCache.put(item, Integer.valueOf(frameIndex));
                        return frameIndex;
                     }
                  }
               }
            }
         }

         this.frameLevelCache.put(item, Integer.valueOf(-1));
         return -1;
      }
   }

   public static void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, LegendaryTooltipsConfig::onLoad);
   }

   public static void onLoad(OnConfigChangedEvent e) {
      if (e.getModID().equals("legendarytooltips")) {
         ConfigManager.sync("legendarytooltips", Type.INSTANCE);
         INSTANCE.frameLevelCache.clear();
      }

   }

   private Integer getColor(String colorString) {
      colorString = colorString.toLowerCase().replace("0x", "").replace("#", "");
      Integer color = TextColor.parseColor(colorString);
      if (color == null && (colorString.length() == 6 || colorString.length() == 8)) {
         color = TextColor.parseColor("#" + colorString);
      }

      return color;
   }

   public Integer getCustomBorderStartColor(int level) {
      if (level >= 0 && level <= 15 && this.startColors[level] != null) {
         Integer startColor = this.getColor(this.startColors[level]);
         return startColor.intValue() > 0 && startColor.intValue() <= 16777215 ? startColor.intValue() | -16777216 : startColor;
      } else {
         return null;
      }
   }

   public Integer getCustomBorderEndColor(int level) {
      if (level >= 0 && level <= 15 && this.endColors[level] != null) {
         Integer endColor = this.getColor(this.endColors[level]);
         return endColor.intValue() > 0 && endColor.intValue() <= 16777215 ? endColor.intValue() | -16777216 : endColor;
      } else {
         return null;
      }
   }

   public Integer getCustomBackgroundColor(int level) {
      if (level >= 0 && level <= 15 && this.bgColors[level] != null) {
         Integer bgColor = this.getColor(this.bgColors[level]);
         return bgColor.intValue() > 0 && bgColor.intValue() <= 16777215 ? bgColor.intValue() | -16777216 : bgColor;
      } else {
         return null;
      }
   }
}
