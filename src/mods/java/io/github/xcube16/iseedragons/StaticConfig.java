package io.github.xcube16.iseedragons;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

@Config(
   modid = "iseedragons"
)
public class StaticConfig {
   @Comment({"A list of entities and view distances "})
   @Name("EntityDistanceOverrides")
   public static Map<String, Integer> distanceOverrides = new LinkedHashMap();
   @Comment({"The default % chance for a block to drop items when smashed by a dragon (requires ASM.DragonLag)"})
   public static Integer defaultDropChance = Integer.valueOf(100);
   @Comment({"The default % chance for a block to play sound and particle effects when smashed by a dragon (requires ASM.DragonLag)"})
   public static Integer defaultEffectChance = Integer.valueOf(100);
   @Comment({"Prevents lightning strikes from destroying items"})
   public static boolean disableLightningItemDamage = false;
   @Comment({"Prevents Tough As Nails from creating an extra attack entity event"})
   @RequiresMcRestart
   public static boolean preventTANAttackEntityEvent = true;
   @Comment({"A list of block drop % chances (requires ASM.DragonLag)"})
   @Name("DropChances")
   public static Map<String, Integer> dropChances = new LinkedHashMap();
   @Comment({"A list of block effect % chances (requires ASM.DragonLag)"})
   @Name("EffectChances")
   public static Map<String, Integer> effectChances = new LinkedHashMap();
   @Comment({"A list of tools/armor and there new repair item (note: only list one tool of a given 'ToolMaterial')"})
   @Name("RepairFixes")
   public static Map<String, String> repairFixes = new LinkedHashMap();
   @Comment({"A list of items that need to do extra damage to undead enemies"})
   @Name("ExtraUndeadDamage")
   public static Map<String, Float> extraUndeadDamage = new LinkedHashMap();
   @Comment({"A list of dimension IDs that Ice and Fire should NOT generate ANY structures in."})
   @Name("iceandfire_structure_dim_blacklist")
   public static int[] generatorBlacklist = new int[]{111};
   @Comment({"Minimum brightness override (can be negative)"})
   public static float minBrightness = 0.0F;
   @Comment({"Maximum brightness override (can be negative)"})
   public static float maxBrightness = 1.0F;
   @Comment({"Entities that can NOT be stoned by the Gorgon/Gorgon Head"})
   @Name("unstoneable_entities")
   public static String[] unstoneableEntitys = new String[]{"minecraft:wither", "iceandfire:gorgon"};
   @Comment({"Core modifications"})
   @Name("ASM")
   public static StaticConfig.ASM asm = new StaticConfig.ASM();

   public StaticConfig() {
   }

