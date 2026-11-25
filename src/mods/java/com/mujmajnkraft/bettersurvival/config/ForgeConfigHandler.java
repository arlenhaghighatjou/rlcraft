package com.mujmajnkraft.bettersurvival.config;

import com.mujmajnkraft.bettersurvival.BetterSurvival;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

@Config(
   modid = "mujmajnkraftsbettersurvival"
)
public class ForgeConfigHandler {
   @Comment({"Weapon Config (server)"})
   @Name("Weapons")
   public static final ForgeConfigHandler.WeaponConfig weapons = new ForgeConfigHandler.WeaponConfig();
   @Comment({"Potion Config (server)"})
   @Name("Potions")
   public static final ForgeConfigHandler.PotionConfig potions = new ForgeConfigHandler.PotionConfig();
   @Comment({"Enchantment Config (server)"})
   @Name("Enchantments")
   public static final ForgeConfigHandler.EnchantmentConfig enchantments = new ForgeConfigHandler.EnchantmentConfig();
   @Comment({"Material Config (server)"})
   @Name("Materials")
   public static final ForgeConfigHandler.MaterialConfig materials = new ForgeConfigHandler.MaterialConfig();
   @Comment({"Client Config"})
   @Name("Client")
   public static final ForgeConfigHandler.ClientConfig client = new ForgeConfigHandler.ClientConfig();

   public ForgeConfigHandler() {
   }

   public static class ClientConfig {
      @Comment({"Prevent custom shields from messing up your FoV. This will cause problems with other mods that change FoV."})
      @Name("Custom Shield FOV")
      public boolean fovShields = true;
      @Comment({"Prevent FoV changes completely."})
      @Name("Static FOV")
      public boolean staticFOV = false;

      public ClientConfig() {
      }
   }

