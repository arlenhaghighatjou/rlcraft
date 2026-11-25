package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.BiomeDictionary.Type;
import rustic.common.items.ItemHerbEdible;

public class Herbs {
   public static BlockHerbBase ALOE_VERA;
   public static BlockHerbBase BLOOD_ORCHID;
   public static BlockHerbBase CHAMOMILE;
   public static BlockHerbBase CLOUDSBLUFF_CROP;
   public static BlockHerbBase COHOSH;
   public static BlockHerbBase CORE_ROOT_CROP;
   public static BlockHerbBase DEATHSTALK;
   public static BlockHerbBase GINSENG_CROP;
   public static BlockHerbBase HORSETAIL;
   public static BlockHerbBase MARSH_MALLOW_CROP;
   public static BlockHerbBase MOONCAP;
   public static BlockHerbBase WIND_THISTLE;
   public static ItemHerbEdible CLOUDSBLUFF;
   public static ItemHerbEdible CORE_ROOT;
   public static ItemHerbEdible GINSENG;
   public static ItemHerbEdible MARSH_MALLOW;

   public Herbs() {
   }

   public static void init() {
      ALOE_VERA = new BlockHerbBase("aloe_vera", false) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Desert;
         }

         public Item getHerb() {
            return Item.getItemFromBlock(this);
         }
      };
      BLOOD_ORCHID = new BlockHerbBase("blood_orchid", false) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Plains;
         }

         public Item getHerb() {
            return Item.getItemFromBlock(this);
         }
      };
      Blocks.FIRE.setFireInfo(BLOOD_ORCHID, 60, 100);
      CHAMOMILE = new BlockHerbBase("chamomile", false) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Plains;
         }

         public Item getHerb() {
            return Item.getItemFromBlock(this);
         }
      };
      Blocks.FIRE.setFireInfo(CHAMOMILE, 60, 100);
      CLOUDSBLUFF_CROP = new BlockHerbBase("cloudsbluff", true) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Plains;
         }

         public Item getHerb() {
            return Herbs.CLOUDSBLUFF;
         }
      };
      Blocks.FIRE.setFireInfo(CLOUDSBLUFF_CROP, 60, 100);
      COHOSH = new BlockHerbBase("cohosh", false) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Plains;
         }

         public Item getHerb() {
            return Item.getItemFromBlock(this);
         }
      };
      Blocks.FIRE.setFireInfo(COHOSH, 60, 100);
      CORE_ROOT_CROP = new BlockHerbBase("core_root", true) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Cave;
         }

         public Item getHerb() {
            return Herbs.CORE_ROOT;
         }
      };
      Blocks.FIRE.setFireInfo(CORE_ROOT_CROP, 60, 100);
      DEATHSTALK = new BlockHerbBase("deathstalk_mushroom", false) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Cave;
         }

         public Item getHerb() {
            return Item.getItemFromBlock(this);
         }
      };
      GINSENG_CROP = new BlockHerbBase("ginseng", true) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Plains;
         }

         public Item getHerb() {
            return Herbs.GINSENG;
         }
      };
      Blocks.FIRE.setFireInfo(GINSENG_CROP, 60, 100);
      HORSETAIL = new BlockHerbBase("horsetail", false) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Plains;
         }

         public Item getHerb() {
            return Item.getItemFromBlock(this);
         }
      };
      Blocks.FIRE.setFireInfo(HORSETAIL, 60, 100);
      MARSH_MALLOW_CROP = new BlockHerbBase("marsh_mallow", true) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Plains;
         }

         public Item getHerb() {
            return Herbs.MARSH_MALLOW;
         }
      };
      Blocks.FIRE.setFireInfo(MARSH_MALLOW_CROP, 60, 100);
      MOONCAP = new BlockHerbBase("mooncap_mushroom", false) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Cave;
         }

         public Item getHerb() {
            return Item.getItemFromBlock(this);
         }

         public int getLightValue(IBlockState state) {
            return 8;
         }
      };
      WIND_THISTLE = new BlockHerbBase("wind_thistle", false) {
         public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
            return EnumPlantType.Plains;
         }

         public Item getHerb() {
            return Item.getItemFromBlock(this);
         }
      };
      Blocks.FIRE.setFireInfo(WIND_THISTLE, 60, 100);
      CLOUDSBLUFF = new ItemHerbEdible(CLOUDSBLUFF_CROP, 2, 0.2F) {
         protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
            if (!worldIn.isRemote) {
               player.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 400));
            }

         }
      };
      CORE_ROOT = new ItemHerbEdible(CORE_ROOT_CROP, 2, 0.3F);
      GINSENG = new ItemHerbEdible(GINSENG_CROP, 2, 0.3F);
      MARSH_MALLOW = new ItemHerbEdible(MARSH_MALLOW_CROP, 3, 0.3F);
   }

   public static void initModels() {
      ALOE_VERA.initModel();
      BLOOD_ORCHID.initModel();
      CHAMOMILE.initModel();
      COHOSH.initModel();
      CORE_ROOT_CROP.initModel();
      DEATHSTALK.initModel();
      GINSENG_CROP.initModel();
      HORSETAIL.initModel();
      MARSH_MALLOW_CROP.initModel();
      MOONCAP.initModel();
      WIND_THISTLE.initModel();
      CLOUDSBLUFF.initModel();
      CORE_ROOT.initModel();
      GINSENG.initModel();
      MARSH_MALLOW.initModel();
   }

   public static BlockHerbBase getRandomHerbForBiome(Biome biome, EaglercraftRandom rand) {
      List<BlockHerbBase> herbs = new ArrayList();
      if (BiomeDictionary.hasType(biome, Type.JUNGLE)) {
         herbs.add(BLOOD_ORCHID);
         herbs.add(HORSETAIL);
         herbs.add(MARSH_MALLOW_CROP);
         herbs.add(MOONCAP);
      } else if (BiomeDictionary.hasType(biome, Type.SANDY)) {
         herbs.add(ALOE_VERA);
      } else if (BiomeDictionary.hasType(biome, Type.SAVANNA)) {
         herbs.add(ALOE_VERA);
      } else if (BiomeDictionary.hasType(biome, Type.MESA)) {
         herbs.add(ALOE_VERA);
      } else if (BiomeDictionary.hasType(biome, Type.MOUNTAIN) && !BiomeDictionary.hasType(biome, Type.SNOWY)) {
         herbs.add(WIND_THISTLE);
         herbs.add(CLOUDSBLUFF_CROP);
      } else if (BiomeDictionary.hasType(biome, Type.SWAMP) && !BiomeDictionary.hasType(biome, Type.SNOWY)) {
         herbs.add(CHAMOMILE);
         herbs.add(HORSETAIL);
         herbs.add(MARSH_MALLOW_CROP);
      } else if (BiomeDictionary.hasType(biome, Type.FOREST) && !BiomeDictionary.hasType(biome, Type.SNOWY)) {
         herbs.add(CHAMOMILE);
         herbs.add(COHOSH);
         herbs.add(GINSENG_CROP);
         herbs.add(HORSETAIL);
      } else {
         if (!BiomeDictionary.hasType(biome, Type.PLAINS) || BiomeDictionary.hasType(biome, Type.SNOWY)) {
            return null;
         }

         herbs.add(CHAMOMILE);
         herbs.add(GINSENG_CROP);
         herbs.add(HORSETAIL);
         herbs.add(WIND_THISTLE);
      }

      return (BlockHerbBase)herbs.get(rand.nextInt(herbs.size()));
   }

   public static BlockHerbBase getRandomCaveHerb(EaglercraftRandom rand) {
      BlockHerbBase[] herbs = new BlockHerbBase[]{CORE_ROOT_CROP, MOONCAP};
      return herbs[rand.nextInt(herbs.length)];
   }

   public static BlockHerbBase getRandomNetherHerb(EaglercraftRandom rand) {
      return DEATHSTALK;
   }
}
