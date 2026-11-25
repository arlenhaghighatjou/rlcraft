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
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public interface ISubBlock {
   String getName();

   String getUnLocalizedName();

   default String getLocalizedName() {
      return I18n.translateToLocal(this.getUnLocalizedName());
   }

   ResourceLocation getTextureLocation();

   int getColor();

   float getHardness();

   int getResistance();

   int getHarvestLevel();

   @Nonnull
   String getHarvestTool();

   void getDrops(int var1, List<ItemStack> var2);

   void setRecipes(List<IRecipe> var1);

   String getOreDict();

   @Nullable
   CreativeTabs getCreativeTab();

   @Nullable
   IGeneratedModel getGeneratedModel();

   Material getMaterial();

   @Nonnull
   ItemStack getItemStack();

   @Nonnull
   IBlockState getBlockState();

   void setMeta(int var1);

   void setBlock(Block var1);

   boolean isSideSolid(EnumFacing var1);

   boolean isTopSolid();

   BlockFaceShape getBlockFaceShape();

   AxisAlignedBB getBoundingBox();

   boolean isFullCube();

   boolean isOpaqueCube();

   boolean isPassable();

   boolean isFullBlock();

   int getLightOpacity();

   boolean canSilkHarvest();

   void onNeighborChange(World var1, BlockPos var2, Block var3, BlockPos var4);

   boolean canPlaceBlockAt(World var1, @Nonnull BlockPos var2);

   boolean onBlockActivated(World var1, BlockPos var2, EntityPlayer var3);

   default BlockRenderLayer getRenderLayer() {
      return this.isOpaqueCube() ? BlockRenderLayer.SOLID : BlockRenderLayer.CUTOUT;
   }
}
