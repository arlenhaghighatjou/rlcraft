package levelup2.util;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import levelup2.config.LevelUpConfig;
import levelup2.skills.SkillRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

public class Library {
   public static int[] tenLevels = new int[]{5, 7, 11, 13, 17, 23, 29, 31, 37, 41};
   public static int[] fiveLevels = new int[]{11, 17, 29, 37, 41};
   public static int[] highTenLevels = new int[]{11, 17, 23, 29, 37, 41, 43, 47, 51, 53};
   public static final EaglercraftUUID speedID = EaglercraftUUID.fromString("4f7637c8-6106-4050-96cb-e47f83bfa415");
   public static final EaglercraftUUID sneakID = EaglercraftUUID.fromString("a4dc0b04-f78a-43f6-8805-5ebfbab10b18");
   public static final ResourceLocation SKILL_LOCATION = new ResourceLocation("levelup", "skills");
   private static Set<Block> ores = Sets.newIdentityHashSet();
   private static Map<String, ItemStack> oreToChunk = new HashMap();
   private static List<String> oreNames = new ArrayList();
   private static Set<ResourceLocation> LOOT_TABLES = Sets.newHashSet();
   private static LevelUpLootManager LEVELUP_MANAGER;

   public Library() {
   }

   public static EntityPlayer getPlayerFromUsername(String username) {
      return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? null : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(username);
   }

   public static EntityPlayer getPlayerFromUUID(EaglercraftUUID uuid) {
      return getPlayerFromUsername(getUsernameFromUUID(uuid));
   }

   public static String getUsernameFromUUID(EaglercraftUUID uuid) {
      return UsernameCache.getLastKnownUsername(uuid);
   }

   public static void addToOreList(List<String> ores) {
      oreNames.addAll(ores);
   }

   public static Set<Block> getOreList() {
      return ores;
   }

   public static ItemStack getChunkFromName(String oreName, int fortune) {
      if (oreToChunk.containsKey(oreName)) {
         ItemStack chunk = ((ItemStack)oreToChunk.get(oreName)).copy();
         chunk.grow(fortune);
         return chunk;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public static void registerOres(List<String> oreNames) {
      for(String ore : oreNames) {
         if (OreDictionary.doesOreNameExist(ore) && !OreDictionary.getOres(ore).isEmpty()) {
            for(ItemStack stack : OreDictionary.getOres(ore)) {
               if (stack.getItem() instanceof ItemBlock) {
                  Block block = ((ItemBlock)stack.getItem()).getBlock();
                  if (!ores.contains(block)) {
                     ores.add(block);
                  }
               }
            }
         }
      }

      if (!LevelUpConfig.oreBlocks.isEmpty()) {
         for(String ore : LevelUpConfig.oreBlocks) {
            Block block = (Block)Block.REGISTRY.getObject(new ResourceLocation(ore));
            if (block != Blocks.AIR) {
               ores.add(block);
            }
         }
      }

   }

   public static void assignExperienceValues(List<String> oreNames, List<Integer> oreValue) {
      for(int i = 0; i < oreNames.size(); ++i) {
         String oreName = (String)oreNames.get(i);
         if (!oreName.equals("null")) {
            int value = i < oreValue.size() ? ((Integer)oreValue.get(i)).intValue() : 1;
            if (value > 0) {
               SkillRegistry.addStackToOreBonus(oreName, value);
            }
         }
      }

   }

   public static String getOreNameForBlock(ItemStack blockStack) {
      for(String ore : oreNames) {
         for(ItemStack stack : OreDictionary.getOres(ore)) {
            if (ItemStack.areItemsEqual(stack, blockStack) || ItemStack.areItemsEqual(new ItemStack(blockStack.getItem(), 1, 32767), stack)) {
               return ore;
            }
         }
      }

      return null;
   }

   public static boolean isOre(ItemStack blockStack) {
      for(String oreName : OreDictionary.getOreNames()) {
         if (oreName.startsWith("ore") && OreDictionary.containsMatch(false, OreDictionary.getOres(oreName), new ItemStack[]{blockStack})) {
            return true;
         }
      }

      return false;
   }

   public static void registerOreToChunk(List<String> ores, Item item) {
      for(int i = 0; i < ores.size(); ++i) {
         oreToChunk.put(ores.get(i), new ItemStack(item, 2, i));
      }

   }

   public static void removeFromList(List<ItemStack> drops, ItemStack toRemove) {
      Iterator<ItemStack> itr = drops.iterator();

      while(itr.hasNext()) {
         ItemStack drop = (ItemStack)itr.next();
         if (!drop.isEmpty() && ItemStack.areItemsEqual(toRemove, drop)) {
            itr.remove();
         }
      }

   }

   public static void registerLootManager() {
      LootFunctionManager.registerFunction(new FortuneEnchantBonus.Serializer());
      LEVELUP_MANAGER = new LevelUpLootManager();
   }

   public static LevelUpLootManager getLootManager() {
      return LEVELUP_MANAGER;
   }

   public static Set<ResourceLocation> getLootTables() {
      return LOOT_TABLES;
   }

   public static void registerLootTableLocations(Set<String> files) {
      files.stream().forEach((s) -> {
         LOOT_TABLES.add(new ResourceLocation("levelup", s));
      });
   }
}
