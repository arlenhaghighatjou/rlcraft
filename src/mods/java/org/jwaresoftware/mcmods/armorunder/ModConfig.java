package org.jwaresoftware.mcmods.armorunder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.jwaresoftware.mcmods.lib.Strings;
import org.jwaresoftware.mcmods.lib.impl.ModConfigBase;

public final class ModConfig extends ModConfigBase {
   static final String _GENERAL = "General";
   static final String _INTEGRATION = "Integration";
   private static ModConfig _sharedINSTANCE = new ModConfig();
   private CreativeTabs _ourTab;

   public ModConfig() {
   }

   public ModConfig(FMLPreInitializationEvent event) {
      super(ModInfo.getInstance(), event);
   }

   public ModConfig setInstance() {
      _sharedINSTANCE = this;
      return this;
   }

   public static final ModConfig getInstance() {
      return _sharedINSTANCE;
   }

   public boolean useModTab() {
      String PROPERTY = "use_custom_tab";
      Boolean b = (Boolean)this._cache.get("Generaluse_custom_tab");
      if (b != null) {
         return b.booleanValue();
      } else {
         Property p = this.forgeConfig().get("General", "use_custom_tab", true, "Set to true to force all mod items and blocks to a single custom tab in creative views. Enabled by default.");
         p.setLanguageKey("armorunder.configui.use_custom_tab");
         b = p.getBoolean();
         this._cache.put("Generaluse_custom_tab", b);
         return b.booleanValue();
      }
   }

   public CreativeTabs getTab() {
      return this._ourTab;
   }

   public void setTab(CreativeTabs tab) {
      this._ourTab = tab;
      this._cache.put("pinklyTab", tab);
   }

   public final boolean isAllowed(@Nonnull String option, boolean defaultValue) {
      if ("special_sets".equals(option)) {
         return this.includeSpecialLiners();
      } else if (!"craft_smart_lining".equals(option)) {
         if ("loot_tables".equals(option)) {
            return this.allowLootTableEnrichment();
         } else {
            return "portable_paks".equals(option) ? this.includePortablePaks() : super.isAllowed(option, defaultValue);
         }
      } else {
         return this.allowSpecialLinerCrafting(defaultValue) && this.includeSpecialLiners();
      }
   }

   public final boolean isDebugMode() {
      String PROPERTY = "debug_mode";
      Boolean b = (Boolean)this._cache.get("Generaldebug_mode");
      if (b != null) {
         return b.booleanValue();
      } else {
         Property p = this.forgeConfig().get("General", "debug_mode", false, "INTERNAL. Development messages enabled.");
         p.setLanguageKey("armorunder.configui.debug_mode");
         b = p.getBoolean();
         this._cache.put("Generaldebug_mode", b);
         return b.booleanValue();
      }
   }

   public boolean isSuperCompatibilityMode() {
      String PROPERTY = "super_compatibility_mode";
      Boolean b = (Boolean)this._cache.get("Generalsuper_compatibility_mode");
      if (b != null) {
         return b.booleanValue();
      } else {
         Property p = this.forgeConfig().get("General", "super_compatibility_mode", true, "Set to true to force all the basic recipes to use a mod-specific element to ensure no collisions with other mod recipes. If turned ON you cannot use TAN armor as liners directly. Off by default; requires restart if changed in-game.");
         p.setLanguageKey("armorunder.configui.super_compatibility_mode");
         b = p.getBoolean();
         this._cache.put("Generalsuper_compatibility_mode", b);
         return b.booleanValue();
      }
   }

   public boolean allowLootTableEnrichment() {
      String PROPERTY = "enrich_loot_tables";
      Boolean b = (Boolean)this._cache.get("Generalenrich_loot_tables");
      if (b != null) {
         return b.booleanValue();
      } else {
         Property p = this.forgeConfig().get("General", "enrich_loot_tables", true, "Set to false to disable all mod-specific additions to vanilla structure and activity loot tables. Loot is the only way to obtain the custom liners like the Ozzys and Ollies. Enabled by default; requires restart if changed in-game.");
         p.setLanguageKey("armorunder.configui.enrich_loot_tables");
         b = p.getBoolean();
         this._cache.put("Generalenrich_loot_tables", b);
         return b.booleanValue();
      }
   }

   public boolean includePortablePaks() {
      String PROPERTY = "include_goopaks";
      Boolean b = (Boolean)this._cache.get("Generalinclude_goopaks");
      if (b != null) {
         return b.booleanValue();
      } else {
         Property p = this.forgeConfig().get("General", "include_goopaks", true, "Set to false to disable the portable 'goo paks' that you carry in your hotbar. Paks are a useful supplement to regular armor liners in extreme environments. Included by default; requires restart if changed in-game.");
         p.setLanguageKey("armorunder.configui.include_goopaks");
         b = p.getBoolean();
         this._cache.put("Generalinclude_goopaks", b);
         return b.booleanValue();
      }
   }

