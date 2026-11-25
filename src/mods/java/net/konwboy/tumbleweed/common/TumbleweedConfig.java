package net.konwboy.tumbleweed.common;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.konwboy.tumbleweed.Tumbleweed;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;

@Config(
   modid = "tumbleweed"
)
public class TumbleweedConfig {
   @Name("Chance")
   @Comment({"Chance a tumbleweed spawns in a chunk."})
   public static double spawnChance = 0.5D;
   @Name("Enable Drops")
   public static boolean enableDrops = true;
   @Name("Damage Crops")
   @Comment({"Should tumbleweeds damage crops."})
   public static boolean damageCrops = true;
   @Name("Max Per Player")
   @Comment({"Maximum number of tumbleweeds existing per player (technically 17x17 loaded chunks)."})
   public static int maxPerPlayer = 8;
   @Name("Drops")
   @Comment({"These items will drop from a tumbleweed upon destroying.\n<weight> <mod>:<item>:[metadata] [amount]"})
   public static String[] dropsData = TumbleweedConfig.Logic.DEFAULT_DROPS;
   @Name("Spawning Blocks")
   @Comment({"Blocks from which tumbleweeds can spawn.\n<mod>:<block>:[metadata]"})
   public static String[] spawningBlocksData = TumbleweedConfig.Logic.DEFAULT_BLOCKS;
   @Name("Biome Whitelist")
   @Comment({"If not empty, tumbleweeds spawn ONLY in the specified biomes. Else they appear in all hot, dry biomes.\nExample entry: minecraft:desert"})
   public static String[] biomeWhitelistData = new String[0];

   public TumbleweedConfig() {
   }

   public static class Logic {
      private static final String[] DEFAULT_DROPS = new String[]{"3 minecraft:bone", "3 minecraft:deadbush", "3 minecraft:string", "3 minecraft:feather", "3 minecraft:wheat", "3 minecraft:stick", "3 minecraft:reeds", "2 minecraft:melon_seeds", "2 minecraft:pumpkin_seeds", "2 minecraft:gold_nugget", "1 minecraft:name_tag", "1 minecraft:saddle", "1 minecraft:emerald", "1 minecraft:diamond", "1 minecraft:iron_ingot", "1 minecraft:gold_ingot"};
      private static final String[] DEFAULT_BLOCKS = new String[]{"minecraft:deadbush"};
      private static List<TumbleweedConfig.WeightedItem> weightedItems = Lists.newArrayList();
      private static int totalWeight;
      public static Set<ResourceLocation> biomeWhitelist = Sets.newHashSet();
      public static Set<TumbleweedConfig.Metadata> spawningBlocks = Sets.newHashSet();

      public Logic() {
      }

      public static void reload() {
         totalWeight = 0;
         weightedItems.clear();
         biomeWhitelist.clear();
         spawningBlocks.clear();

         for(String s : TumbleweedConfig.dropsData) {
            String[] itemData = s.split(" ");
            if (itemData.length >= 2) {
               double weight = Double.parseDouble(itemData[0]);
               TumbleweedConfig.Metadata meta = getMetadata(itemData[1]);
               if (meta != null && Item.REGISTRY.containsKey(meta.id)) {
                  int amount = 1;
                  if (itemData.length >= 3) {
                     try {
                        amount = Integer.parseInt(itemData[2]);
                     } catch (NumberFormatException var10) {
                        ;
                     }
                  }

                  weightedItems.add(new TumbleweedConfig.WeightedItem(getMetadata(itemData[1]), amount, weight));
                  totalWeight = (int)((double)totalWeight + weight);
               } else {
                  Tumbleweed.logger.warn("Item {} is invalid.", itemData[1]);
               }
            }
         }

         for(String entry : TumbleweedConfig.biomeWhitelistData) {
            ResourceLocation id = new ResourceLocation(entry);
            if (Biome.REGISTRY.containsKey(id)) {
               biomeWhitelist.add(id);
            } else {
               Tumbleweed.logger.warn("Biome {} is invalid.", id);
            }
         }

         for(String entry : TumbleweedConfig.spawningBlocksData) {
            TumbleweedConfig.Metadata meta = getMetadata(entry);
            if (meta != null && Block.REGISTRY.containsKey(meta.id)) {
               spawningBlocks.add(meta);
            } else {
               Tumbleweed.logger.warn("Block {} is invalid.", entry);
            }
         }

      }

      public static ItemStack getRandomItem() {
         double randomWeight = (double)totalWeight * Math.random();
         double countedWeight = 0.0D;

         for(TumbleweedConfig.WeightedItem weightedItem : weightedItems) {
            countedWeight += weightedItem.weight;
            if (countedWeight >= randomWeight) {
               Item item = (Item)Item.REGISTRY.getObject(weightedItem.meta.id);
               if (item != null) {
                  return new ItemStack(item, weightedItem.amount, weightedItem.meta.meta);
               }
            }
         }

         return null;
      }

      @Nullable
      private static TumbleweedConfig.Metadata getMetadata(String id) {
         String[] split = id.split(":");
         if (split.length < 2) {
            return null;
         } else {
            int meta = 0;
            if (split.length >= 3) {
               try {
                  meta = Integer.parseInt(split[2]);
               } catch (NumberFormatException var4) {
                  ;
               }
            }

            return new TumbleweedConfig.Metadata(new ResourceLocation(split[0], split[1]), meta);
         }
      }
   }

   public static class Metadata {
      public final ResourceLocation id;
      public final int meta;

      public Metadata(ResourceLocation id, int meta) {
         this.id = id;
         this.meta = meta;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            TumbleweedConfig.Metadata metadata = (TumbleweedConfig.Metadata)o;
            return this.meta == metadata.meta && Objects.equal(this.id, metadata.id);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.id, this.meta});
      }
   }

   private static class WeightedItem {
      public final TumbleweedConfig.Metadata meta;
      public final int amount;
      public final double weight;

      public WeightedItem(TumbleweedConfig.Metadata meta, int amount, double weight) {
         this.meta = meta;
         this.amount = amount;
         this.weight = weight;
      }
   }
}
