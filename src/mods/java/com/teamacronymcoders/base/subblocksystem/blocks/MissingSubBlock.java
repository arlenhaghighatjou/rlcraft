package com.teamacronymcoders.base.subblocksystem.blocks;

import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MissingSubBlock implements ISubBlock {
   public MissingSubBlock() {
   }

   public String getName() {
      return "Missing";
   }

   public String getUnLocalizedName() {
      return "Missing";
   }

   public ResourceLocation getTextureLocation() {
      return new ResourceLocation("bedrock");
   }

   public int getColor() {
      return -1;
   }

   public float getHardness() {
      return 0.0F;
   }

   public int getResistance() {
      return 0;
   }

   public int getHarvestLevel() {
      return 0;
   }

   @Nonnull
   public String getHarvestTool() {
      return "";
   }

   public void getDrops(int fortune, List<ItemStack> itemStacks) {
   }

   public void setRecipes(List<IRecipe> recipes) {
   }

   public String getOreDict() {
      return "";
   }

   public CreativeTabs getCreativeTab() {
      return null;
   }

   @Nullable
   public IGeneratedModel getGeneratedModel() {
      return null;
   }

   public Material getMaterial() {
      return Material.IRON;
   }

   @Nonnull
   public ItemStack getItemStack() {
      return ItemStack.EMPTY;
   }

   @Nonnull
   public IBlockState getBlockState() {
      return Blocks.BEDROCK.getDefaultState();
   }

   public void setMeta(int x) {
   }

   public void setBlock(Block block) {
   }

   public boolean isSideSolid(EnumFacing side) {
      return true;
   }

   public boolean isTopSolid() {
      return true;
   }

   public BlockFaceShape getBlockFaceShape() {
      return BlockFaceShape.SOLID;
   }

   public AxisAlignedBB getBoundingBox() {
      return Block.FULL_BLOCK_AABB;
   }

   public boolean isFullCube() {
      return true;
   }

   public boolean isOpaqueCube() {
      return true;
   }

   public boolean isPassable() {
      return false;
   }

   public boolean isFullBlock() {
      return true;
   }

   public int getLightOpacity() {
      return 255;
   }

   public boolean canSilkHarvest() {
      return true;
   }

   public void onNeighborChange(World world, BlockPos pos, Block block, BlockPos fromPos) {
   }

   public boolean canPlaceBlockAt(World world, @Nonnull BlockPos pos) {
      return false;
   }

   public boolean onBlockActivated(World world, BlockPos pos, EntityPlayer player) {
      return false;
   }
}
