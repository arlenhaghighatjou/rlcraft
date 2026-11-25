package xzeroair.trinkets.util.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;
import xzeroair.trinkets.races.dragon.config.DragonConfig;
import xzeroair.trinkets.races.dwarf.config.DwarfConfig;
import xzeroair.trinkets.races.elf.config.ElfConfig;
import xzeroair.trinkets.races.faelis.config.FaelisConfig;
import xzeroair.trinkets.races.fairy.config.FairyConfig;
import xzeroair.trinkets.races.goblin.config.GoblinConfig;
import xzeroair.trinkets.races.titan.config.TitanConfig;
import xzeroair.trinkets.util.config.mana.EntityManaConfig;
import xzeroair.trinkets.util.config.potions.PotionConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigArcingOrb;
import xzeroair.trinkets.util.config.trinkets.ConfigDamageShield;
import xzeroair.trinkets.util.config.trinkets.ConfigDragonsEye;
import xzeroair.trinkets.util.config.trinkets.ConfigEnderCrown;
import xzeroair.trinkets.util.config.trinkets.ConfigFaelisClaw;
import xzeroair.trinkets.util.config.trinkets.ConfigGlowRing;
import xzeroair.trinkets.util.config.trinkets.ConfigGreaterInertia;
import xzeroair.trinkets.util.config.trinkets.ConfigInertiaNull;
import xzeroair.trinkets.util.config.trinkets.ConfigPoisonStone;
import xzeroair.trinkets.util.config.trinkets.ConfigPolarizedStone;
import xzeroair.trinkets.util.config.trinkets.ConfigSeaStone;
import xzeroair.trinkets.util.config.trinkets.ConfigTeddyBear;
import xzeroair.trinkets.util.config.trinkets.ConfigWeightlessStone;
import xzeroair.trinkets.util.config.trinkets.ConfigWitherRing;
import xzeroair.trinkets.util.config.trinkets.shared.TransformationRingConfig;

public class ServerConfig {
   private static final String cfgPrefix = "xat.config";
   private static final String serverCfgPrefix = "xat.config.server";
   @Comment({"Settings regarding the Trinkets Container on the server"})
   @Name("Trinket Container Settings")
   @LangKey("xat.config.server.container.settings")
   public ServerConfig.ContainerSettings GUI = new ServerConfig.ContainerSettings();
   @Name("Magical Foods Settings")
   @Comment({""})
   @LangKey("xat.config.server.food.settings")
   public ServerConfig.Foods Food = new ServerConfig.Foods();
   @Name("Potion Settings")
   @Comment({""})
   @LangKey("xat.config.potion.settings")
   public ServerConfig.Potions Potion = new ServerConfig.Potions();
   @Name("Item Settings")
   @Comment({""})
   @LangKey("xat.config.items")
   public ServerConfig.TrinketItems Items = new ServerConfig.TrinketItems();
   @Name("Magic Settings")
   @Comment({""})
   @LangKey("xat.config.magic")
   public EntityManaConfig mana = new EntityManaConfig();
   @Name("Race Settings")
   @Comment({""})
   @LangKey("xat.config.races")
   public ServerConfig.RaceConfigs races = new ServerConfig.RaceConfigs();
   @Name("Misc Settings")
   @Comment({""})
   @LangKey("xat.config.misc")
   public ServerConfig.MiscConfigs misc = new ServerConfig.MiscConfigs();

   public ServerConfig() {
   }

   public class ContainerSettings {
      @RequiresMcRestart
      @Comment({"How many slots should the Trinkets container have"})
      @Name("01. Container Enabled")
      @LangKey("xat.config.server.container.enabled")
      public boolean guiEnabled = true;
      @RequiresMcRestart
      @RangeInt(
         min = 1,
         max = 32
      )
      @Comment({"How many slots should the Trinkets container have"})
      @Name("02. Container Slots")
      @LangKey("xat.config.server.container.slots")
      public int guiSlotsRows = 8;

      public ContainerSettings() {
      }
   }

   public class Foods {
      @RequiresMcRestart
      @Comment({"Should this mod add Magical Foods?. Set to False to Disable. Default True"})
      @Name("01. Foods Enabled")
      @LangKey("xat.config.server.food.registry.enabled")
      public boolean foods_enabled = true;
      @Comment({"Set to False to Disable. Default True"})
      @Name("02. Foods Effects")
      @LangKey("xat.config.server.food.transformation.effect")
      public boolean food_effects = true;
      @Comment({"Should the player keep the Food Effects on Death. Set to False to Disable. Default True"})
      @Name("03. Keep Effects")
      @LangKey("xat.config.server.food.transformation.effect.keep")
      public boolean keep_effects = true;

      public Foods() {
      }
   }