   public static class EnchantmentConfig {
      @Name("Assassinate max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int assassinteLevel = 3;
      @Name("Agility max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int agilityLevel = 2;
      @Name("Arrow Recovery max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int arrowRecoveryLevel = 3;
      @Name("Bash max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int bashLevel = 3;
      @Name("Blast max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int blastLevel = 2;
      @Name("Blocking Power max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int blockingPowerLevel = 3;
      @Name("Combo max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int comboLevel = 3;
      @Name("Diamonds Everywhere max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int diamondsEverywhereLevel = 3;
      @Name("Disarm max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int disarmLevel = 1;
      @Name("Education max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int educationLevel = 3;
      @Name("Fling max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int flingLevel = 2;
      @Name("Heavy max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int heavyLevel = 1;
      @Name("High Jump max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int highJumpLevel = 2;
      @Name("Multishot max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int multishotLevel = 3;
      @Name("Penetration max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int penetrationLevel = 5;
      @Name("Range max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int rangeLevel = 1;
      @Name("Rapid Fire max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int rapidFireLevel = 5;
      @Name("Reflection max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int reflectionLevel = 3;
      @Name("Smelting max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int smeltingLevel = 1;
      @Name("Spell Shield max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int spellShieldLevel = 3;
      @Name("Tunneling max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int tunnelingLevel = 2;
      @Name("Vampirism max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int vampirismLevel = 1;
      @Name("Versatility max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int versatilityLevel = 1;
      @Name("Vitality max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int vitalityLevel = 1;
      @Name("Weightless max level")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int weightlessLevel = 1;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Assassinate treasure")
      public boolean assassinteTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Agility treasure")
      public boolean agilityTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Arrow Recovery treasure")
      public boolean arrowRecoveryTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Bash treasure")
      public boolean bashTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Blast treasure")
      public boolean blastTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Blocking Power treasure")
      public boolean blockingPowerTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Combo treasure")
      public boolean comboTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Diamonds Everywhere treasure")
      public boolean diamondsEverywhereTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Disarm treasure")
      public boolean disarmTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Education treasure")
      public boolean educationTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Fling treasure")
      public boolean flingTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Heavy treasure")
      public boolean heavyTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("High Jump treasure")
      public boolean highJumpTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Multishot treasure")
      public boolean multishotTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Penetration treasure")
      public boolean penetrationTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Range treasure")
      public boolean rangeTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Rapid Fire treasure")
      public boolean rapidFireTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Reflection treasure")
      public boolean reflectionTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Smelting treasure")
      public boolean smeltingTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Spell Shield treasure")
      public boolean spellShieldTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Tunneling treasure")
      public boolean tunnelingTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Vampirism treasure")
      public boolean vampirismTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Versatility treasure")
      public boolean versatilityTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Vitality treasure")
      public boolean vitalityTreasure = false;
      @Comment({"Whether or not is this enchantment a treasure enchantment (exclusive to loot and trading)."})
      @Name("Weightless treasure")
      public boolean weightlessTreasure = false;
      @Comment({"Multiplier of arrow velocity for the Range enchantment."})
      @Name("Range Multiplier")
      @RangeDouble(
         min = 1.0D,
         max = 10.0D
      )
      public double rangeVelocity = 1.5D;
      @Comment({"Prevent Tunneling enchantment from attempting to mine TileEntities."})
      @Name("Tunneling ignores TileEntities")
      public boolean preventTunnelingTileEntities = true;

      public EnchantmentConfig() {
      }
   }

   @EventBusSubscriber(
      modid = "mujmajnkraftsbettersurvival"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("mujmajnkraftsbettersurvival")) {
            ForgeConfigHandler.potions.refreshCache();
            ConfigManager.sync("mujmajnkraftsbettersurvival", Type.INSTANCE);
         }

      }
   }

   public static class MaterialConfig {
      @Comment({"Allows you to craft weapons from materials from other mods."})
      @Name("Modded materials")
      @RequiresMcRestart
      public boolean moddedMaterials = true;
      @Comment({"Set stats of copper material (harvest level, durability, efficiency, damage, enchantability)"})
      @Name("Copper stats")
      @RequiresMcRestart
      public Double[] copperStats = new Double[]{1.0D, 160.0D, 5.0D, 1.0D, 5.0D};
      @Comment({"Set stats of copper material (harvest level, durability, efficiency, damage, enchantability)"})
      @Name("Brone stats")
      @RequiresMcRestart
      public Double[] bronzeStats = new Double[]{2.0D, 200.0D, 6.0D, 1.8D, 14.0D};
      @Comment({"Set stats of invar material (harvest level, durability, efficiency, damage, enchantability)"})
      @Name("Invar stats")
      @RequiresMcRestart
      public Double[] invarStats = new Double[]{2.0D, 250.0D, 6.5D, 2.1D, 10.0D};
      @Comment({"Set stats of silver material (harvest level, durability, efficiency, damage, enchantability)"})
      @Name("Silver stats")
      @RequiresMcRestart
      public Double[] silverStats = new Double[]{0.0D, 32.0D, 12.0D, 0.5D, 22.0D};
      @Comment({"Set stats of electrum material (harvest level, durability, efficiency, damage, enchantability)"})
      @Name("Electrum stats")
      @RequiresMcRestart
      public Double[] electrumStats = new Double[]{0.0D, 32.0D, 15.0D, 0.6D, 35.0D};
      @Comment({"Set stats of aluminium material (harvest level, durability, efficiency, damage, enchantability)"})
      @Name("Aluminium stats")
      @RequiresMcRestart
      public Double[] aluminiumStats = new Double[]{2.0D, 220.0D, 12.0D, 1.8D, 14.0D};
      @Comment({"Set stats of steel material (harvest level, durability, efficiency, damage, enchantability)"})
      @Name("Steel stats")
      @RequiresMcRestart
      public Double[] steelStats = new Double[]{2.0D, 350.0D, 6.5D, 2.5D, 14.0D};
      @Comment({"Set stats of signalum material (harvest level, durability, efficiency, damage, enchantability)"})
      @Name("Signalum stats")
      @RequiresMcRestart
      public Double[] signalumStats = new Double[]{2.0D, 500.0D, 10.0D, 2.0D, 14.0D};
      @Comment({"Set stats of limium material (harvest level, durability, efficiency, damage, enchantability)"})
      @Name("Lumium stats")
      @RequiresMcRestart
      public Double[] lumiumStats = new Double[]{2.0D, 600.0D, 12.0D, 2.5D, 14.0D};
      @Comment({"Set stats of enderium material (harvest level, durability, efficiency, damage, enchantability)"})
      @Name("Enderium stats")
      @RequiresMcRestart
      public Double[] enderiumStats = new Double[]{3.0D, 1000.0D, 10.0D, 4.0D, 20.0D};

      public MaterialConfig() {
      }
   }

   public static class PotionConfig {
      @Comment({"How much should Blindness reduce mobs view distance (Percentage)"})
      @Name("Blindness Strength")
      @RangeDouble(
         min = 0.0D,
         max = 100.0D
      )
      public double blindnessStrength = 80.0D;
      @Comment({"Blacklist of mobs to not affect with Blindness."})
      @Name("Blindness Blacklist")
      public String[] blindnessBlacklist = new String[]{""};
      @Comment({"When applying a potion to a weapon, how many hits should a dose give on the weapon."})
      @Name("Potion Hits Amount")
      @RangeInt(
         min = 1,
         max = 256
      )
      public int potionHits = 64;
      @Comment({"Maximum number of hits to apply to a weapon with repeated doses."})
      @Name("Maximum Potion Hits")
      @RangeInt(
         min = 1,
         max = 512
      )
      public int maximumPotionHits = 64;
      @Comment({"When applying a potion to a weapon, what to divide the potion's time by for a single dose."})
      @Name("Potion Timer Divider")
      @RangeInt(
         min = 1,
         max = 256
      )
      public int potionDivisor = 8;
      @Comment({"Whitelist of weapons classes that can have potions applied to them."})
      @Name("Potion-Applying Weapon Whitelist")
      public String[] paWeaponWhitelist = new String[]{"net.minecraft.item.ItemSword", "com.mujmajnkraft.bettersurvival.items.ItemCustomWeapon"};
      @Comment({"Blacklist of potions that can be applied to weapons."})
      @Name("Potion-Applying Potion Blacklist")
      public String[] paPotionBlacklist = new String[]{""};
      private List<Class<?>> weaponWhitelist = null;

      public PotionConfig() {
      }

      public boolean isClassInstanceofWhitelistedWeapon(Class<?> clazz) {
         return ForgeConfigHandler.potions.getWeaponWhitelistClasses().stream().anyMatch((w) -> {
            return w.isAssignableFrom(clazz);
         });
      }

      private List<Class<?>> getWeaponWhitelistClasses() {
         if (this.weaponWhitelist == null) {
            this.weaponWhitelist = new ArrayList();

            for(String name : this.paWeaponWhitelist) {
               try {
                  this.weaponWhitelist.add(Class.forName(name));
               } catch (Exception var6) {
                  BetterSurvival.LOG.log(Level.WARN, "Failed to parse weapon whitelist entry: " + name);
               }
            }
         }

         return this.weaponWhitelist;
      }

      public void refreshCache() {
         this.weaponWhitelist = null;
      }
   }

   public static class WeaponConfig {
      final String damageModifierDesc = "Set damage stat modifere for the weapon";
      final String speedModifierDesc = "Set speed stat modifere for the weapon";
      @Comment({"Extra attack reach given to spears."})
      @Name("Extra reach base")
      @RequiresMcRestart
      public float spearReachBonus = 2.0F;
      @Comment({"Chance per full-strength hammer hit that Stun procs."})
      @Name("Stun Base Chance")
      public float stunBaseChance = 0.1F;
      @Comment({"Increased Stun chance per level of Bash"})
      @Name("Bash Stun Modifier")
      public float bashModifier = 0.05F;
      @Comment({"Chance per full-strength battleaxe hit that Disarm procs."})
      @Name("Disarm Base Chance")
      public float disarmBaseChance = 0.1F;
      @Comment({"Increased Disarm chance per level of Disarming"})
      @Name("Disarming chance Modifier")
      public float disarmModifier = 0.05F;
      @Comment({"Set damage stat modifere for the weapon"})
      @Name("Battle Axe damage modifier")
      @RequiresMcRestart
      public float battleAxeDmgMod = 1.6F;
      @Comment({"Set damage stat modifere for the weapon"})
      @Name("Nunchaku damage modifier")
      @RequiresMcRestart
      public float nunchakuDmgMod = 0.5F;
      @Comment({"Set damage stat modifere for the weapon"})
      @Name("Hammer damage modifier")
      @RequiresMcRestart
      public float hammerDmgMod = 1.2F;
      @Comment({"Set damage stat modifere for the weapon"})
      @Name("Dagger damage modifier")
      @RequiresMcRestart
      public float daggerDmgMod = 0.7F;
      @Comment({"Set damage stat modifere for the weapon"})
      @Name("Spear damage")
      @RequiresMcRestart
      public float spearDmgMod = 0.75F;
      @Comment({"Set damage stat modifere for the weapon"})
      @Name("Crossbow damage")
      public float crossbowDmgMod = 2.0F;
      @Comment({"Set speed stat modifere for the weapon"})
      @Name("Battle Axe attack delay")
      @RequiresMcRestart
      public float battleAxeSpd = 1.25F;
      @Comment({"Set speed stat modifere for the weapon"})
      @Name("Nunchaku attack delay")
      @RequiresMcRestart
      public float nunchakuSpd = 0.3F;
      @Comment({"Set speed stat modifere for the weapon"})
      @Name("Hammer attack delay")
      @RequiresMcRestart
      public float hammerSpd = 1.35F;
      @Comment({"Set speed stat modifere for the weapon"})
      @Name("Dagger attack delay")
      @RequiresMcRestart
      public float daggerSpd = 0.8F;
      @Comment({"Set speed stat modifere for the weapon"})
      @Name("Spear attack delay")
      @RequiresMcRestart
      public float spearSpd = 1.0F;
      @Comment({"Set speed stat modifere for the weapon"})
      @Name("Crossbow realoding time")
      public int crossbowSpd = 40;
      @Comment({"When set to false, disable Vanilla shield recipe, forcing player to use this mod's shields."})
      @Name("Allow Vanilla Shields")
      @RequiresMcRestart
      public boolean allowVanillaShields = true;

      public WeaponConfig() {
      }
   }
}
