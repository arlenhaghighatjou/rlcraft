package jaredbgreat.dldungeons.pieces.chests;

import jaredbgreat.dldungeons.ConfigHandler;
import jaredbgreat.dldungeons.nbt.NBTHelper;
import jaredbgreat.dldungeons.nbt.tags.ITag;
import jaredbgreat.dldungeons.util.debug.Logging;
import jaredbgreat.dldungeons.util.parser.Tokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class LootItem {
   private static IForgeRegistry ItemRegistry;
   private static Map<LootItem.ItemPrototype, Integer> prototypes = new HashMap();
   Item item;
   int min;
   int max;
   int meta;
   int level;
   ArrayList<ITag> nbtData;
   public static LootItem stoneSword = new LootItem(getItem("stone_sword"), 1, 1);
   public static LootItem ironSword = new LootItem(getItem("iron_sword"), 1, 1);
   public static LootItem diamondSword = new LootItem(getItem("diamond_sword"), 1, 1);
   public static LootItem bow = new LootItem(getItem("bow"), 1, 1);
   public static LootItem fewArrows = new LootItem(getItem("arrow"), 4, 12);
   public static LootItem someArrows = new LootItem(getItem("arrow"), 8, 16);
   public static LootItem manyArrows = new LootItem(getItem("arrow"), 16, 48);
   public static LootItem fewTorches = new LootItem(Block.getBlockFromName("torch"), 4, 12);
   public static LootItem someToreches = new LootItem(Block.getBlockFromName("torch"), 12, 16);
   public static LootItem manyTorches = new LootItem(Block.getBlockFromName("torch"), 16, 24);
   public static LootItem leatherHat = new LootItem(getItem("leather_helmet"), 1, 1);
   public static LootItem goldHat = new LootItem(getItem("golden_helmet"), 1, 1);
   public static LootItem ironHat = new LootItem(getItem("iron_helmet"), 1, 1);
   public static LootItem diamondHat = new LootItem(getItem("diamond_helmet"), 1, 1);
   public static LootItem leatherBoots = new LootItem(getItem("leather_boots"), 1, 1);
   public static LootItem goldBoots = new LootItem(getItem("golden_boots"), 1, 1);
   public static LootItem ironBoots = new LootItem(getItem("iron_boots"), 1, 1);
   public static LootItem diamondBoots = new LootItem(getItem("diamond_boots"), 1, 1);
   public static LootItem leatherPants = new LootItem(getItem("leather_leggings"), 1, 1);
   public static LootItem goldPants = new LootItem(getItem("golden_leggings"), 1, 1);
   public static LootItem ironPants = new LootItem(getItem("iron_leggings"), 1, 1);
   public static LootItem diamondPants = new LootItem(getItem("diamond_leggings"), 1, 1);
   public static LootItem leatherChest = new LootItem(getItem("leather_chestplate"), 1, 1);
   public static LootItem goldChest = new LootItem(getItem("golden_chestplate"), 1, 1);
   public static LootItem ironChest = new LootItem(getItem("iron_chestplate"), 1, 1);
   public static LootItem diamondChest = new LootItem(getItem("diamond_chestplate"), 1, 1);
   public static LootItem someBread = new LootItem(getItem("bread"), 2, 4);
   public static LootItem moreBread = new LootItem(getItem("bread"), 4, 8);
   public static LootItem someSteak = new LootItem(getItem("cooked_beef"), 2, 4);
   public static LootItem moreSteak = new LootItem(getItem("cooked_beef"), 4, 8);
   public static LootItem someChicken = new LootItem(getItem("cooked_chicken"), 2, 4);
   public static LootItem moreChicken = new LootItem(getItem("cooked_chicken"), 4, 8);
   public static LootItem someApples = new LootItem(getItem("apple"), 1, 3);
   public static LootItem moreApples = new LootItem(getItem("apple"), 2, 7);
   public static LootItem somePie = new LootItem(getItem("pumpkin_pie"), 1, 3);
   public static LootItem morePie = new LootItem(getItem("pumpkin_pie"), 2, 7);
   public static LootItem goldApple = new LootItem(getItem("golden_apple"), 1, 1);
   public static LootItem goldApples = new LootItem(getItem("golden_apple"), 1, 3);
   public static LootItem oneGold = new LootItem(getItem("gold_ingot"), 1, 1);
   public static LootItem someGold = new LootItem(getItem("gold_ingot"), 2, 5);
   public static LootItem moreGold = new LootItem(getItem("gold_ingot"), 3, 8);
   public static LootItem someIron = new LootItem(getItem("iron_ingot"), 1, 8);
   public static LootItem moreIron = new LootItem(getItem("iron_ingot"), 3, 12);
   public static LootItem oneDiamond = new LootItem(getItem("iron_ingot"), 1, 1);
   public static LootItem diamonds = new LootItem(getItem("diamond"), 1, 4);
   public static LootItem manyDiamonds = new LootItem(getItem("diamond"), 3, 9);
   public static LootItem oneEmerald = new LootItem(getItem("emerald"), 1, 1);
   public static LootItem emeralds = new LootItem(getItem("emerald"), 1, 4);
   public static LootItem manyEmerald = new LootItem(getItem("emerald"), 3, 7);
   public static LootItem saddle = new LootItem(getItem("saddle"), 1, 1);
   public static LootItem ironBarding = new LootItem(getItem("iron_horse_armor"), 1, 1);
   public static LootItem goldBarding = new LootItem(getItem("golden_horse_armor"), 1, 1);
   public static LootItem diamondBard = new LootItem(getItem("diamond_horse_armor"), 1, 1);
   public static LootItem book = new LootItem(getItem("book"), 1, 1);
   public static LootItem someBooks = new LootItem(getItem("book"), 2, 5);
   public static LootItem moreBooks = new LootItem(getItem("book"), 3, 8);
   public static LootItem nameTag = new LootItem(getItem("name_tag"), 1, 1);
   public static LootItem enderpearl = new LootItem(getItem("ender_pearl"), 1, 2);
   public static LootItem enderpearls = new LootItem(getItem("ender_pearl"), 1, 9);
   public static LootItem eyeOfEnder = new LootItem(getItem("ender_eye"), 1, 2);
   public static LootItem blazeRod = new LootItem(getItem("blaze_rod"), 1, 2);
   public static LootItem netherstar = new LootItem(getItem("nether_star"), 1, 1);
   public static LootItem disc13 = new LootItem(getItem("record_13"), 1, 1);
   public static LootItem discCat = new LootItem(getItem("record_cat"), 1, 1);
   public static LootItem discBlocks = new LootItem(getItem("record_blocks"), 1, 1);
   public static LootItem discChirp = new LootItem(getItem("stone_chirp"), 1, 1);
   public static LootItem discFar = new LootItem(getItem("record_far"), 1, 1);
   public static LootItem discMall = new LootItem(getItem("record_mall"), 1, 1);
   public static LootItem discMellohi = new LootItem(getItem("record_mellohi"), 1, 1);
   public static LootItem discStrad = new LootItem(getItem("record_strad"), 1, 1);
   public static LootItem discWard = new LootItem(getItem("record_ward"), 1, 1);
   public static LootItem disc11 = new LootItem(getItem("record_11"), 1, 1);
   public static LootItem discWait = new LootItem(getItem("record_wait"), 1, 1);

   public LootItem(String id, int min, int max, int level) {
      this.metaParse(id);
      if (min > max) {
         min = max;
      }

      this.min = min;
      this.max = max;
      this.level = level;
      if (this.item == null) {
         String error = "[DLDUNGEONS] ERROR! Item read as \"" + id + "\" was was not in registry (returned null).";
         Logging.logError(error);
         if (ConfigHandler.failfast) {
            throw new NoSuchElementException(error);
         }
      } else {
         this.fixLevel();
      }

   }

   public LootItem(Item item, int min, int max) {
      this.item = item;
      if (min > max) {
         min = max;
      }

      this.min = min;
      this.max = max;
      this.level = 6;
   }

   public LootItem(Block item, int min, int max) {
      this.item = Item.getItemFromBlock(item);
      if (min > max) {
         min = max;
      }

      this.min = min;
      this.max = max;
      this.level = 6;
   }

   private void fixLevel() {
      if (this.max == 1) {
         LootItem.ItemPrototype p = new LootItem.ItemPrototype(this.item, this.meta);
         if (prototypes.containsKey(p)) {
            int l = ((Integer)prototypes.get(p)).intValue();
            if (l < this.level) {
               this.level = l;
            }

            if (l > this.level) {
               prototypes.put(p, Integer.valueOf(this.level));
            }
         } else {
            prototypes.put(p, Integer.valueOf(this.level));
         }
      }

   }

   private void metaParse(String in) {
      Tokenizer tokens = new Tokenizer(in, "({[]})");
      String name = tokens.nextToken();
      this.item = (Item)Item.REGISTRY.getObject(new ResourceLocation(name));
      if (this.item == null) {
         Logging.logError("[DLDUNGEONS] ERROR! Item read as \"" + in + "\" was was not in registry (returned null).");
      }

      if (tokens.hasMoreTokens()) {
         this.meta = Integer.parseInt(tokens.nextToken());
      }

   }

   private static Item getItem(String in) {
      return (Item)Item.REGISTRY.getObject(new ResourceLocation(in));
   }

   public void addNbt(String in) {
      if (this.nbtData == null) {
         this.nbtData = new ArrayList();
      }

      this.nbtData.add(NBTHelper.getTagFromLabel(in));
   }

   public void trimNbt() {
      if (this.nbtData != null) {
         this.nbtData.trimToSize();
      }

   }

   public ItemStack getStack(EaglercraftRandom random) {
      ItemStack out;
      if (this.max <= this.min) {
         if (this.item instanceof Item) {
            out = new ItemStack(this.item, this.max);
         } else {
            out = new ItemStack(this.item, this.max);
         }
      } else if (this.item instanceof Item) {
         out = new ItemStack(this.item, random.nextInt(this.max - this.min) + this.min + 1);
      } else {
         out = new ItemStack(this.item, random.nextInt(this.max - this.min) + this.min + 1);
      }

      if (out.getItem() == null) {
         return null;
      } else {
         if (out.getHasSubtypes() && this.meta >= 0) {
            out.setItemDamage(this.meta);
         } else {
            out.setItemDamage(0);
         }

         if (this.nbtData != null && !this.nbtData.isEmpty()) {
            for(ITag tag : this.nbtData) {
               NBTHelper.setNbtTag(out, tag);
            }
         }

         return out;
      }
   }

   private static class ItemPrototype {
      public final Item item;
      public final int meta;

      public ItemPrototype(Item i, int m) {
         this.item = i;
         this.meta = m;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof LootItem.ItemPrototype)) {
            return false;
         } else {
            LootItem.ItemPrototype p = (LootItem.ItemPrototype)obj;
            return p.item == this.item && p.meta == this.meta;
         }
      }

      public int hashCode() {
         return this.item.hashCode() + this.meta * 102181;
      }
   }
}
