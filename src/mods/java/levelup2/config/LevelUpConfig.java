package levelup2.config;

import com.google.common.collect.Lists;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import levelup2.api.IPlayerSkill;
import levelup2.skills.SkillRegistry;
import levelup2.util.JsonTransfer;
import levelup2.util.Library;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class LevelUpConfig {
   public static boolean resetClassOnDeath = false;
   public static boolean furnaceEjection = false;
   private static boolean resetJsonFiles = false;
   public static boolean damageScaling = false;
   public static boolean alwaysDropChunks = false;
   public static boolean useOreChunks = false;
   public static boolean dupeAllOres = true;
   public static boolean fortuneOre = true;
   public static List<String> cropBlacklist;
   public static List<String> oreList;
   private static String[] ores = new String[]{"oreCoal", "oreIron", "oreGold", "oreDiamond", "oreEmerald", "oreRedstone", "oreLapis", "oreCopper", "oreTin"};
   public static List<Integer> oreColors;
   private static int[] colors = new int[]{3421236, 12360064, 16576075, 6155509, 1564002, 16711680, 1653940, 16739601, 9416910};
   public static List<Integer> oreExperience;
   public static int[] experience = new int[]{1, 1, 2, 4, 4, 2, 2, 1, 1};
   public static List<String> netherOreList;
   private static String[] netherOres = new String[]{"oreQuartz"};
   public static List<Integer> netherOreColors;
   private static int[] netherColors = new int[]{15064789};
   public static List<Integer> netherOreExperience;
   private static int[] netherExperience = new int[]{2};
   public static List<String> endOreList;
   private static String[] endOres = new String[]{"null"};
   public static List<Integer> endOreColors;
   private static int[] endColors = new int[]{0};
   public static List<Integer> endOreExperience;
   private static int[] endExperience = new int[]{0};
   private static Configuration cfg;
   private static Property[] serverProperties;
   public static int rareChance = 1;
   public static int uncommonChance = 15;
   public static int commonChance = 85;
   public static int combinedChance;
   public static int reclassCost = 30;
   public static List<String> oreBlocks;
   public static List<Ingredient> blacklistOutputs;
   private static String[] oreBlockList = new String[]{"geolosys:ore_vanilla", "geolosys:ore"};
   private static Property resetJson;
   private static Path configDir;
   public static Path jsonDir;
   public static Path lootDir;

   public LevelUpConfig() {
   }

   public static void init(File file) {
      configDir = file.getParentFile().toPath().resolve("levelup2");
      jsonDir = configDir.resolve("json");
      lootDir = jsonDir.resolve("loot_tables");
      cfg = new Configuration(file);
      serverProperties = new Property[]{cfg.get("general", "Reset class on death", resetClassOnDeath, "Does the player lose all levels on death?"), cfg.get("general", "Furnace ejects bonus items", furnaceEjection, "Does the furnace eject doubled items?"), cfg.get("general", "Sword skill damage scaling", damageScaling, "Get additional attack power if a mob's max HP is over 20"), cfg.get("general", "Always drop ore chunks", alwaysDropChunks, "Always drop ore chunks on ore harvest"), cfg.get("general", "Break ores into chunks", useOreChunks, "Use ore chunks for ore doubling"), cfg.get("general", "Duplicate any ore", dupeAllOres, "All ores can be doubled, even if they don't have a chunk."), cfg.get("general", "Reclass level cost", reclassCost, "How many levels it will cost to change classes.", 0, 100), cfg.get("general", "Fortune ore doubling", fortuneOre, "Doubled ores are affected by the Fortune enchant.")};
      oreBlocks = Arrays.asList(cfg.getStringList("Special ore cases", "Whitelist", oreBlockList, "Blocks that don't have their own OreDict entry, but still drop registered ores."));
      cropBlacklist = Arrays.asList(cfg.getStringList("Crops for farming", "Blacklist", new String[]{""}, "Crops that won't be affected by farming growth skill, uses internal block name. No sync to client required."));
      oreList = Arrays.asList(cfg.get("general", "Surface Ores to double", ores, "Ores that double from mining efficiency").getStringList());
      oreColors = getColorsFromProperty(cfg.get("general", "Surface Ore colors", colors, "Colors for the surface ore item"));
      oreExperience = getColorsFromProperty(cfg.get("general", "Surface Ore experience", experience, "XP credit levels for Mining Specialization"));
      netherOreList = Arrays.asList(cfg.get("general", "Nether Ores to double", netherOres, "Nether ores that double from mining efficiency").getStringList());
      netherOreColors = getColorsFromProperty(cfg.get("general", "Nether Ore colors", netherColors, "Colors for the nether ore item"));
      netherOreExperience = getColorsFromProperty(cfg.get("general", "Nether Ore experience", netherExperience, "XP credit level for Mining Specialization"));
      endOreList = Arrays.asList(cfg.get("general", "End Ores to double", endOres, "End ores that double from mining efficiency").getStringList());
      endOreColors = getColorsFromProperty(cfg.get("general", "End Ore colors", endColors, "Colors for the end ore item"));
      endOreExperience = getColorsFromProperty(cfg.get("general", "End Ore experience", endExperience, "XP credit level for Mining Specialization"));
      resetJson = cfg.get("debug", "Reset json files", resetJsonFiles, "Forces Level Up! to restore external json files to default");
      resetJsonFiles = resetJson.getBoolean();
      rareChance = cfg.getInt("Rare Digging Loot Chance", "digloot", rareChance, 0, 100, "Chances that a rare loot drop will appear");
      uncommonChance = cfg.getInt("Uncommon Digging Loot Chance", "digloot", uncommonChance, 0, 100, "Chances that an uncommon loot drop will appear");
      commonChance = cfg.getInt("Common Digging Loot Chance", "digloot", commonChance, 0, 100, "Chances that a common loot drop will appear");
      combinedChance = rareChance + uncommonChance + commonChance;
      if (cfg.hasChanged()) {
         cfg.save();
      }

      useServerProperties();
      transferLootTables();
      if (resetJsonFiles) {
         resetJson.set(false);
         cfg.save();
      }

   }

   public static void getBlacklistOutputs() {
      String[] items = cfg.getStringList("CraftingOutputs", "blacklist", new String[0], "Which items, if any, do not give experience upon crafting. Format: modid:item OR modid:item:metadata");
      List<Ingredient> ing = Lists.newArrayList();
      if (items.length > 0) {
         for(String str : items) {
            String[] parts = str.split(":");
            int meta = parts.length == 3 ? Integer.parseInt(parts[2]) : 32767;
            Item item = (Item)Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));
            if (item != null && item != Items.AIR) {
               ing.add(Ingredient.fromStacks(new ItemStack[]{new ItemStack(item, 1, meta)}));
            }
         }
      }

      if (cfg.hasChanged()) {
         cfg.save();
      }

      blacklistOutputs = ing;
   }

   public static Property[] getServerProperties() {
      return serverProperties;
   }

   public static void useServerProperties() {
      resetClassOnDeath = serverProperties[0].getBoolean();
      furnaceEjection = serverProperties[1].getBoolean();
      damageScaling = serverProperties[2].getBoolean();
      alwaysDropChunks = serverProperties[3].getBoolean();
      useOreChunks = serverProperties[4].getBoolean();
      dupeAllOres = serverProperties[5].getBoolean();
      reclassCost = serverProperties[6].getInt();
      fortuneOre = serverProperties[7].getBoolean();
   }

   private static List<Integer> getColorsFromProperty(Property prop) {
      int[] colors = prop.getIntList();
      List<Integer> list = new ArrayList();

      for(int i = 0; i < colors.length; ++i) {
         list.add(Integer.valueOf(colors[i]));
      }

      return list;
   }

   private static void transferLootTables() {
      Set<String> files = new HashSet();
      files.add("fishing/fishing_loot");
      files.add("digging/common_dig");
      files.add("digging/uncommon_dig");
      files.add("digging/rare_dig");
      JsonTransfer.findResources("json/loot_tables", files).stream().forEach((r) -> {
         JsonTransfer.copyResource(r, configDir.resolve(r), resetJsonFiles);
      });
      Library.registerLootTableLocations(files);
   }

   public static void registerSkillProperties() {
      Set<String> files = new HashSet();

      for(IPlayerSkill skill : SkillRegistry.getSkillRegistry()) {
         if (skill.hasExternalJson()) {
            files.add(skill.getJsonLocation());
         }
      }

      JsonTransfer.findResources("json/skills", files).stream().forEach((r) -> {
         JsonTransfer.copyResource(r, configDir.resolve(r), resetJsonFiles);
      });
      SkillRegistry.registerSkillProperties();
   }
}