   public boolean includeSpecialLiners() {
      String PROPERTY = "include_special_liners";
      Boolean b = (Boolean)this._cache.get("Generalinclude_special_liners");
      if (b != null) {
         return b.booleanValue();
      } else {
         Property p = this.forgeConfig().get("General", "include_special_liners", false, "Set to true to enable special liners including their damage mitigation features. These items are available only as loot rewards in the standalone mod. Disabled by default; requires restart if changed in-game.");
         p.setLanguageKey("armorunder.configui.include_special_liners");
         b = p.getBoolean();
         this._cache.put("Generalinclude_special_liners", b);
         return b.booleanValue();
      }
   }

   public boolean allowSpecialLinerCrafting(boolean default_value) {
      return _getBooleanOrDefault(this._allowSpecialLinerCrafting(), default_value);
   }

   private String _allowSpecialLinerCrafting() {
      String PROPERTY = "special_liners_craftable";
      String s = (String)this._cache.get("Generalspecial_liners_craftable");
      if (s != null) {
         return s;
      } else {
         s = this.forgeConfig().getString("special_liners_craftable", "General", "*DEFAULT*", "Set to true to include crafting recipes for items that are normally obtained through other mechanisms like looting or advancements. By default, only the ToughAsNails temperature-related 'Ozzy' liners are craftable.", Strings.__DEFAULT_VAL_SETTINGS(), "armorunder.configui.special_liners_craftable");
         this._cache.put("Generalspecial_liners_craftable", s);
         return s;
      }
   }

   @Nonnull
   public Map<String, Item[]> getUnderwearReadyArmorAll() {
      String PROPERTY = "armor";
      Map<String, Item[]> values = (Map)this._cache.get("Integrationarmor");
      if (values != null) {
         return values;
      } else {
         Map<String, Item[]> var3 = new HashMap();
         var3.put("itemBoots", this.getLiningCapableArmor(false, "boots", "boots", new String[0]));
         var3.put("itemChestplate", this.getLiningCapableArmor(false, "chestplate", "chestplates", new String[0]));
         var3.put("itemHelmet", this.getLiningCapableArmor(false, "helmet", "helmets", new String[0]));
         var3.put("itemLeggings", this.getLiningCapableArmor(false, "leggings", "leggings", new String[0]));
         this._cache.put("Integrationarmor", var3);
         return var3;
      }
   }

   private void defineUnderwearReadyArmorSection() {
      String armorCategory = "Integration".toLowerCase(Locale.US);
      ConfigCategory c = this.forgeConfig().getCategory(armorCategory);
      c.setComment("Armor that can be lined in addition to vanilla armor pieces. Requires restart if changed.");
      c.setLanguageKey("armorunder.configui.armor");
      this.getLiningCapableArmor(true, "boots", "boots", new String[]{"basemetals:coldiron_boots", "cyclicmagic:purple_boots"});
      this.getLiningCapableArmor(true, "chestplate", "chestplates", new String[]{"basemetals:coldiron_chestplate", "cyclicmagic:purple_chestplate"});
      this.getLiningCapableArmor(true, "helmet", "helmets", new String[]{"basemetals:coldiron_helmet", "cyclicmagic:purple_helmet"});
      this.getLiningCapableArmor(true, "leggings", "leggings", new String[]{"basemetals:coldiron_leggings", "cyclicmagic:purple_leggings"});
   }

   private Item[] getLiningCapableArmor(boolean define_only, String sub_category, String description, String[] defaults) {
      Item[] values = (Item[])this._cache.get("Integration" + sub_category);
      if (!define_only && values != null) {
         return values;
      } else {
         String[] ids = this.forgeConfig().getStringList(sub_category, "Integration", defaults, "Set to the full IDs of all modded " + description + " that can be worn over lining up to 3 layers thick (actual max thickness depends on armor piece). Some examples shown. Requires a restart if changed in-game.", (String[])null, "armorunder.configui." + sub_category);
         if (define_only) {
            return null;
         } else {
            values = new Item[ids.length];
            int i = 0;

            for(String id : ids) {
               values[i] = null;
               Item item = (Item)Item.REGISTRY.getObject(new ResourceLocation(id));
               if (item instanceof ItemArmor) {
                  values[i] = item;
               }

               ++i;
            }

            this._cache.put("Integration" + sub_category, values);
            return values;
         }
      }
   }

   public int getLiningThicknessLimit() {
      String PROPERTY = "thickness_limit";
      Integer n = (Integer)this._cache.get("Generalthickness_limit");
      if (n != null) {
         return n.intValue();
      } else {
         Property p = this.forgeConfig().get("General", "thickness_limit", 3, "Set to the maximum number of plain liners a single piece of armor can have. Applies to all types of armor pieces (helmets, chestplates, leggings, boots). Does not apply to special types of liners.", 1, 3);
         p.setLanguageKey("armorunder.configui.thickness_limit");
         n = MathHelper.clamp(p.getInt(), 1, 3);
         this._cache.put("Generalthickness_limit", n);
         return n.intValue();
      }
   }

   protected void fullyDefine() {
      this.useModTab();
      this.isDebugMode();
      this.isSuperCompatibilityMode();
      this.allowLootTableEnrichment();
      this.getLiningThicknessLimit();
      this.defineUnderwearReadyArmorSection();
      this.includePortablePaks();
      this.includeSpecialLiners();
      this._allowSpecialLinerCrafting();
   }
}
