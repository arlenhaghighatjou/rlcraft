package levelup2.skills;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import levelup2.api.IPlayerSkill;
import levelup2.capability.PlayerCapability;
import levelup2.config.LevelUpConfig;
import levelup2.items.ItemOreChunk;
import levelup2.items.ItemRespecBook;
import levelup2.network.SkillPacketHandler;
import levelup2.player.IPlayerClass;
import levelup2.skills.combat.ArrowSpeedBonus;
import levelup2.skills.combat.DrawSpeedBonus;
import levelup2.skills.combat.NaturalArmorBonus;
import levelup2.skills.combat.ShieldBlockBonus;
import levelup2.skills.combat.StealthBonus;
import levelup2.skills.combat.StealthDamage;
import levelup2.skills.combat.StealthSpeed;
import levelup2.skills.combat.SwordCritBonus;
import levelup2.skills.combat.SwordDamageBonus;
import levelup2.skills.combat.XPBonusCombat;
import levelup2.skills.crafting.BrewingEfficiencyBonus;
import levelup2.skills.crafting.FishingLootBonus;
import levelup2.skills.crafting.FoodGrowthBonus;
import levelup2.skills.crafting.FoodHarvestBonus;
import levelup2.skills.crafting.FurnaceEfficiencyBonus;
import levelup2.skills.crafting.FurnaceSmeltBonus;
import levelup2.skills.crafting.XPBonusCrafting;
import levelup2.skills.mining.DiggingTreasureBonus;
import levelup2.skills.mining.FallDamageBonus;
import levelup2.skills.mining.FlintLootBonus;
import levelup2.skills.mining.SprintSpeedBonus;
import levelup2.skills.mining.StoneMiningBonus;
import levelup2.skills.mining.StoneSpeedBonus;
import levelup2.skills.mining.WoodSpeedBonus;
import levelup2.skills.mining.WoodcuttingBonus;
import levelup2.skills.mining.XPBonusMining;
import levelup2.util.Library;
import levelup2.util.PlankCache;
import levelup2.util.SkillProperties;
import levelup2.util.SmeltingBlacklist;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class SkillRegistry {
   private static Map<String, Integer> oreBonusXP = new HashMap();
   private static List<IPlayerSkill> skillRegistry = new ArrayList();
   private static Map<String, IPlayerSkill> skillHashMap = new HashMap();
   private static Map<String, SkillProperties> skillProperties = new HashMap();
   private static List<IPlantable> cropBlacklist = new ArrayList();
   public static int smallestDisplayColumn = 0;
   public static int smallestDisplayRow = 0;
   public static int largestDisplayColumn = 0;
   public static int largestDisplayRow = 0;
   public static Item surfaceOreChunk = (Item)(new ItemOreChunk(LevelUpConfig.oreList)).setUnlocalizedName("levelup:surfaceore").setRegistryName(new ResourceLocation("levelup2", "surfaceore"));
   public static Item netherOreChunk = (Item)(new ItemOreChunk(LevelUpConfig.netherOreList)).setUnlocalizedName("levelup:netherore").setRegistryName(new ResourceLocation("levelup2", "netherore"));
   public static Item endOreChunk = (Item)(new ItemOreChunk(LevelUpConfig.endOreList)).setUnlocalizedName("levelup:endore").setRegistryName(new ResourceLocation("levelup2", "endore"));
   public static Item respecBook = (Item)(new ItemRespecBook()).setUnlocalizedName("levelup:respec").setRegistryName(new ResourceLocation("levelup2", "respecbook"));

   public SkillRegistry() {
   }

   public static void loadSkills() {
      addSkill(new XPBonusCombat());
      addSkill(new XPBonusCrafting());
      addSkill(new XPBonusMining());
      addSkill(new StoneSpeedBonus());
      addSkill(new StoneMiningBonus());
      addSkill(new WoodSpeedBonus());
      addSkill(new WoodcuttingBonus());
      addSkill(new FlintLootBonus());
      addSkill(new DiggingTreasureBonus());
      addSkill(new SwordCritBonus());
      addSkill(new SwordDamageBonus());
      addSkill(new DrawSpeedBonus());
      addSkill(new ArrowSpeedBonus());
      addSkill(new StealthBonus());
      addSkill(new StealthDamage());
      addSkill(new ShieldBlockBonus());
      addSkill(new NaturalArmorBonus());
      addSkill(new FoodGrowthBonus());
      addSkill(new FoodHarvestBonus());
      addSkill(new SprintSpeedBonus());
      addSkill(new FallDamageBonus());
      addSkill(new StealthSpeed());
      addSkill(new FurnaceEfficiencyBonus());
      addSkill(new FurnaceSmeltBonus());
      addSkill(new BrewingEfficiencyBonus());
      addSkill(new FishingLootBonus());
      addCropsToBlacklist(LevelUpConfig.cropBlacklist);
      Library.registerLootManager();
   }

   public static void postLoadSkills() {
      for(IPlayerSkill skill : skillRegistry) {
         if (skill.hasSubscription()) {
            skill.register(MinecraftForge.EVENT_BUS);
         }
      }

   }

   public static void registerRecipes() {
      if (!isNullList(LevelUpConfig.oreList)) {
         Library.registerOreToChunk(LevelUpConfig.oreList, surfaceOreChunk);
         Library.addToOreList(LevelUpConfig.oreList);
         Library.assignExperienceValues(LevelUpConfig.oreList, LevelUpConfig.oreExperience);
         registerSmelting(LevelUpConfig.oreList, surfaceOreChunk);
         if (LevelUpConfig.oreList.contains("oreRedstone")) {
            Library.getOreList().add(Blocks.LIT_REDSTONE_ORE);
         }
      }

      if (!isNullList(LevelUpConfig.netherOreList)) {
         Library.registerOreToChunk(LevelUpConfig.netherOreList, netherOreChunk);
         Library.addToOreList(LevelUpConfig.netherOreList);
         Library.assignExperienceValues(LevelUpConfig.netherOreList, LevelUpConfig.netherOreExperience);
         registerSmelting(LevelUpConfig.netherOreList, netherOreChunk);
      }

      if (!isNullList(LevelUpConfig.endOreList)) {
         Library.registerOreToChunk(LevelUpConfig.endOreList, endOreChunk);
         Library.addToOreList(LevelUpConfig.endOreList);
         Library.assignExperienceValues(LevelUpConfig.endOreList, LevelUpConfig.endOreExperience);
         registerSmelting(LevelUpConfig.endOreList, endOreChunk);
      }

      List<String> names = new ArrayList();
      if (LevelUpConfig.dupeAllOres) {
         for(String name : OreDictionary.getOreNames()) {
            if (name.startsWith("ore")) {
               names.add(name);
            }

            if (!Library.getOreList().contains(Blocks.LIT_REDSTONE_ORE)) {
               Library.getOreList().add(Blocks.LIT_REDSTONE_ORE);
            }
         }
      } else {
         names.addAll(LevelUpConfig.oreList);
         names.addAll(LevelUpConfig.netherOreList);
         names.addAll(LevelUpConfig.endOreList);
      }

      Library.registerOres(names);
   }

   private static void registerSmelting(List<String> ores, Item item) {
      for(int i = 0; i < ores.size(); ++i) {
         String names = (String)ores.get(i);
         if (OreDictionary.doesOreNameExist(names)) {
            ItemStack ore = getOreEntry(names);
            if (!ore.isEmpty() && !FurnaceRecipes.instance().getSmeltingResult(ore).isEmpty()) {
               ItemStack result = FurnaceRecipes.instance().getSmeltingResult(ore);
               GameRegistry.addSmelting(new ItemStack(item, 1, i), result, FurnaceRecipes.instance().getSmeltingExperience(result));
            }
         }
      }

      SmeltingBlacklist.addItem(new ItemStack(Blocks.SPONGE, 1, 1));

      for(String ore : OreDictionary.getOreNames()) {
         if (ore.startsWith("dust")) {
            SmeltingBlacklist.addOres(ore);
         }
      }

   }

   private static void registerCrafting(List<String> ores, Item item) {
      for(int i = 0; i < ores.size(); ++i) {
         String names = (String)ores.get(i);
         if (OreDictionary.doesOreNameExist(names)) {
            ItemStack ore = getOreEntry(names);
            if (!ore.isEmpty()) {
               ItemStack chunk = new ItemStack(item, 1, i);
               registerShapelessRecipe(ore.copy(), chunk, chunk);
               OreDictionary.registerOre(names, chunk);
            }
         }
      }

   }

   private static void registerShapelessRecipe(ItemStack output, Object... inputs) {
      GameRegistry.findRegistry(IRecipe.class).register(new ShapelessOreRecipe(new ResourceLocation("levelup", "orechunk"), output, inputs));
   }

   public static boolean isNullList(List<String> list) {
      return !list.isEmpty() && ((String)list.get(0)).equals("null");
   }

   public static List<IPlayerSkill> getSkillRegistry() {
      return skillRegistry;
   }

   public static IPlayerSkill getSkillFromName(String name) {
      return skillHashMap.containsKey(name) ? (IPlayerSkill)skillHashMap.get(name) : null;
   }

   public static void addSkill(IPlayerSkill skill) {
      skillRegistry.add(skill);
      skillHashMap.put(skill.getSkillName(), skill);
   }

   public static int getSkillLevel(EntityPlayer player, String skill) {
      return getPlayer(player).getSkillLevel(skill);
   }

   public static IPlayerClass getPlayer(EntityPlayer player) {
      return (IPlayerClass)player.getCapability(PlayerCapability.PLAYER_CLASS, (EnumFacing)null);
   }

   public static void addStackToOreBonus(String stack, int bonusXP) {
      oreBonusXP.put(stack, Integer.valueOf(bonusXP));
   }

   public static Map<String, Integer> getOreBonusXP() {
      return oreBonusXP;
   }

   public static void addExperience(EntityPlayer player, int amount) {
      player.addExperience(amount);
   }

   public static boolean stackMatches(ItemStack stack1, ItemStack stack2) {
      if (!stack1.isEmpty() && !stack2.isEmpty()) {
         if (stack1.getItemDamage() != 32767 && stack2.getItemDamage() != 32767) {
            return stack1.isItemEqual(stack2);
         } else {
            return stack1.getItem() == stack2.getItem();
         }
      } else {
         return false;
      }
   }

   public static boolean listContains(ItemStack stack, List<ItemStack> list) {
      if (list.isEmpty()) {
         return false;
      } else {
         for(ItemStack lStack : list) {
            if (stackMatches(stack, lStack)) {
               return true;
            }
         }

         return false;
      }
   }

   public static void increaseSkillLevel(EntityPlayer player, String skillName) {
      IPlayerSkill skill = getPlayer(player).getSkillFromName(skillName);
      int skillCost = skill.getLevelCost(getPlayer(player).getSkillLevel(skillName));
      if (skillCost > 0 && player.experienceLevel >= skillCost) {
         player.experienceLevel -= skillCost;
         getPlayer(player).addToSkill(skillName, 1);
      }

   }

   public static void loadPlayer(EntityPlayer player) {
      if (player instanceof EntityPlayerMP) {
         byte spec = getPlayer(player).getSpecialization();
         Map<String, Integer> skills = getPlayer(player).getSkills();
         SkillPacketHandler.initChannel.sendTo(SkillPacketHandler.getPacket(Side.CLIENT, 0, spec, skills), (EntityPlayerMP)player);
      }

   }

   public static void addCropsToBlacklist(List<String> blacklist) {
      for(String str : blacklist) {
         Block block = (Block)Block.REGISTRY.getObject(new ResourceLocation(str));
         if (block != null && block instanceof IPlantable) {
            cropBlacklist.add((IPlantable)block);
         }
      }

   }

   public static List<IPlantable> getCropBlacklist() {
      return cropBlacklist;
   }

   public static ItemStack getOreEntry(String ore) {
      if (OreDictionary.doesOreNameExist(ore) && !OreDictionary.getOres(ore).isEmpty()) {
         for(ItemStack stack : OreDictionary.getOres(ore)) {
            if (stack.getItem() instanceof ItemBlock && !stack.hasTagCompound()) {
               return new ItemStack(stack.getItem(), 1, stack.getMetadata());
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public static void initPlankCache() {
      PlankCache.refresh();

      for(ItemStack log : OreDictionary.getOres("logWood")) {
         if (log.getItem() instanceof ItemBlock) {
            if (log.getMetadata() == 32767) {
               for(int i = 0; i < 4; ++i) {
                  ItemStack logTest = new ItemStack(log.getItem(), 1, i);
                  ItemStack plank = getPlankOutput(logTest);
                  if (!plank.isEmpty()) {
                     registerLog(logTest, plank);
                  }
               }
            } else {
               ItemStack plank = getPlankOutput(log);
               if (!plank.isEmpty()) {
                  registerLog(log, plank);
               }
            }
         }
      }

   }

   private static void registerLog(ItemStack log, ItemStack plank) {
      Block block = ((ItemBlock)log.getItem()).getBlock();
      ItemStack plankCopy = plank.copy();
      plankCopy.setCount(plank.getCount() / 2);
      PlankCache.addBlock(block, log.getMetadata(), plankCopy);
   }

   private static ItemStack getPlankOutput(ItemStack log) {
      Iterator<IRecipe> it = CraftingManager.REGISTRY.iterator();
      ItemStack stack = ItemStack.EMPTY;

      while(it.hasNext() && stack.isEmpty()) {
         IRecipe recipe = (IRecipe)it.next();
         if (recipe.getGroup().equals("planks")) {
            NonNullList<Ingredient> ing = recipe.getIngredients();
            if (isPlank(recipe.getRecipeOutput())) {
               for(Ingredient in : ing) {
                  for(ItemStack check : in.getMatchingStacks()) {
                     if (check.isItemEqual(log)) {
                        stack = recipe.getRecipeOutput().copy();
                     }
                  }
               }
            }
         }
      }

      return stack;
   }

   private static boolean isPlank(ItemStack output) {
      for(ItemStack plank : OreDictionary.getOres("plankWood")) {
         if (plank.getMetadata() == 32767) {
            return output.getItem() == plank.getItem();
         }

         if (plank.isItemEqual(output)) {
            return true;
         }
      }

      return false;
   }

   public static void registerSkillProperties() {
      for(IPlayerSkill skill : getSkillRegistry()) {
         SkillProperties prop = loadFromJson(skill);
         if (prop != null) {
            skillProperties.put(skill.getSkillName(), prop);
         }
      }

      calculateHighLow();
   }

   private static SkillProperties loadFromJson(IPlayerSkill skill) {
      SkillProperties prop = null;
      VFile2 dir = new VFile2(LevelUpConfig.jsonDir, "skills");
      VFile2 location = new VFile2(dir, skill.getJsonLocation() + ".json");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      String json = location.getAllChars();
      if (json == null) {
         System.err.println("[LEVELUP] ERROR: Cannot load skill json " + location.getPath());
      } else {
         prop = SkillProperties.fromJson(skill.getSkillName(), (JsonObject)JsonUtils.gsonDeserialize(gson, json, JsonObject.class));
      }

      return prop;
   }

   public static SkillProperties getProperty(IPlayerSkill skill) {
      return (SkillProperties)skillProperties.get(skill.getSkillName());
   }

   public static void calculateHighLow() {
      largestDisplayRow = 0;
      largestDisplayColumn = 0;
      smallestDisplayRow = 0;
      smallestDisplayColumn = 0;

      for(IPlayerSkill skill : getSkillRegistry()) {
         IPlayerSkill sk = getSkillFromName(skill.getSkillName());
         int column = sk.getSkillColumn();
         int row = sk.getSkillRow();
         if (column > largestDisplayColumn) {
            largestDisplayColumn = column;
         } else if (column < smallestDisplayColumn) {
            smallestDisplayColumn = column;
         }

         if (row > largestDisplayRow) {
            largestDisplayRow = row;
         } else if (row < smallestDisplayRow) {
            smallestDisplayRow = row;
         }
      }

   }
}
