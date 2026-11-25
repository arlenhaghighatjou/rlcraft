package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public final class RID {
   public static final String adhesiveBlock = "blockAdhesive";
   public static final String anyArrow = "arrow";
   public static final String anyBone = "bone";
   public static final String anyChest = "chest";
   public static final String anyColorlessGlass = "blockGlassColorless";
   public static final String anyDirt = "dirt";
   public static final String anyDye = "dye";
   public static final String anyEgg = "egg";
   public static final String anyFeather = "feather";
   public static final String anyGlass = "blockGlass";
   public static final String anyPaper = "paper";
   public static final String anyString = "string";
   public static final String anyStone = "stone";
   public static final String anyVine = "vine";
   public static final String blazepowder = "dustBlaze";
   public static final String bonemeal = "bonemeal";
   public static final String cactusBlock = "blockCactus";
   public static final String clayball = "clayball";
   public static final String coalBlock = "blockCoal";
   public static final String cobblestone = "cobblestone";
   public static final String darkprismarineBlock = "blockPrismarineDark";
   public static final String diamond = "gemDiamond";
   public static final String diamondNugget = "nuggetDiamond";
   public static final String dirtDust = "dustDirt";
   public static final String emerald = "gemEmerald";
   public static final String emeraldNugget = "nuggetEmerald";
   public static final String emptyBottle = "itemEmptyBottle";
   public static final String emptyBowl = "bowlWood";
   public static final String emptyBucket = "itemEmptyBucket";
   public static final String emptyJar = "itemEmptyJar";
   public static final String enderpearl = "enderpearl";
   public static final String flint = "itemRawFlint";
   public static final String glowstoneDust = "dustGlowstone";
   public static final String goldIngot = "ingotGold";
   public static final String goldNugget = "nuggetGold";
   public static final String goldBlock = "blockGold";
   public static final String gravel = "gravel";
   public static final String gunpowder = "gunpowder";
   public static final String ironBlock = "blockIron";
   public static final String ironIngot = "ingotIron";
   public static final String ironNugget = "nuggetIron";
   public static final String lapis = "gemLapis";
   public static final String lapisNugget = "nuggetLapis";
   public static final String lavaBucket = "itemLavaBucket";
   public static final String lavaDust = "dustLava";
   public static final String lifeEssence = "itemLifeEssence";
   public static final String lifeRebirth = "itemRebirth";
   public static final String mendingTwine = "itemTwineMends";
   public static final String milkBucket = "itemMilkBucket";
   public static final String netherrack = "netherrack";
   public static final String obsidian = "obsidian";
   public static final String paper = "paper";
   public static final String pinkWool = "itemPinkWool";
   public static final String prismarineBlock = "blockPrismarine";
   public static final String potionBottle = "itemPotionBottle";
   public static final String quartz = "gemQuartz";
   public static final String quartzNugget = "nuggetQuartz";
   public static final String redstoneBlock = "blockRedstone";
   public static final String redstoneDust = "dustRedstone";
   public static final String rock = "rock";
   public static final String rottenflesh = "rottenflesh";
   public static final String sand = "sand";
   public static final String slimeball = "slimeball";
   public static final String slimeBucket = "itemSlimeBucket";
   public static final String slimeNugget = "nuggetSlime";
   public static final String smallrock = "small_rock";
   public static final String sponge = "sponge";
   public static final String spongeBlock = "blockSponge";
   public static final String stoneNugget = "nuggetStone";
   public static final String strongTie = "itemTieNormal";
   public static final String tipped_arrow = "tipped_arrow";
   public static final String treeLeaves = "treeLeaves";
   public static final String treeSapling = "treeSapling";
   public static final String waterBottle = "itemWaterBottle";
   public static final String waterBucket = "itemWaterBucket";
   public static final String waterTreatment = "itemWaterTreatment";
   public static final String wheatCrop = "cropWheat";
   public static final String woodChest = "chestWood";
   public static final String woodFence = "fenceWood";
   public static final String woodLog = "logWood";
   public static final String woodPlank = "plankWood";
   public static final String woodSlab = "slabWood";
   public static final String woodStick = "stickWood";
   public static final String woolBlock = "blockWool";
   private static boolean _DEFAULTS_INITED = false;

   public static final boolean matches(@Nullable ItemStack stack, @Nonnull String oreList, @Nullable Item preference, boolean strict) {
      if (!ItemStacks.isEmpty(stack)) {
         if (stack.getItem() == preference) {
            return true;
         }

         if (oreList.contains(":")) {
            preference = (Item)Item.REGISTRY.getObject(new ResourceLocation(oreList));
            if (preference != null && stack.getItem() == preference) {
               return true;
            }
         }

         for(ItemStack check : OreDictionary.getOres(oreList, false)) {
            if (OreDictionary.itemMatches(check, stack, strict)) {
               return true;
            }
         }
      }

      return false;
   }

   public static final boolean matches(ItemStack stack, @Nonnull String oreList) {
      return matches(stack, oreList, (Item)null, false);
   }

   public static final boolean matches(ItemStack stack, @Nonnull String oreList, @Nullable Item preference) {
      return matches(stack, oreList, preference, false);
   }

   public static final void initDefaults() {
      if (!_DEFAULTS_INITED) {
         int _ANY = 32767;
         OreDictionary.registerOre("dustBlaze", Items.BLAZE_POWDER);
         OreDictionary.registerOre("bonemeal", new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()));
         OreDictionary.registerOre("clayball", Items.CLAY_BALL);
         OreDictionary.registerOre("bowlWood", Items.BOWL);
         OreDictionary.registerOre("itemEmptyBottle", Items.GLASS_BOTTLE);
         OreDictionary.registerOre("itemEmptyBucket", Items.BUCKET);
         OreDictionary.registerOre("itemRawFlint", Items.FLINT);
         OreDictionary.registerOre("itemLavaBucket", Items.LAVA_BUCKET);
         OreDictionary.registerOre("dustLava", Items.BLAZE_POWDER);
         OreDictionary.registerOre("rottenflesh", Items.ROTTEN_FLESH);
         OreDictionary.registerOre("itemTieNormal", Items.STRING);
         OreDictionary.registerOre("itemWaterBucket", Items.WATER_BUCKET);
         OreDictionary.registerOre("blockAdhesive", Blocks.SLIME_BLOCK);
         OreDictionary.registerOre("blockCactus", new ItemStack(Blocks.CACTUS, 1, 32767));
         OreDictionary.registerOre("blockSponge", new ItemStack(Blocks.SPONGE, 1, 32767));
         OreDictionary.registerOre("sponge", new ItemStack(Blocks.SPONGE, 1, 32767));
         OreDictionary.registerOre("blockWool", new ItemStack(Blocks.WOOL, 1, 32767));
         OreDictionary.registerOre("itemTieStrong", Items.STRING);
         OreDictionary.registerOre("portionHideSmall", Items.RABBIT_HIDE);
         _DEFAULTS_INITED = true;
      }
   }

   private RID() {
   }

   public static final void init() {
   }
}