   static {
      distanceOverrides.put("iceandfire:firedragon", Integer.valueOf(256));
      distanceOverrides.put("iceandfire:icedragon", Integer.valueOf(256));
      distanceOverrides.put("iceandfire:seaserpent", Integer.valueOf(256));
      distanceOverrides.put("iceandfire:cyclops", Integer.valueOf(256));
      distanceOverrides.put("battletower:golem", Integer.valueOf(256));
      dropChances.put("iceandfire:ash", Integer.valueOf(2));
      dropChances.put("iceandfire:chared_cobblestone", Integer.valueOf(2));
      dropChances.put("iceandfire:chared_stone", Integer.valueOf(2));
      dropChances.put("iceandfire:chared_grass", Integer.valueOf(2));
      dropChances.put("iceandfire:chared_dirt", Integer.valueOf(2));
      dropChances.put("iceandfire:chared_gravel", Integer.valueOf(2));
      dropChances.put("iceandfire:chared_grass_path", Integer.valueOf(2));
      dropChances.put("minecraft:cobblestone", Integer.valueOf(3));
      dropChances.put("minecraft:dirt", Integer.valueOf(3));
      dropChances.put("minecraft:grass", Integer.valueOf(4));
      dropChances.put("minecraft:sand", Integer.valueOf(3));
      dropChances.put("minecraft:stone", Integer.valueOf(2));
      dropChances.put("iceandfire:frozen_cobblestone", Integer.valueOf(2));
      dropChances.put("iceandfire:frozen_stone", Integer.valueOf(2));
      dropChances.put("iceandfire:frozen_grass", Integer.valueOf(2));
      dropChances.put("iceandfire:frozen_dirt", Integer.valueOf(2));
      dropChances.put("iceandfire:frozen_gravel", Integer.valueOf(2));
      dropChances.put("iceandfire:frozen_grass_path", Integer.valueOf(2));
      dropChances.put("iceandfire:frozen_splinters", Integer.valueOf(2));
      effectChances.put("iceandfire:ash", Integer.valueOf(5));
      effectChances.put("iceandfire:chared_cobblestone", Integer.valueOf(5));
      effectChances.put("iceandfire:chared_stone", Integer.valueOf(5));
      effectChances.put("iceandfire:chared_dirt", Integer.valueOf(5));
      effectChances.put("iceandfire:chared_gravel", Integer.valueOf(5));
      effectChances.put("minecraft:dirt", Integer.valueOf(5));
      effectChances.put("minecraft:stone", Integer.valueOf(5));
      effectChances.put("iceandfire:frozen_cobblestone", Integer.valueOf(5));
      effectChances.put("iceandfire:frozen_stone", Integer.valueOf(5));
      effectChances.put("iceandfire:frozen_dirt", Integer.valueOf(5));
      effectChances.put("iceandfire:frozen_gravel", Integer.valueOf(5));
      effectChances.put("iceandfire:frozen_splinters", Integer.valueOf(5));
      repairFixes.put("aquaculture:neptunium_pickaxe", "aquaculture:loot,1");
      repairFixes.put("aquaculture:neptunium_chestplate", "aquaculture:loot,1");
      extraUndeadDamage.put("minecraft:bedrock", Float.valueOf(100.0F));
   }

   @RequiresMcRestart
   public static final class ASM {
      @Comment({"Patches EntityDragonBase to help with lag"})
      @Name("DragonLag")
      public boolean dragonLag = true;
      @Comment({"Patches ItemModAxe to allow axes to work on modded wood blocks"})
      @Name("IceAndFireAxeFix")
      public boolean iceAndFireAxeFix = true;
      @Comment({"Mutes harmless noisy warnings/errors in the RLCraft modpack"})
      @Name("STFU")
      public boolean stfu = true;
      @Comment({"Prevents Myrmex eggs from being duped when broken with multiple damage events in one tick"})
      @Name("FixMyrmexDupeBug")
      public boolean fixMyrmexDupeBug = true;
      @Comment({"Removes everything from the vanilla achievements system! Can be used to stop log spam when recipes are tweaked."})
      @Name("NukeAchievements")
      public boolean nukeAchievements = false;
      @Comment({"Fixes a bug and allows the last Ice and Fire Sea Serpent type to spawn"})
      @Name("FixSeaSerpentSpawn")
      public boolean fixSeaSerpentSpawn = true;
      @Comment({"Part of a complex fix to prevent players from using dismount (pressing 'shift') to escape a dragon's jaws"})
      @Name("DragonDismountFix")
      public boolean dragonDismountFix = true;
      @Comment({"Adds a hook to Ice and Fire's StructureGenerator so we can cancel generation in some worlds"})
      @Name("HookStructureGenerator")
      public boolean hookStructureGenerator = true;
      @Comment({"Adds a hook to blacklist some entities from being stoned by the Gorgon/Gorgon Head"})
      @Name("HookGorgonHead")
      public boolean hookGorgonHead = true;
      @Comment({"Patches the dummy from MmmMmmMmmMmm (yes, thats a mod name) to show damage and not hearts"})
      @Name("PatchMmmMmm")
      public boolean patchMmmMmm = true;

      public ASM() {
      }
   }
}