   public class MiscConfigs {
      @RequiresWorldRestart
      @Comment({"Does Depth Strider Stack with Swim Speed Attributes?"})
      @Name("Depth Strider Stacks")
      @LangKey("xat.config.misc.depth")
      public boolean depthStacks = false;
      @RequiresWorldRestart
      @Comment({"Does Underwater Strider from Rins So many Enchantments Stack with Swim Speed Attributes?"})
      @Name("Underwater Strider Stacks")
      @LangKey("xat.config.misc.sme.underwaterstrider")
      public boolean underwaterStriderStacks = false;
      @RequiresWorldRestart
      @Comment({"If enabled, the player will be unable to move when transforming from one race to another"})
      @Name("Prevent Movement while transforming")
      @LangKey("xat.config.misc.movement")
      public boolean movement = false;
      @Comment({"Vanilla MC doesn't handle interaction with increased reach properly, this fixes it"})
      @Name("Reach Interaction Fix")
      @LangKey("xat.config.misc.interaction.fix")
      public boolean reach = true;
      @Comment({"The VIP list may be unaccessible under some circumstance, use this to shorten game launching time."})
      @Name("VIP")
      @LangKey("xat.config.misc.vip")
      public boolean retrieveVIP = true;

      public MiscConfigs() {
      }
   }

   public class Potions {
      private final String potion = "xat.config.server.potion";
      @RequiresMcRestart
      @Comment({"Should T&B Register any potions"})
      @Name("00. Potions Enabled")
      @LangKey("xat.config.server.potion.registry.enabled")
      public boolean potions_enabled = true;
      @Comment({"If Tough as Nails is Installed, Should the Potions Give water?"})
      @Name("01. Potion Give Water")
      @LangKey("xat.config.server.potion.toughasnails.water")
      public boolean potion_thirst = true;
      @Name("Normie Potion")
      @Comment({""})
      @LangKey("xat.config.server.potion.human")
      public PotionConfig human = new PotionConfig("minecraft:apple", 3600);
      @Name("Dwarf Potion")
      @Comment({""})
      @LangKey("xat.config.server.potion.dwarf")
      public PotionConfig dwarf = new PotionConfig("minecraft:iron_block", 1200);
      @Name("Elf Potion")
      @Comment({""})
      @LangKey("xat.config.server.potion.elf")
      public PotionConfig elf = new PotionConfig("minecraft:leaves", 1200);
      @Name("Fairy Potion")
      @Comment({""})
      @LangKey("xat.config.server.potion.fairy")
      public PotionConfig fairy = new PotionConfig("minecraft:ghast_tear", 1200);
      @Name("Goblin Potion")
      @Comment({""})
      @LangKey("xat.config.server.potion.goblin")
      public PotionConfig goblin = new PotionConfig("minecraft:leather", 1200);
      @Name("Titan Potion")
      @Comment({""})
      @LangKey("xat.config.server.potion.titan")
      public PotionConfig titan = new PotionConfig("minecraft:golden_apple", 1200);
      @Name("Faelis Potion")
      @Comment({""})
      @LangKey("xat.config.server.potion.faelis")
      public PotionConfig faelis = new PotionConfig("xat:faelis_claw", 1200);
      @Name("Dragon Potion")
      @Comment({""})
      @LangKey("xat.config.server.potion.dragon")
      public PotionConfig dragon = new PotionConfig("minecraft:dragon_breath", 1200);
      @Comment({"Should T&B Transformation Potions work only on players"})
      @Name("09. Work on players only")
      @LangKey("xat.config.server.potion.playersonly")
      public boolean players_only = true;

      public Potions() {
      }
   }

   public class RaceConfigs {
      @Name("Selection Menu")
      @Comment({""})
      public boolean selectionMenu = false;
      @Name("Selection Menu Blacklist")
      @Comment({""})
      @RequiresWorldRestart
      public String[] selectionBlacklist = new String[]{"Dragon"};
      @Name("Fairy Settings")
      @Comment({""})
      @LangKey("xat.config.races.fairy")
      public FairyConfig fairy = new FairyConfig();
      @Name("Dwarf Settings")
      @Comment({""})
      @LangKey("xat.config.races.dwarf")
      public DwarfConfig dwarf = new DwarfConfig();
      @Name("Titan Settings")
      @Comment({""})
      @LangKey("xat.config.races.titan")
      public TitanConfig titan = new TitanConfig();
      @Name("Goblin Settings")
      @Comment({""})
      @LangKey("xat.config.races.goblin")
      public GoblinConfig goblin = new GoblinConfig();
      @Name("Elf Settings")
      @Comment({""})
      @LangKey("xat.config.races.elf")
      public ElfConfig elf = new ElfConfig();
      @Name("Faelis Config")
      @Comment({""})
      @LangKey("xat.config.races.faelis")
      public FaelisConfig faelis = new FaelisConfig();
      @Name("Dragon Config")
      @Comment({""})
      @LangKey("xat.config.races.dragon")
      public DragonConfig dragon = new DragonConfig();

