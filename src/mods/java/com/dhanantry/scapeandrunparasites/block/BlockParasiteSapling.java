package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTallFlower;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTree;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeThin;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BlockParasiteSapling extends BlockBush implements IGrowable, IMetaName {
   public static final PropertyEnum<BlockParasiteSapling.EnumType> VARIANT = PropertyEnum.create("variant", BlockParasiteSapling.EnumType.class);
   public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
   protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

   public BlockParasiteSapling(String name) {
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockParasiteSapling.EnumType.TREE).withProperty(STAGE, Integer.valueOf(0)));
      this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return SAPLING_AABB;
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!worldIn.isRemote) {
         super.updateTick(worldIn, pos, state, rand);
         if (!worldIn.isAreaLoaded(pos, 1)) {
            return;
         }

         if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
            this.grow(worldIn, pos, state, rand);
         }
      }

   }

   public void grow(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (((Integer)state.getValue(STAGE)).intValue() == 0) {
         worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
      } else {
         this.generateTree(worldIn, pos, state, rand);
      }

   }

   public void generateTree(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
         WorldGenerator worldgenerator = (WorldGenerator)(rand.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true));
         int i = 0;
         int j = 0;
         boolean flag = false;
         switch((BlockParasiteSapling.EnumType)state.getValue(VARIANT)) {
         case TREE:
            worldgenerator = new WorldGenParasiteTree(true);
            break;
         case TREETHIN:
            worldgenerator = new WorldGenParasiteTreeThin(true);
            break;
         case FLOWERTALL:
            worldgenerator = new WorldGenParasiteTallFlower(true);
         }

         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
         if (!worldgenerator.generate(worldIn, rand, pos.add(i, 0, j))) {
            worldIn.setBlockState(pos, state, 4);
         }

      }
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return this.checkBush(worldIn.getBlockState(pos.down()));
   }

   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      return this.checkBush(worldIn.getBlockState(pos.down()));
   }

   protected boolean checkBush(IBlockState state) {
      return state.isFullCube();
   }

   public boolean isTypeAt(World worldIn, BlockPos pos, BlockParasiteSapling.EnumType type) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      return iblockstate.getBlock() == this && iblockstate.getValue(VARIANT) == type;
   }

   public int damageDropped(IBlockState state) {
      return ((BlockParasiteSapling.EnumType)state.getValue(VARIANT)).ordinal();
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockParasiteSapling.EnumType blockplanks$enumtype : BlockParasiteSapling.EnumType.values()) {
         items.add(new ItemStack(this, 1, blockplanks$enumtype.ordinal()));
      }

   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      return (double)worldIn.rand.nextFloat() < 0.45D;
   }

   public void grow(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      this.grow(worldIn, pos, state, rand);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockParasiteSapling.EnumType.values()[meta & 7]).withProperty(STAGE, Integer.valueOf((meta & 8) >> 3));
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((BlockParasiteSapling.EnumType)state.getValue(VARIANT)).ordinal();
      i = i | ((Integer)state.getValue(STAGE)).intValue() << 3;
      return i;
   }

   public Enum[] getVariants() {
      return BlockParasiteSapling.EnumType.values();
   }

   public ItemBlock getItemBlock() {
      return new ItemBlockVariant(this);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT, STAGE});
   }

   public static enum EnumType implements IStringSerializable {
      TREE,
      TREETHIN,
      FLOWERTALL;

      private EnumType() {
      }

      public String getName() {
         return this.name().toLowerCase();
      }

      public String toString() {
         return this.getName();
      }
   }
}
