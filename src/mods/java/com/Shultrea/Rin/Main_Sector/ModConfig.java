package com.Shultrea.Rin.Main_Sector;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
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

   public static class EventHandler {
      private EventHandler() {
      }

      public static void register(EventBus bus) {
         bus.register(OnConfigChangedEvent.class, EventHandler::onConfigChanged);
      }

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

   public static void register() {
      ConfigManager.register("somanyenchantments", "", "general", null,
         ConfigEntry.category("Enabled Enchantments",
            ConfigEntry.value("Adept", boolean.class, () -> ModConfig.enabled.Adept, v -> ModConfig.enabled.Adept = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Advanced Bane of Arthropods", boolean.class, () -> ModConfig.enabled.AdvancedBaneOfArthropodsEnable, v -> ModConfig.enabled.AdvancedBaneOfArthropodsEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedbaneofarthropodsenable").requiresMcRestart(),
            ConfigEntry.value("Advanced Blast Protection", boolean.class, () -> ModConfig.enabled.AdvancedBlastProtection, v -> ModConfig.enabled.AdvancedBlastProtection = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedblastprotection").requiresMcRestart(),
            ConfigEntry.value("Advanced Efficiency", boolean.class, () -> ModConfig.enabled.AdvancedEfficencyEnable, v -> ModConfig.enabled.AdvancedEfficencyEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedefficencyenable").requiresMcRestart(),
            ConfigEntry.value("Advanced Feather Falling", boolean.class, () -> ModConfig.enabled.AdvancedFeatherFalling, v -> ModConfig.enabled.AdvancedFeatherFalling = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedfeatherfalling").requiresMcRestart(),
            ConfigEntry.value("Advanced Fire Aspect", boolean.class, () -> ModConfig.enabled.AdvancedFireAspect, v -> ModConfig.enabled.AdvancedFireAspect = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedfireaspect").requiresMcRestart(),
            ConfigEntry.value("Advanced Fire Protection", boolean.class, () -> ModConfig.enabled.AdvancedFireProtection, v -> ModConfig.enabled.AdvancedFireProtection = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedfireprotection").requiresMcRestart(),
            ConfigEntry.value("Advanced Flame", boolean.class, () -> ModConfig.enabled.AdvancedFlame, v -> ModConfig.enabled.AdvancedFlame = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedflame").requiresMcRestart(),
            ConfigEntry.value("Advanced Knockback", boolean.class, () -> ModConfig.enabled.AdvancedKnockbackEnable, v -> ModConfig.enabled.AdvancedKnockbackEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedknockbackenable").requiresMcRestart(),
            ConfigEntry.value("Advanced Looting", boolean.class, () -> ModConfig.enabled.AdvancedLootingEnable, v -> ModConfig.enabled.AdvancedLootingEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedlootingenable").requiresMcRestart(),
            ConfigEntry.value("Advanced Luck of the Sea", boolean.class, () -> ModConfig.enabled.AdvancedLuckOfTheSea, v -> ModConfig.enabled.AdvancedLuckOfTheSea = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedluckofthesea").requiresMcRestart(),
            ConfigEntry.value("Advanced Lure", boolean.class, () -> ModConfig.enabled.AdvancedLure, v -> ModConfig.enabled.AdvancedLure = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedlure").requiresMcRestart(),
            ConfigEntry.value("Advanced Mending", boolean.class, () -> ModConfig.enabled.AdvancedMending, v -> ModConfig.enabled.AdvancedMending = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedmending").requiresMcRestart(),
            ConfigEntry.value("Advanced Power", boolean.class, () -> ModConfig.enabled.AdvancedPowerEnable, v -> ModConfig.enabled.AdvancedPowerEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedpowerenable").requiresMcRestart(),
            ConfigEntry.value("Advanced Projectile Protection", boolean.class, () -> ModConfig.enabled.AdvancedProjectileProtection, v -> ModConfig.enabled.AdvancedProjectileProtection = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedprojectileprotection").requiresMcRestart(),
            ConfigEntry.value("Advanced Protection", boolean.class, () -> ModConfig.enabled.AdvancedProtection, v -> ModConfig.enabled.AdvancedProtection = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedprotection").requiresMcRestart(),
            ConfigEntry.value("Advanced Punch", boolean.class, () -> ModConfig.enabled.AdvancedPunch, v -> ModConfig.enabled.AdvancedPunch = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedpunch").requiresMcRestart(),
            ConfigEntry.value("Advanced Sharpness", boolean.class, () -> ModConfig.enabled.AdvancedSharpnessEnable, v -> ModConfig.enabled.AdvancedSharpnessEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedsharpnessenable").requiresMcRestart(),
            ConfigEntry.value("Advanced Smite", boolean.class, () -> ModConfig.enabled.AdvancedSmiteEnable, v -> ModConfig.enabled.AdvancedSmiteEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedsmiteenable").requiresMcRestart(),
            ConfigEntry.value("Advanced Thorns", boolean.class, () -> ModConfig.enabled.AdvancedThorns, v -> ModConfig.enabled.AdvancedThorns = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.advancedthorns").requiresMcRestart(),
            ConfigEntry.value("Ancient Sealed Curses", boolean.class, () -> ModConfig.enabled.AncientCurseInflicter, v -> ModConfig.enabled.AncientCurseInflicter = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.ancientcurseinflicter").requiresMcRestart(),
            ConfigEntry.value("Ancient Sword Mastery", boolean.class, () -> ModConfig.enabled.AncientSwordMastery, v -> ModConfig.enabled.AncientSwordMastery = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.ancientswordmastery").requiresMcRestart(),
            ConfigEntry.value("Arc Slash", boolean.class, () -> ModConfig.enabled.Swiper, v -> ModConfig.enabled.Swiper = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.swiper").requiresMcRestart(),
            ConfigEntry.value("Ash Destroyer", boolean.class, () -> ModConfig.enabled.AshDestroyerEnable, v -> ModConfig.enabled.AshDestroyerEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.ashdestroyerenable").requiresMcRestart(),
            ConfigEntry.value("Atomic Deconstructor", boolean.class, () -> ModConfig.enabled.AtomicDeconstructor, v -> ModConfig.enabled.AtomicDeconstructor = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.atomicdeconstructor").requiresMcRestart(),
            ConfigEntry.value("Blessed Edge", boolean.class, () -> ModConfig.enabled.BlessedEdgeEnable, v -> ModConfig.enabled.BlessedEdgeEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.blessededgeenable").requiresMcRestart(),
            ConfigEntry.value("Bluntness", boolean.class, () -> ModConfig.enabled.BluntnessEnable, v -> ModConfig.enabled.BluntnessEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.bluntnessenable").requiresMcRestart(),
            ConfigEntry.value("Brutality", boolean.class, () -> ModConfig.enabled.Brutality, v -> ModConfig.enabled.Brutality = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Burning Shield", boolean.class, () -> ModConfig.enabled.FieryShield, v -> ModConfig.enabled.FieryShield = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.fieryshield").requiresMcRestart(),
            ConfigEntry.value("Burning Thorns", boolean.class, () -> ModConfig.enabled.BurningThorns, v -> ModConfig.enabled.BurningThorns = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.burningthorns").requiresMcRestart(),
            ConfigEntry.value("Butchering", boolean.class, () -> ModConfig.enabled.ButcheringEnable, v -> ModConfig.enabled.ButcheringEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.butcheringenable").requiresMcRestart(),
            ConfigEntry.value("Clearskies' Favor", boolean.class, () -> ModConfig.enabled.ClearskyEnable, v -> ModConfig.enabled.ClearskyEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.clearskyenable").requiresMcRestart(),
            ConfigEntry.value("Combat Veterancy", boolean.class, () -> ModConfig.enabled.CombatRegenerationEnable, v -> ModConfig.enabled.CombatRegenerationEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.combatregenerationenable").requiresMcRestart(),
            ConfigEntry.value("Counter Attack", boolean.class, () -> ModConfig.enabled.CounterAttackEnable, v -> ModConfig.enabled.CounterAttackEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.counterattackenable").requiresMcRestart(),
            ConfigEntry.value("Critical Strike", boolean.class, () -> ModConfig.enabled.CriticalStrikeEnable, v -> ModConfig.enabled.CriticalStrikeEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.criticalstrikeenable").requiresMcRestart(),
            ConfigEntry.value("Culling", boolean.class, () -> ModConfig.enabled.CullingEnable, v -> ModConfig.enabled.CullingEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.cullingenable").requiresMcRestart(),
            ConfigEntry.value("Curse of Decay", boolean.class, () -> ModConfig.enabled.CurseofDecay, v -> ModConfig.enabled.CurseofDecay = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.curseofdecay").requiresMcRestart(),
            ConfigEntry.value("Curse of Holding", boolean.class, () -> ModConfig.enabled.CurseofHolding, v -> ModConfig.enabled.CurseofHolding = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.curseofholding").requiresMcRestart(),
            ConfigEntry.value("Curse of Inaccuracy", boolean.class, () -> ModConfig.enabled.CurseOfInaccuracy, v -> ModConfig.enabled.CurseOfInaccuracy = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.curseofinaccuracy").requiresMcRestart(),
            ConfigEntry.value("Curse of Possession", boolean.class, () -> ModConfig.enabled.CurseofPossession, v -> ModConfig.enabled.CurseofPossession = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.curseofpossession").requiresMcRestart(),
            ConfigEntry.value("Curse of Vulnerability", boolean.class, () -> ModConfig.enabled.CurseofVulnerability, v -> ModConfig.enabled.CurseofVulnerability = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.curseofvulnerability").requiresMcRestart(),
            ConfigEntry.value("Cursed Edge", boolean.class, () -> ModConfig.enabled.CursedEdgeEnable, v -> ModConfig.enabled.CursedEdgeEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.cursededgeenable").requiresMcRestart(),
            ConfigEntry.value("Dark Shadows", boolean.class, () -> ModConfig.enabled.DarkShadows, v -> ModConfig.enabled.DarkShadows = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.darkshadows").requiresMcRestart(),
            ConfigEntry.value("Defusing Edge", boolean.class, () -> ModConfig.enabled.DefusionEnable, v -> ModConfig.enabled.DefusionEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.defusionenable").requiresMcRestart(),
            ConfigEntry.value("Desolator", boolean.class, () -> ModConfig.enabled.DesolatorEnable, v -> ModConfig.enabled.DesolatorEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.desolatorenable").requiresMcRestart(),
            ConfigEntry.value("Difficulty's Endowment", boolean.class, () -> ModConfig.enabled.DifficultyScaled, v -> ModConfig.enabled.DifficultyScaled = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.difficultyscaled").requiresMcRestart(),
            ConfigEntry.value("Disarmament", boolean.class, () -> ModConfig.enabled.Disarmament, v -> ModConfig.enabled.Disarmament = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Disorientating Blade", boolean.class, () -> ModConfig.enabled.DisorientationEnable, v -> ModConfig.enabled.DisorientationEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.disorientationenable").requiresMcRestart(),
            ConfigEntry.value("Dragging", boolean.class, () -> ModConfig.enabled.Pulling, v -> ModConfig.enabled.Pulling = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.pulling").requiresMcRestart(),
            ConfigEntry.value("Empowered Defence", boolean.class, () -> ModConfig.enabled.EmpoweredDefenceEnable, v -> ModConfig.enabled.EmpoweredDefenceEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.empowereddefenceenable").requiresMcRestart(),
            ConfigEntry.value("Envenomed", boolean.class, () -> ModConfig.enabled.EnvenomedEnable, v -> ModConfig.enabled.EnvenomedEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.envenomedenable").requiresMcRestart(),
            ConfigEntry.value("Evasion", boolean.class, () -> ModConfig.enabled.Evasion, v -> ModConfig.enabled.Evasion = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Fiery Edge", boolean.class, () -> ModConfig.enabled.FieryEdgeEnable, v -> ModConfig.enabled.FieryEdgeEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.fieryedgeenable").requiresMcRestart(),
            ConfigEntry.value("Flinging", boolean.class, () -> ModConfig.enabled.Flinging, v -> ModConfig.enabled.Flinging = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Freezing", boolean.class, () -> ModConfig.enabled.Freezing, v -> ModConfig.enabled.Freezing = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Heavy Weight", boolean.class, () -> ModConfig.enabled.HeavyWeight, v -> ModConfig.enabled.HeavyWeight = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.heavyweight").requiresMcRestart(),
            ConfigEntry.value("Hors De Combat", boolean.class, () -> ModConfig.enabled.Hors_de_combat, v -> ModConfig.enabled.Hors_de_combat = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.hors_de_combat").requiresMcRestart(),
            ConfigEntry.value("Inefficient", boolean.class, () -> ModConfig.enabled.InefficentEnable, v -> ModConfig.enabled.InefficentEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.inefficentenable").requiresMcRestart(),
            ConfigEntry.value("Inhumane", boolean.class, () -> ModConfig.enabled.Inhumane, v -> ModConfig.enabled.Inhumane = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Inner Berserk", boolean.class, () -> ModConfig.enabled.InnerBerserk, v -> ModConfig.enabled.InnerBerserk = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.innerberserk").requiresMcRestart(),
            ConfigEntry.value("Instability", boolean.class, () -> ModConfig.enabled.Instability, v -> ModConfig.enabled.Instability = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Jagged Rake", boolean.class, () -> ModConfig.enabled.ScytheDamage, v -> ModConfig.enabled.ScytheDamage = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.scythedamage").requiresMcRestart(),
            ConfigEntry.value("Lesser Bane of Arthropods", boolean.class, () -> ModConfig.enabled.LesserBaneOfArthropods, v -> ModConfig.enabled.LesserBaneOfArthropods = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.lesserbaneofarthropods").requiresMcRestart(),
            ConfigEntry.value("Lesser Fire Aspect", boolean.class, () -> ModConfig.enabled.LesserFireAspect, v -> ModConfig.enabled.LesserFireAspect = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.lesserfireaspect").requiresMcRestart(),
            ConfigEntry.value("Lesser Flame", boolean.class, () -> ModConfig.enabled.LesserFlame, v -> ModConfig.enabled.LesserFlame = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.lesserflame").requiresMcRestart(),
            ConfigEntry.value("Lesser Sharpness", boolean.class, () -> ModConfig.enabled.LesserSharpness, v -> ModConfig.enabled.LesserSharpness = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.lessersharpness").requiresMcRestart(),
            ConfigEntry.value("Lesser Smite", boolean.class, () -> ModConfig.enabled.LesserSmite, v -> ModConfig.enabled.LesserSmite = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.lessersmite").requiresMcRestart(),
            ConfigEntry.value("Levitator", boolean.class, () -> ModConfig.enabled.LevitatorEnable, v -> ModConfig.enabled.LevitatorEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.levitatorenable").requiresMcRestart(),
            ConfigEntry.value("Lifesteal", boolean.class, () -> ModConfig.enabled.LifestealEnable, v -> ModConfig.enabled.LifestealEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.lifestealenable").requiresMcRestart(),
            ConfigEntry.value("Light Weight", boolean.class, () -> ModConfig.enabled.LightWeight, v -> ModConfig.enabled.LightWeight = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.lightweight").requiresMcRestart(),
            ConfigEntry.value("Luck Magnification", boolean.class, () -> ModConfig.enabled.LuckMagnification, v -> ModConfig.enabled.LuckMagnification = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.luckmagnification").requiresMcRestart(),
            ConfigEntry.value("Lunar's Blessing", boolean.class, () -> ModConfig.enabled.MoonlightEnable, v -> ModConfig.enabled.MoonlightEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.moonlightenable").requiresMcRestart(),
            ConfigEntry.value("Magic Protection", boolean.class, () -> ModConfig.enabled.MagicProtectionEnable, v -> ModConfig.enabled.MagicProtectionEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.magicprotectionenable").requiresMcRestart(),
            ConfigEntry.value("Magma Walker", boolean.class, () -> ModConfig.enabled.MagmaWalker, v -> ModConfig.enabled.MagmaWalker = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.magmawalker").requiresMcRestart(),
            ConfigEntry.value("Meltdown", boolean.class, () -> ModConfig.enabled.Meltdown, v -> ModConfig.enabled.Meltdown = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Moisturized", boolean.class, () -> ModConfig.enabled.WellTilled, v -> ModConfig.enabled.WellTilled = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.welltilled").requiresMcRestart(),
            ConfigEntry.value("Mortalitas", boolean.class, () -> ModConfig.enabled.Mortalitas, v -> ModConfig.enabled.Mortalitas = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Natural Blocking", boolean.class, () -> ModConfig.enabled.NaturalBlocking, v -> ModConfig.enabled.NaturalBlocking = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.naturalblocking").requiresMcRestart(),
            ConfigEntry.value("Pandora's Curse", boolean.class, () -> ModConfig.enabled.Pandora, v -> ModConfig.enabled.Pandora = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.pandora").requiresMcRestart(),
            ConfigEntry.value("Parry", boolean.class, () -> ModConfig.enabled.ParryEnable, v -> ModConfig.enabled.ParryEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.parryenable").requiresMcRestart(),
            ConfigEntry.value("Penetrating Edge", boolean.class, () -> ModConfig.enabled.PenetratingEdgeEnable, v -> ModConfig.enabled.PenetratingEdgeEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.penetratingedgeenable").requiresMcRestart(),
            ConfigEntry.value("Physical Protection", boolean.class, () -> ModConfig.enabled.PhysicalProtectionEnable, v -> ModConfig.enabled.PhysicalProtectionEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.physicalprotectionenable").requiresMcRestart(),
            ConfigEntry.value("Plowing", boolean.class, () -> ModConfig.enabled.TillingPower, v -> ModConfig.enabled.TillingPower = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.tillingpower").requiresMcRestart(),
            ConfigEntry.value("Powerless", boolean.class, () -> ModConfig.enabled.PowerlessEnable, v -> ModConfig.enabled.PowerlessEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.powerlessenable").requiresMcRestart(),
            ConfigEntry.value("Purging Blade", boolean.class, () -> ModConfig.enabled.PurgingBladeEnable, v -> ModConfig.enabled.PurgingBladeEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.purgingbladeenable").requiresMcRestart(),
            ConfigEntry.value("Purification", boolean.class, () -> ModConfig.enabled.PurificationEnable, v -> ModConfig.enabled.PurificationEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.purificationenable").requiresMcRestart(),
            ConfigEntry.value("Pushing", boolean.class, () -> ModConfig.enabled.Pushing, v -> ModConfig.enabled.Pushing = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Quarrying", boolean.class, () -> ModConfig.enabled.Quarrying, v -> ModConfig.enabled.Quarrying = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Rain's Bestowment", boolean.class, () -> ModConfig.enabled.RainingEnable, v -> ModConfig.enabled.RainingEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.rainingenable").requiresMcRestart(),
            ConfigEntry.value("Reinforced Sharpness", boolean.class, () -> ModConfig.enabled.SharperEdge, v -> ModConfig.enabled.SharperEdge = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.sharperedge").requiresMcRestart(),
            ConfigEntry.value("Reviled Blade", boolean.class, () -> ModConfig.enabled.ReviledBladeEnable, v -> ModConfig.enabled.ReviledBladeEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.reviledbladeenable").requiresMcRestart(),
            ConfigEntry.value("Rune: Arrow Piercing", boolean.class, () -> ModConfig.enabled.Rune_PiercingArrows, v -> ModConfig.enabled.Rune_PiercingArrows = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.rune_piercingarrows").requiresMcRestart(),
            ConfigEntry.value("Rune: Magical Blessing", boolean.class, () -> ModConfig.enabled.Rune_MagicalBlessingEnable, v -> ModConfig.enabled.Rune_MagicalBlessingEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.rune_magicalblessingenable").requiresMcRestart(),
            ConfigEntry.value("Rune: Piercing Capabilities", boolean.class, () -> ModConfig.enabled.Rune_PiercingCapabilitiesEnable, v -> ModConfig.enabled.Rune_PiercingCapabilitiesEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.rune_piercingcapabilitiesenable").requiresMcRestart(),
            ConfigEntry.value("Rune: Resurrection", boolean.class, () -> ModConfig.enabled.Rune_Resurrection, v -> ModConfig.enabled.Rune_Resurrection = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.rune_resurrection").requiresMcRestart(),
            ConfigEntry.value("Rune: Revival", boolean.class, () -> ModConfig.enabled.RevivalEnable, v -> ModConfig.enabled.RevivalEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.revivalenable").requiresMcRestart(),
            ConfigEntry.value("Rusted", boolean.class, () -> ModConfig.enabled.RustedEnable, v -> ModConfig.enabled.RustedEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.rustedenable").requiresMcRestart(),
            ConfigEntry.value("Smelter", boolean.class, () -> ModConfig.enabled.SmelterEnable, v -> ModConfig.enabled.SmelterEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.smelterenable").requiresMcRestart(),
            ConfigEntry.value("Sol's Blessing", boolean.class, () -> ModConfig.enabled.SunshineEnable, v -> ModConfig.enabled.SunshineEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.sunshineenable").requiresMcRestart(),
            ConfigEntry.value("Spell Breaker", boolean.class, () -> ModConfig.enabled.SpellBreakerEnable, v -> ModConfig.enabled.SpellBreakerEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.spellbreakerenable").requiresMcRestart(),
            ConfigEntry.value("Splitshot", boolean.class, () -> ModConfig.enabled.SplitShot, v -> ModConfig.enabled.SplitShot = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.splitshot").requiresMcRestart(),
            ConfigEntry.value("Strafe", boolean.class, () -> ModConfig.enabled.StrafeEnable, v -> ModConfig.enabled.StrafeEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.strafeenable").requiresMcRestart(),
            ConfigEntry.value("Strengthened Vitality", boolean.class, () -> ModConfig.enabled.StrengthenedVitality, v -> ModConfig.enabled.StrengthenedVitality = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.strengthenedvitality").requiresMcRestart(),
            ConfigEntry.value("Subject English", boolean.class, () -> ModConfig.enabled.English, v -> ModConfig.enabled.English = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.english").requiresMcRestart(),
            ConfigEntry.value("Subject History", boolean.class, () -> ModConfig.enabled.History, v -> ModConfig.enabled.History = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.history").requiresMcRestart(),
            ConfigEntry.value("Subject Mathematics", boolean.class, () -> ModConfig.enabled.Mathematics, v -> ModConfig.enabled.Mathematics = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.mathematics").requiresMcRestart(),
            ConfigEntry.value("Subject P.E.", boolean.class, () -> ModConfig.enabled.PE, v -> ModConfig.enabled.PE = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.pe").requiresMcRestart(),
            ConfigEntry.value("Subject Science", boolean.class, () -> ModConfig.enabled.Science, v -> ModConfig.enabled.Science = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.science").requiresMcRestart(),
            ConfigEntry.value("Supreme Bane of Arthropods", boolean.class, () -> ModConfig.enabled.SupremeBaneOfArthropods, v -> ModConfig.enabled.SupremeBaneOfArthropods = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.supremebaneofarthropods").requiresMcRestart(),
            ConfigEntry.value("Supreme Fire Aspect", boolean.class, () -> ModConfig.enabled.SupremeFireAspect, v -> ModConfig.enabled.SupremeFireAspect = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.supremefireaspect").requiresMcRestart(),
            ConfigEntry.value("Supreme Flame", boolean.class, () -> ModConfig.enabled.SupremeFlame, v -> ModConfig.enabled.SupremeFlame = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.supremeflame").requiresMcRestart(),
            ConfigEntry.value("Supreme Sharpness", boolean.class, () -> ModConfig.enabled.SupremeSharpness, v -> ModConfig.enabled.SupremeSharpness = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.supremesharpness").requiresMcRestart(),
            ConfigEntry.value("Supreme Smite", boolean.class, () -> ModConfig.enabled.SupremeSmite, v -> ModConfig.enabled.SupremeSmite = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.supremesmite").requiresMcRestart(),
            ConfigEntry.value("Swifter Slashes", boolean.class, () -> ModConfig.enabled.SwifterSlashesEnable, v -> ModConfig.enabled.SwifterSlashesEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.swifterslashesenable").requiresMcRestart(),
            ConfigEntry.value("Thunderstorm's Bestowment", boolean.class, () -> ModConfig.enabled.ThunderstormEnable, v -> ModConfig.enabled.ThunderstormEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.thunderstormenable").requiresMcRestart(),
            ConfigEntry.value("True Strike", boolean.class, () -> ModConfig.enabled.TrueStrike, v -> ModConfig.enabled.TrueStrike = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.truestrike").requiresMcRestart(),
            ConfigEntry.value("Underwater Strider", boolean.class, () -> ModConfig.enabled.UnderwaterStrider, v -> ModConfig.enabled.UnderwaterStrider = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.underwaterstrider").requiresMcRestart(),
            ConfigEntry.value("Unpredictable", boolean.class, () -> ModConfig.enabled.UnpredictableEnable, v -> ModConfig.enabled.UnpredictableEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.unpredictableenable").requiresMcRestart(),
            ConfigEntry.value("Unreasonable", boolean.class, () -> ModConfig.enabled.Frenzy, v -> ModConfig.enabled.Frenzy = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.frenzy").requiresMcRestart(),
            ConfigEntry.value("Unsheathing", boolean.class, () -> ModConfig.enabled.Unsheathing, v -> ModConfig.enabled.Unsheathing = (Boolean)v).requiresMcRestart(),
            ConfigEntry.value("Upgraded Potentials", boolean.class, () -> ModConfig.enabled.Upgrade, v -> ModConfig.enabled.Upgrade = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.upgrade").requiresMcRestart(),
            ConfigEntry.value("Viper", boolean.class, () -> ModConfig.enabled.ViperEnable, v -> ModConfig.enabled.ViperEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.viperenable").requiresMcRestart(),
            ConfigEntry.value("Water Aspect", boolean.class, () -> ModConfig.enabled.WaterAspectEnable, v -> ModConfig.enabled.WaterAspectEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.wateraspectenable").requiresMcRestart(),
            ConfigEntry.value("Winter's Grace", boolean.class, () -> ModConfig.enabled.WinterEnable, v -> ModConfig.enabled.WinterEnable = (Boolean)v).langKey("somanyenchantments.general.enabled enchantments.winterenable").requiresMcRestart()).comment("Enabled Enchantments").langKey("somanyenchantments.general.enabled"),
         ConfigEntry.category("Enchantment Levels",
            ConfigEntry.value("Adept", int.class, () -> ModConfig.level.Adept, v -> ModConfig.level.Adept = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Bane of Arthropods", int.class, () -> ModConfig.level.AdvancedBaneOfArthropods, v -> ModConfig.level.AdvancedBaneOfArthropods = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedbaneofarthropods").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Blast Protection", int.class, () -> ModConfig.level.AdvancedBlastProtection, v -> ModConfig.level.AdvancedBlastProtection = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedblastprotection").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Efficiency", int.class, () -> ModConfig.level.AdvancedEfficency, v -> ModConfig.level.AdvancedEfficency = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedefficency").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Feather Falling", int.class, () -> ModConfig.level.AdvancedFeatherFalling, v -> ModConfig.level.AdvancedFeatherFalling = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedfeatherfalling").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Fire Aspect", int.class, () -> ModConfig.level.AdvancedFireAspect, v -> ModConfig.level.AdvancedFireAspect = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedfireaspect").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Fire Protection", int.class, () -> ModConfig.level.AdvancedFireProtection, v -> ModConfig.level.AdvancedFireProtection = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedfireprotection").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Knockback", int.class, () -> ModConfig.level.AdvancedKnockback, v -> ModConfig.level.AdvancedKnockback = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedknockback").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Looting", int.class, () -> ModConfig.level.AdvancedLooting, v -> ModConfig.level.AdvancedLooting = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedlooting").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Luck of the Sea", int.class, () -> ModConfig.level.AdvancedLuckOfTheSea, v -> ModConfig.level.AdvancedLuckOfTheSea = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedluckofthesea").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Lure", int.class, () -> ModConfig.level.AdvancedLure, v -> ModConfig.level.AdvancedLure = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedlure").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Power", int.class, () -> ModConfig.level.AdvancedPower, v -> ModConfig.level.AdvancedPower = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedpower").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Projectile Protection", int.class, () -> ModConfig.level.AdvancedProjectileProtection, v -> ModConfig.level.AdvancedProjectileProtection = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedprojectileprotection").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Protection", int.class, () -> ModConfig.level.AdvancedProtection, v -> ModConfig.level.AdvancedProtection = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedprotection").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Punch", int.class, () -> ModConfig.level.AdvancedPunch, v -> ModConfig.level.AdvancedPunch = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedpunch").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Sharpness", int.class, () -> ModConfig.level.AdvancedSharpness, v -> ModConfig.level.AdvancedSharpness = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedsharpness").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Smite", int.class, () -> ModConfig.level.AdvancedSmite, v -> ModConfig.level.AdvancedSmite = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedsmite").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Advanced Thorns", int.class, () -> ModConfig.level.AdvancedThorns, v -> ModConfig.level.AdvancedThorns = (Integer)v).langKey("somanyenchantments.general.enchantment levels.advancedthorns").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Ancient Sword Mastery", int.class, () -> ModConfig.level.AncientSwordMastery, v -> ModConfig.level.AncientSwordMastery = (Integer)v).langKey("somanyenchantments.general.enchantment levels.ancientswordmastery").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Arc Slash", int.class, () -> ModConfig.level.Swiper, v -> ModConfig.level.Swiper = (Integer)v).langKey("somanyenchantments.general.enchantment levels.swiper").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Ash Destroyer", int.class, () -> ModConfig.level.AshDestroyer, v -> ModConfig.level.AshDestroyer = (Integer)v).langKey("somanyenchantments.general.enchantment levels.ashdestroyer").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Atomic Deconstructor", int.class, () -> ModConfig.level.AtomicDeconstructor, v -> ModConfig.level.AtomicDeconstructor = (Integer)v).langKey("somanyenchantments.general.enchantment levels.atomicdeconstructor").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Blessed Edge", int.class, () -> ModConfig.level.BlessedEdge, v -> ModConfig.level.BlessedEdge = (Integer)v).langKey("somanyenchantments.general.enchantment levels.blessededge").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Bluntness", int.class, () -> ModConfig.level.Bluntness, v -> ModConfig.level.Bluntness = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Brutality", int.class, () -> ModConfig.level.Brutality, v -> ModConfig.level.Brutality = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Burning Shield", int.class, () -> ModConfig.level.FieryShield, v -> ModConfig.level.FieryShield = (Integer)v).langKey("somanyenchantments.general.enchantment levels.fieryshield").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Burning Thorns", int.class, () -> ModConfig.level.BurningThorns, v -> ModConfig.level.BurningThorns = (Integer)v).langKey("somanyenchantments.general.enchantment levels.burningthorns").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Butchering", int.class, () -> ModConfig.level.Butchering, v -> ModConfig.level.Butchering = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Clearskies' Favor", int.class, () -> ModConfig.level.Clearsky, v -> ModConfig.level.Clearsky = (Integer)v).langKey("somanyenchantments.general.enchantment levels.clearsky").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Combat Veterancy", int.class, () -> ModConfig.level.CombatRegeneration, v -> ModConfig.level.CombatRegeneration = (Integer)v).langKey("somanyenchantments.general.enchantment levels.combatregeneration").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Counter Attack", int.class, () -> ModConfig.level.CounterAttack, v -> ModConfig.level.CounterAttack = (Integer)v).langKey("somanyenchantments.general.enchantment levels.counterattack").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Critical Strike", int.class, () -> ModConfig.level.CriticalStrike, v -> ModConfig.level.CriticalStrike = (Integer)v).langKey("somanyenchantments.general.enchantment levels.criticalstrike").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Culling", int.class, () -> ModConfig.level.Culling, v -> ModConfig.level.Culling = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Curse of Holding", int.class, () -> ModConfig.level.CurseofHolding, v -> ModConfig.level.CurseofHolding = (Integer)v).langKey("somanyenchantments.general.enchantment levels.curseofholding").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Curse of Inaccuracy", int.class, () -> ModConfig.level.CurseOfInaccuracy, v -> ModConfig.level.CurseOfInaccuracy = (Integer)v).langKey("somanyenchantments.general.enchantment levels.curseofinaccuracy").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Curse of Vulnerability", int.class, () -> ModConfig.level.CurseofVulnerability, v -> ModConfig.level.CurseofVulnerability = (Integer)v).langKey("somanyenchantments.general.enchantment levels.curseofvulnerability").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Cursed Edge", int.class, () -> ModConfig.level.CursedEdge, v -> ModConfig.level.CursedEdge = (Integer)v).langKey("somanyenchantments.general.enchantment levels.cursededge").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Dark Shadows", int.class, () -> ModConfig.level.DarkShadows, v -> ModConfig.level.DarkShadows = (Integer)v).langKey("somanyenchantments.general.enchantment levels.darkshadows").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Defusing Edge", int.class, () -> ModConfig.level.Defusion, v -> ModConfig.level.Defusion = (Integer)v).langKey("somanyenchantments.general.enchantment levels.defusion").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Desolator", int.class, () -> ModConfig.level.Desolator, v -> ModConfig.level.Desolator = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Difficulty's Endowment", int.class, () -> ModConfig.level.DifficultyScaled, v -> ModConfig.level.DifficultyScaled = (Integer)v).langKey("somanyenchantments.general.enchantment levels.difficultyscaled").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Disarmament", int.class, () -> ModConfig.level.Disarmament, v -> ModConfig.level.Disarmament = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Disorientating Blade", int.class, () -> ModConfig.level.Disorientation, v -> ModConfig.level.Disorientation = (Integer)v).langKey("somanyenchantments.general.enchantment levels.disorientation").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Dragging", int.class, () -> ModConfig.level.Pulling, v -> ModConfig.level.Pulling = (Integer)v).langKey("somanyenchantments.general.enchantment levels.pulling").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Empowered Defence", int.class, () -> ModConfig.level.EmpoweredDefence, v -> ModConfig.level.EmpoweredDefence = (Integer)v).langKey("somanyenchantments.general.enchantment levels.empowereddefence").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Envenomed", int.class, () -> ModConfig.level.Envenomed, v -> ModConfig.level.Envenomed = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Evasion", int.class, () -> ModConfig.level.Evasion, v -> ModConfig.level.Evasion = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Fiery Edge", int.class, () -> ModConfig.level.FieryEdge, v -> ModConfig.level.FieryEdge = (Integer)v).langKey("somanyenchantments.general.enchantment levels.fieryedge").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Flinging", int.class, () -> ModConfig.level.Flinging, v -> ModConfig.level.Flinging = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Freezing", int.class, () -> ModConfig.level.Freezing, v -> ModConfig.level.Freezing = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Heavy Weight", int.class, () -> ModConfig.level.HeavyWeight, v -> ModConfig.level.HeavyWeight = (Integer)v).langKey("somanyenchantments.general.enchantment levels.heavyweight").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Hors De Combat", int.class, () -> ModConfig.level.Hors_de_combat, v -> ModConfig.level.Hors_de_combat = (Integer)v).langKey("somanyenchantments.general.enchantment levels.hors_de_combat").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Inefficient", int.class, () -> ModConfig.level.Inefficent, v -> ModConfig.level.Inefficent = (Integer)v).langKey("somanyenchantments.general.enchantment levels.inefficent").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Inhumane", int.class, () -> ModConfig.level.Inhumane, v -> ModConfig.level.Inhumane = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Inner Berserk", int.class, () -> ModConfig.level.InnerBerserk, v -> ModConfig.level.InnerBerserk = (Integer)v).langKey("somanyenchantments.general.enchantment levels.innerberserk").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Instability", int.class, () -> ModConfig.level.Instability, v -> ModConfig.level.Instability = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Jagged Rake", int.class, () -> ModConfig.level.ScytheDamage, v -> ModConfig.level.ScytheDamage = (Integer)v).langKey("somanyenchantments.general.enchantment levels.scythedamage").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Lesser Bane of Arthropods", int.class, () -> ModConfig.level.LesserBaneOfArthropods, v -> ModConfig.level.LesserBaneOfArthropods = (Integer)v).langKey("somanyenchantments.general.enchantment levels.lesserbaneofarthropods").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Lesser Fire Aspect", int.class, () -> ModConfig.level.LesserFireAspect, v -> ModConfig.level.LesserFireAspect = (Integer)v).langKey("somanyenchantments.general.enchantment levels.lesserfireaspect").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Lesser Sharpness", int.class, () -> ModConfig.level.LesserSharpness, v -> ModConfig.level.LesserSharpness = (Integer)v).langKey("somanyenchantments.general.enchantment levels.lessersharpness").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Lesser Smite", int.class, () -> ModConfig.level.LesserSmite, v -> ModConfig.level.LesserSmite = (Integer)v).langKey("somanyenchantments.general.enchantment levels.lessersmite").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Levitator", int.class, () -> ModConfig.level.Levitator, v -> ModConfig.level.Levitator = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Lifesteal", int.class, () -> ModConfig.level.Lifesteal, v -> ModConfig.level.Lifesteal = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Light Weight", int.class, () -> ModConfig.level.LightWeight, v -> ModConfig.level.LightWeight = (Integer)v).langKey("somanyenchantments.general.enchantment levels.lightweight").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Luck Magnification", int.class, () -> ModConfig.level.LuckMagnification, v -> ModConfig.level.LuckMagnification = (Integer)v).langKey("somanyenchantments.general.enchantment levels.luckmagnification").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Lunar's Blessing", int.class, () -> ModConfig.level.Moonlight, v -> ModConfig.level.Moonlight = (Integer)v).langKey("somanyenchantments.general.enchantment levels.moonlight").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Magic Protection", int.class, () -> ModConfig.level.MagicProtection, v -> ModConfig.level.MagicProtection = (Integer)v).langKey("somanyenchantments.general.enchantment levels.magicprotection").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Magma Walker", int.class, () -> ModConfig.level.MagmaWalker, v -> ModConfig.level.MagmaWalker = (Integer)v).langKey("somanyenchantments.general.enchantment levels.magmawalker").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Meltdown", int.class, () -> ModConfig.level.Meltdown, v -> ModConfig.level.Meltdown = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Mortalitas", int.class, () -> ModConfig.level.Mortalitas, v -> ModConfig.level.Mortalitas = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Natural Blocking", int.class, () -> ModConfig.level.NaturalBlocking, v -> ModConfig.level.NaturalBlocking = (Integer)v).langKey("somanyenchantments.general.enchantment levels.naturalblocking").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Parry", int.class, () -> ModConfig.level.Parry, v -> ModConfig.level.Parry = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Penetrating Edge", int.class, () -> ModConfig.level.PenetratingEdge, v -> ModConfig.level.PenetratingEdge = (Integer)v).langKey("somanyenchantments.general.enchantment levels.penetratingedge").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Physical Protection", int.class, () -> ModConfig.level.PhysicalProtection, v -> ModConfig.level.PhysicalProtection = (Integer)v).langKey("somanyenchantments.general.enchantment levels.physicalprotection").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Plowing", int.class, () -> ModConfig.level.TillingPower, v -> ModConfig.level.TillingPower = (Integer)v).langKey("somanyenchantments.general.enchantment levels.tillingpower").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Powerless", int.class, () -> ModConfig.level.Powerless, v -> ModConfig.level.Powerless = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Purging Blade", int.class, () -> ModConfig.level.PurgingBlade, v -> ModConfig.level.PurgingBlade = (Integer)v).langKey("somanyenchantments.general.enchantment levels.purgingblade").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Purification", int.class, () -> ModConfig.level.Purification, v -> ModConfig.level.Purification = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Pushing", int.class, () -> ModConfig.level.Pushing, v -> ModConfig.level.Pushing = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Quarrying", int.class, () -> ModConfig.level.Quarrying, v -> ModConfig.level.Quarrying = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Rain's Bestowment", int.class, () -> ModConfig.level.Raining, v -> ModConfig.level.Raining = (Integer)v).langKey("somanyenchantments.general.enchantment levels.raining").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Reinforced Sharpness", int.class, () -> ModConfig.level.SharperEdge, v -> ModConfig.level.SharperEdge = (Integer)v).langKey("somanyenchantments.general.enchantment levels.sharperedge").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Reviled Blade", int.class, () -> ModConfig.level.ReviledBlade, v -> ModConfig.level.ReviledBlade = (Integer)v).langKey("somanyenchantments.general.enchantment levels.reviledblade").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Rune: Arrow Piercing", int.class, () -> ModConfig.level.Rune_PiercingArrows, v -> ModConfig.level.Rune_PiercingArrows = (Integer)v).langKey("somanyenchantments.general.enchantment levels.rune_piercingarrows").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Rune: Magical Blessing", int.class, () -> ModConfig.level.Rune_MagicalBlessing, v -> ModConfig.level.Rune_MagicalBlessing = (Integer)v).langKey("somanyenchantments.general.enchantment levels.rune_magicalblessing").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Rune: Piercing Capabilities", int.class, () -> ModConfig.level.Rune_PiercingCapabilities, v -> ModConfig.level.Rune_PiercingCapabilities = (Integer)v).langKey("somanyenchantments.general.enchantment levels.rune_piercingcapabilities").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Rune: Resurrection", int.class, () -> ModConfig.level.Rune_Resurrection, v -> ModConfig.level.Rune_Resurrection = (Integer)v).langKey("somanyenchantments.general.enchantment levels.rune_resurrection").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Rune: Revival", int.class, () -> ModConfig.level.Revival, v -> ModConfig.level.Revival = (Integer)v).langKey("somanyenchantments.general.enchantment levels.revival").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Rusted", int.class, () -> ModConfig.level.Rusted, v -> ModConfig.level.Rusted = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Sol's Blessing", int.class, () -> ModConfig.level.Sunshine, v -> ModConfig.level.Sunshine = (Integer)v).langKey("somanyenchantments.general.enchantment levels.sunshine").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Spell Breaker", int.class, () -> ModConfig.level.SpellBreaker, v -> ModConfig.level.SpellBreaker = (Integer)v).langKey("somanyenchantments.general.enchantment levels.spellbreaker").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Splitshot", int.class, () -> ModConfig.level.SplitShot, v -> ModConfig.level.SplitShot = (Integer)v).langKey("somanyenchantments.general.enchantment levels.splitshot").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Strafe", int.class, () -> ModConfig.level.Strafe, v -> ModConfig.level.Strafe = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Strengthened Vitality", int.class, () -> ModConfig.level.StrengthenedVitality, v -> ModConfig.level.StrengthenedVitality = (Integer)v).langKey("somanyenchantments.general.enchantment levels.strengthenedvitality").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Subject English", int.class, () -> ModConfig.level.English, v -> ModConfig.level.English = (Integer)v).langKey("somanyenchantments.general.enchantment levels.english").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Subject History", int.class, () -> ModConfig.level.History, v -> ModConfig.level.History = (Integer)v).langKey("somanyenchantments.general.enchantment levels.history").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Subject Mathematics", int.class, () -> ModConfig.level.Mathematics, v -> ModConfig.level.Mathematics = (Integer)v).langKey("somanyenchantments.general.enchantment levels.mathematics").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Subject P.E.", int.class, () -> ModConfig.level.PE, v -> ModConfig.level.PE = (Integer)v).langKey("somanyenchantments.general.enchantment levels.pe").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Subject Science", int.class, () -> ModConfig.level.Science, v -> ModConfig.level.Science = (Integer)v).langKey("somanyenchantments.general.enchantment levels.science").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Supreme Bane of Arthropods", int.class, () -> ModConfig.level.SupremeBaneOfArthropods, v -> ModConfig.level.SupremeBaneOfArthropods = (Integer)v).langKey("somanyenchantments.general.enchantment levels.supremebaneofarthropods").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Supreme Fire Aspect", int.class, () -> ModConfig.level.SupremeFireAspect, v -> ModConfig.level.SupremeFireAspect = (Integer)v).langKey("somanyenchantments.general.enchantment levels.supremefireaspect").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Supreme Sharpness", int.class, () -> ModConfig.level.SupremeSharpness, v -> ModConfig.level.SupremeSharpness = (Integer)v).langKey("somanyenchantments.general.enchantment levels.supremesharpness").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Supreme Smite", int.class, () -> ModConfig.level.SupremeSmite, v -> ModConfig.level.SupremeSmite = (Integer)v).langKey("somanyenchantments.general.enchantment levels.supremesmite").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Swifter Slashes", int.class, () -> ModConfig.level.SwifterSlashes, v -> ModConfig.level.SwifterSlashes = (Integer)v).langKey("somanyenchantments.general.enchantment levels.swifterslashes").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Thunderstorm's Bestowment", int.class, () -> ModConfig.level.Thunderstorm, v -> ModConfig.level.Thunderstorm = (Integer)v).langKey("somanyenchantments.general.enchantment levels.thunderstorm").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Underwater Strider", int.class, () -> ModConfig.level.UnderwaterStrider, v -> ModConfig.level.UnderwaterStrider = (Integer)v).langKey("somanyenchantments.general.enchantment levels.underwaterstrider").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Unpredictable", int.class, () -> ModConfig.level.Unpredictable, v -> ModConfig.level.Unpredictable = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Unreasonable", int.class, () -> ModConfig.level.Frenzy, v -> ModConfig.level.Frenzy = (Integer)v).langKey("somanyenchantments.general.enchantment levels.frenzy").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Unsheathing", int.class, () -> ModConfig.level.Unsheathing, v -> ModConfig.level.Unsheathing = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Viper", int.class, () -> ModConfig.level.Viper, v -> ModConfig.level.Viper = (Integer)v).range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Water Aspect", int.class, () -> ModConfig.level.WaterAspect, v -> ModConfig.level.WaterAspect = (Integer)v).langKey("somanyenchantments.general.enchantment levels.wateraspect").range(1, 10).requiresMcRestart(),
            ConfigEntry.value("Winter's Grace", int.class, () -> ModConfig.level.Winter, v -> ModConfig.level.Winter = (Integer)v).langKey("somanyenchantments.general.enchantment levels.winter").range(1, 10).requiresMcRestart()).comment("Maximum levels of each enchantment").langKey("somanyenchantments.general.level"),
         ConfigEntry.category("Miscellaneous",
            ConfigEntry.value("Allow Weather Change", boolean.class, () -> ModConfig.miscellaneous.EnableWeatherChange, v -> ModConfig.miscellaneous.EnableWeatherChange = (Boolean)v).comment("Allow enchantments to change the weather").langKey("somanyenchantments.general.miscellaneous.enableweatherchange"),
            ConfigEntry.value("Double XP Orbs", boolean.class, () -> ModConfig.miscellaneous.EnableDoubleXPBug, v -> ModConfig.miscellaneous.EnableDoubleXPBug = (Boolean)v).comment("XP Orbs give double XP (this is a bug present in older versions)").langKey("somanyenchantments.general.miscellaneous.enabledoublexpbug"),
            ConfigEntry.value("Fix Zero Damage Enchantments", boolean.class, () -> ModConfig.miscellaneous.EnableFixEnchantment, v -> ModConfig.miscellaneous.EnableFixEnchantment = (Boolean)v).comment("Fixes enchantment that doesn't deal damage when damage is 0 (E.g having weakness while hitting with a wood sword doesn't let the enchantment deal the damage because negative damage cancels damage calculation.)").langKey("somanyenchantments.general.miscellaneous.enablefixenchantment"),
            ConfigEntry.value("Evasion Dodge Roll", boolean.class, () -> ModConfig.miscellaneous.EvasionKnockback, v -> ModConfig.miscellaneous.EvasionKnockback = (Boolean)v).comment("Evasion Enchantment performs a dodge-like action to the Player. Disable if you don't like falling into cliff that is caused by Evasion Enchantment.").langKey("somanyenchantments.general.miscellaneous.evasionknockback"),
            ConfigEntry.value("Quarrying Ore Only", boolean.class, () -> ModConfig.miscellaneous.QuarryingOreOnly, v -> ModConfig.miscellaneous.QuarryingOreOnly = (Boolean)v).comment("Restrict Quarrying to Ore blocks only.").langKey("somanyenchantments.general.miscellaneous.quarryingoreonly"),
            ConfigEntry.value("Quarrying Tile Entities", boolean.class, () -> ModConfig.miscellaneous.harvestTile, v -> ModConfig.miscellaneous.harvestTile = (Boolean)v).comment("Allow Quarrying to work with blocks that are tile entities. I don't know what blocks are these but it may be useful for other mods...").langKey("somanyenchantments.general.miscellaneous.harvesttile"),
            ConfigEntry.value("Unregister Disabled Enchants", boolean.class, () -> ModConfig.miscellaneous.unregisterDisabled, v -> ModConfig.miscellaneous.unregisterDisabled = (Boolean)v).comment("Unregister disabled enchantments").langKey("somanyenchantments.general.miscellaneous.unregisterdisabled").requiresMcRestart(),
            ConfigEntry.value("Potion Blacklist", String[].class, () -> ModConfig.miscellaneous.potionBlacklist, v -> ModConfig.miscellaneous.potionBlacklist = (String[])v).comment("Specify what potion effects are blacklisted from enchantments that add potion effects (use the full registry name, I.E. minecraft:").langKey("somanyenchantments.general.miscellaneous.potionblacklist").requiresMcRestart(),
            ConfigEntry.value("Potion Blacklist as Whitelist", boolean.class, () -> ModConfig.miscellaneous.potionBlacklistAsWhitelist, v -> ModConfig.miscellaneous.potionBlacklistAsWhitelist = (Boolean)v).comment("Whether the blacklist should be treated as a whitelist").langKey("somanyenchantments.general.miscellaneous.potionblacklistaswhitelist").requiresMcRestart(),
            ConfigEntry.value("Enable Pet Attacks", boolean.class, () -> ModConfig.miscellaneous.enablePetAttacks, v -> ModConfig.miscellaneous.enablePetAttacks = (Boolean)v).comment("Whether enchantments should work with pet attacks").langKey("somanyenchantments.general.miscellaneous.enablepetattacks"),
            ConfigEntry.value("Extra Protection Effects", boolean.class, () -> ModConfig.miscellaneous.extraProtectionEffects, v -> ModConfig.miscellaneous.extraProtectionEffects = (Boolean)v).comment("Enables extra protection effects").langKey("somanyenchantments.general.miscellaneous.extraprotectioneffects")).comment("Miscellaneous").langKey("somanyenchantments.general.miscellaneous"));
   }
}
