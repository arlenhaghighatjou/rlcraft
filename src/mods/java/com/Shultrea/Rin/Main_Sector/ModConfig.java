package com.Shultrea.Rin.Main_Sector;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(
   modid = "somanyenchantments"
)
public class ModConfig {
   @Comment({"Enabled Enchantments"})
   @Name("Enabled Enchantments")
   public static EnabledConfig enabled = new EnabledConfig();
   @Comment({"Maximum levels of each enchantment"})
   @Name("Enchantment Levels")
   public static LevelConfig level = new LevelConfig();
   @Comment({"Miscellaneous"})
   @Name("Miscellaneous")
   public static ModConfig.MiscellaneousConfig miscellaneous = new ModConfig.MiscellaneousConfig();

   public ModConfig() {
   }

   @EventBusSubscriber(
      modid = "somanyenchantments"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      @SideOnly(Side.CLIENT)
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("somanyenchantments")) {
            ConfigManager.sync("somanyenchantments", Type.INSTANCE);
         }

      }
   }

   public static class MiscellaneousConfig {
      @Comment({"Allow enchantments to change the weather"})
      @Name("Allow Weather Change")
      public boolean EnableWeatherChange = true;
      @Comment({"XP Orbs give double XP (this is a bug present in older versions)"})
      @Name("Double XP Orbs")
      public boolean EnableDoubleXPBug = false;
      @Comment({"Fixes enchantment that doesn't deal damage when damage is 0 (E.g having weakness while hitting with a wood sword doesn't let the enchantment deal the damage because negative damage cancels damage calculation.)"})
      @Name("Fix Zero Damage Enchantments")
      public boolean EnableFixEnchantment = false;
      @Comment({"Evasion Enchantment performs a dodge-like action to the Player. Disable if you don't like falling into cliff that is caused by Evasion Enchantment."})
      @Name("Evasion Dodge Roll")
      public boolean EvasionKnockback = true;
      @Comment({"Restrict Quarrying to Ore blocks only."})
      @Name("Quarrying Ore Only")
      public boolean QuarryingOreOnly = true;
      @Comment({"Allow Quarrying to work with blocks that are tile entities. I don't know what blocks are these but it may be useful for other mods..."})
      @Name("Quarrying Tile Entities")
      public boolean harvestTile = true;
      @Comment({"Unregister disabled enchantments"})
      @Name("Unregister Disabled Enchants")
      @RequiresMcRestart
      public boolean unregisterDisabled = true;
      @Comment({"Specify what potion effects are blacklisted from enchantments that add potion effects (use the full registry name, I.E. minecraft:"})
      @Name("Potion Blacklist")
      @RequiresMcRestart
      public String[] potionBlacklist = new String[]{"examplemod:registryname"};
      @Comment({"Whether the blacklist should be treated as a whitelist"})
      @Name("Potion Blacklist as Whitelist")
      @RequiresMcRestart
      public boolean potionBlacklistAsWhitelist = false;
      @Comment({"Whether enchantments should work with pet attacks"})
      @Name("Enable Pet Attacks")
      public boolean enablePetAttacks = false;
      @Comment({"Enables extra protection effects"})
      @Name("Extra Protection Effects")
      public boolean extraProtectionEffects = true;

      public MiscellaneousConfig() {
      }
   }
}
