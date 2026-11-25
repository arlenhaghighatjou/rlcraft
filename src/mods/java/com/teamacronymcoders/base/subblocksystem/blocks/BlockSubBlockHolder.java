package com.teamacronymcoders.base.subblocksystem.blocks;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.blocks.BlockBaseNoModel;
import com.teamacronymcoders.base.blocks.IHasBlockColor;
import com.teamacronymcoders.base.blocks.IHasBlockStateMapper;
import com.teamacronymcoders.base.client.models.generator.IHasGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.items.IHasOreDict;
import com.teamacronymcoders.base.items.IHasRecipe;
import com.teamacronymcoders.base.subblocksystem.SubBlockSystem;
import com.teamacronymcoders.base.subblocksystem.items.ItemBlockSubBlockHolder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSubBlockHolder extends BlockBaseNoModel implements IHasBlockStateMapper, IHasBlockColor, IHasOreDict, IHasRecipe, IHasGeneratedModel {
   public static final PropertyInteger SUB_BLOCK_NUMBER = PropertyInteger.create("sub_block_number", 0, 15);
   private Map<Integer, ISubBlock> subBlocks;

   public BlockSubBlockHolder(int number, Map<Integer, ISubBlock> subBlocks) {
      super(Material.IRON, "sub_block_holder_" + number);
      this.setItemBlock(new ItemBlockSubBlockHolder(this));
      this.subBlocks = subBlocks;

      for(int x = 0; x < 16; ++x) {
         this.getSubBlocks().putIfAbsent(Integer.valueOf(x), SubBlockSystem.MISSING_SUB_BLOCK);
         this.getSubBlock(x).setBlock(this);
         this.getSubBlock(x).setMeta(x);
      }

   }

   @Nonnull
   public Material getMaterial(@Nonnull IBlockState state) {
      return this.getSubBlock(state).getMaterial();
   }

   public List<ItemStack> getAllSubItems(List<ItemStack> itemStacks) {
      for(int x = 0; x < 16; ++x) {
         if (this.getSubBlocks().get(Integer.valueOf(x)) != SubBlockSystem.MISSING_SUB_BLOCK) {
            itemStacks.add(new ItemStack(this.getItemBlock(), 1, x));
         }
      }

      return itemStacks;
   }

   public void getSubBlocks(@Nullable CreativeTabs creativeTab, @Nonnull NonNullList<ItemStack> list) {
      for(Entry<Integer, ISubBlock> subBlock : this.getSubBlocks().entrySet()) {
         if (((ISubBlock)subBlock.getValue()).getCreativeTab() == creativeTab || creativeTab == CreativeTabs.SEARCH) {
            list.add(new ItemStack(this, 1, ((Integer)subBlock.getKey()).intValue()));
         }
      }

   }

   public float getBlockHardness(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
      return this.getSubBlock(((Integer)blockState.getValue(SUB_BLOCK_NUMBER)).intValue()).getHardness();
   }

   public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Entity exploder, @Nonnull Explosion explosion) {
      return (float)this.getSubBlock(((Integer)world.getBlockState(pos).getValue(SUB_BLOCK_NUMBER)).intValue()).getResistance();
   }

   public String getHarvestTool(@Nonnull IBlockState state) {
      return this.getSubBlock(((Integer)state.getValue(SUB_BLOCK_NUMBER)).intValue()).getHarvestTool();
   }

   public int getHarvestLevel(@Nonnull IBlockState state) {
      return this.getSubBlock(((Integer)state.getValue(SUB_BLOCK_NUMBER)).intValue()).getHarvestLevel();
   }

   @ParametersAreNonnullByDefault
   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      this.getSubBlock(state).getDrops(fortune, drops);
   }

   public ResourceLocation getResourceLocation(IBlockState blockState) {
      return this.getSubBlock(blockState).getTextureLocation();
   }

   @Nonnull
   public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
      return this.getSubBlock(state).getRenderLayer() == layer;
   }

   public ISubBlock getSubBlock(IBlockState blockState) {
      return this.getSubBlock(((Integer)blockState.getValue(SUB_BLOCK_NUMBER)).intValue());
   }

   public ISubBlock getSubBlock(int meta) {
      return (ISubBlock)this.getSubBlocks().getOrDefault(Integer.valueOf(meta), SubBlockSystem.MISSING_SUB_BLOCK);
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SUB_BLOCK_NUMBER, Integer.valueOf(meta));
   }

   public int getMetaFromState(@Nonnull IBlockState state) {
      return ((Integer)state.getValue(SUB_BLOCK_NUMBER)).intValue();
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{SUB_BLOCK_NUMBER});
   }

   public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
      return this.getSubBlock(((Integer)state.getValue(SUB_BLOCK_NUMBER)).intValue()).getColor();
   }

   public int damageDropped(@Nonnull IBlockState state) {
      return ((Integer)state.getValue(SUB_BLOCK_NUMBER)).intValue();
   }

   public Map<Integer, ISubBlock> getSubBlocks() {
      if (this.subBlocks == null) {
         this.subBlocks = Maps.newHashMap();
      }

      return this.subBlocks;
   }

   @Nonnull
   public Map<ItemStack, String> getOreDictNames(@Nonnull Map<ItemStack, String> names) {
      this.getSubBlocks().forEach((key, value) -> {
         String oreDict = value.getOreDict();
         if (!Strings.isNullOrEmpty(oreDict)) {
            names.put(new ItemStack(this.getItemBlock(), 1, key.intValue()), oreDict);
         }

      });
      return names;
   }

   public List<IRecipe> getRecipes(List<IRecipe> recipes) {
      this.getSubBlocks().values().forEach((subBlock) -> {
         subBlock.setRecipes(recipes);
      });
      return recipes;
   }

   public List<IGeneratedModel> getGeneratedModels() {
      return (List)this.getSubBlocks().values().stream().map(ISubBlock::getGeneratedModel).filter(Objects::nonNull).collect(Collectors.toList());
   }

   public boolean isSideSolid(IBlockState blockState, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
      return this.getSubBlock(blockState).isSideSolid(side);
   }

   public boolean isTopSolid(IBlockState blockState) {
      return this.getSubBlock(blockState).isTopSolid();
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState blockState, BlockPos pos, EnumFacing face) {
      return this.getSubBlock(blockState).getBlockFaceShape();
   }

   public AxisAlignedBB getBoundingBox(IBlockState blockState, IBlockAccess source, BlockPos pos) {
      return this.getSubBlock(blockState).getBoundingBox();
   }

   public boolean isFullCube(IBlockState blockState) {
      return this.getSubBlock(blockState).isFullCube();
   }

   public boolean isOpaqueCube(IBlockState blockState) {
      return this.getSubBlock(blockState).isOpaqueCube();
   }

   public boolean isPassable(IBlockAccess world, BlockPos pos) {
      return this.getSubBlock(world.getBlockState(pos)).isPassable();
   }

   public boolean isFullBlock(IBlockState blockState) {
      return this.getSubBlock(blockState).isFullBlock();
   }

   public int getLightOpacity(IBlockState blockState) {
      return this.getSubBlock(blockState).getLightOpacity();
   }

   public boolean canSilkHarvest(World world, BlockPos pos, @Nonnull IBlockState blockState, EntityPlayer player) {
      return this.getSubBlock(blockState).canSilkHarvest();
   }

   public void neighborChanged(IBlockState blockState, World world, BlockPos pos, Block block, BlockPos fromPos) {
      this.getSubBlock(blockState).onNeighborChange(world, pos, block, fromPos);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      return this.getSubBlock(state).onBlockActivated(world, pos, player);
   }
}