      public RaceConfigs() {
      }
   }

   public class TrinketItems {
      @Name("Transformation Items")
      @Comment({""})
      @LangKey("xat.config.items.transformation")
      public ServerConfig.TrinketItems.RaceRings raceRings = new ServerConfig.TrinketItems.RaceRings();
      @Name("Dragon's Eye Settings")
      @Comment({""})
      @LangKey("xat.config.dragons_eye")
      public ConfigDragonsEye DRAGON_EYE = new ConfigDragonsEye();
      @Name("Ender Queen's Crown Settings")
      @Comment({""})
      @LangKey("xat.config.ender_tiara")
      public ConfigEnderCrown ENDER_CROWN = new ConfigEnderCrown();
      @Name("Damage Shield Settings")
      @Comment({""})
      @LangKey("xat.config.damage_shield")
      public ConfigDamageShield DAMAGE_SHIELD = new ConfigDamageShield();
      @Name("Ring of Enchanted Eyes Settings")
      @Comment({""})
      @LangKey("xat.config.glow_ring")
      public ConfigGlowRing GLOW_RING = new ConfigGlowRing();
      @Name("Poison Stone Settings")
      @Comment({""})
      @LangKey("xat.config.poison_stone")
      public ConfigPoisonStone POISON_STONE = new ConfigPoisonStone();
      @Name("Wither Ring Settings")
      @Comment({""})
      @LangKey("xat.config.wither_ring")
      public ConfigWitherRing WITHER_RING = new ConfigWitherRing();
      @Name("Polarized Stone Settings")
      @Comment({""})
      @LangKey("xat.config.polarized_stone")
      public ConfigPolarizedStone POLARIZED_STONE = new ConfigPolarizedStone();
      @Name("Stone of the Sea Settings")
      @Comment({""})
      @LangKey("xat.config.sea_stone")
      public ConfigSeaStone SEA_STONE = new ConfigSeaStone();
      @Name("Stone of Inertia Null Settings")
      @Comment({""})
      @LangKey("xat.config.inertia_null_stone")
      public ConfigInertiaNull INERTIA_NULL = new ConfigInertiaNull();
      @Name("Stone of Greater Inertia Settings")
      @Comment({""})
      @LangKey("xat.config.greater_inertia_stone")
      public ConfigGreaterInertia GREATER_INERTIA = new ConfigGreaterInertia();
      @Name("Weightless Stone Settings")
      @Comment({""})
      @LangKey("xat.config.weightless_stone")
      public ConfigWeightlessStone WEIGHTLESS_STONE = new ConfigWeightlessStone();
      @Name("Arcing Orb Settings")
      @Comment({""})
      @LangKey("xat.config.arcing_orb")
      public ConfigArcingOrb ARCING_ORB = new ConfigArcingOrb();
      @Name("Teddy Bear Settings")
      @Comment({""})
      @LangKey("xat.config.teddy_bear")
      public ConfigTeddyBear TEDDY_BEAR = new ConfigTeddyBear();
      @Name("Faelis Claw Settings")
      @Comment({""})
      @LangKey("xat.config.faelis_claw")
      public ConfigFaelisClaw FAELIS_CLAW = new ConfigFaelisClaw();

      public TrinketItems() {
      }

      public class RaceRings {
         @Name("Dwarf Ring Settings")
         @Comment({""})
         @LangKey("xat.config.dwarf_ring")
         public TransformationRingConfig DWARF_RING = new TransformationRingConfig();
         @Name("Elf Ring Settings")
         @Comment({""})
         @LangKey("xat.config.elf_ring")
         public TransformationRingConfig ELF_RING = new TransformationRingConfig();
         @Name("Fairy Ring Settings")
         @Comment({""})
         @LangKey("xat.config.fairy_ring")
         public TransformationRingConfig FAIRY_RING = new TransformationRingConfig();
         @Name("Goblin Ring Settings")
         @Comment({""})
         @LangKey("xat.config.goblin_ring")
         public TransformationRingConfig GOBLIN_RING = new TransformationRingConfig();
         @Name("Titan Ring Settings")
         @Comment({""})
         @LangKey("xat.config.titan_ring")
         public TransformationRingConfig TITAN_RING = new TransformationRingConfig();
         @Name("Faelis Ring Settings")
         @Comment({""})
         @LangKey("xat.config.faelis_ring")
         public TransformationRingConfig FAELIS_RING = new TransformationRingConfig();
         @Name("Dragon Ring Settings")
         @Comment({""})
         @LangKey("xat.config.dragon_ring")
         public TransformationRingConfig DRAGON_RING = new TransformationRingConfig();

         public RaceRings() {
         }
      }
   }
}
