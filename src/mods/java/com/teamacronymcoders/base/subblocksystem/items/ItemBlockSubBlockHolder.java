package com.teamacronymcoders.base.subblocksystem.items;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.items.IHasItemColor;
import com.teamacronymcoders.base.items.IHasItemMeshDefinition;
import com.teamacronymcoders.base.items.itemblocks.ItemBlockGeneric;
import com.teamacronymcoders.base.subblocksystem.blocks.BlockSubBlockHolder;
import com.teamacronymcoders.base.subblocksystem.blocks.ISubBlock;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockSubBlockHolder extends ItemBlockGeneric<BlockSubBlockHolder> implements IHasItemColor, IHasItemMeshDefinition {
   public ItemBlockSubBlockHolder(BlockSubBlockHolder block) {
      super(block);
   }

   public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
      return tintIndex == 0 ? ((BlockSubBlockHolder)this.getActualBlock()).getSubBlock(stack.getMetadata()).getColor() : -1;
   }

   @Nonnull
   public String getItemStackDisplayName(@Nonnull ItemStack stack) {
      return ((BlockSubBlockHolder)this.getActualBlock()).getSubBlock(stack.getMetadata()).getLocalizedName();
   }

   @Nonnull
   public CreativeTabs[] getCreativeTabs() {
      List<CreativeTabs> creativeTabsList = Lists.newArrayList();
      ((BlockSubBlockHolder)this.getActualBlock()).getSubBlocks().values().forEach((subBlock) -> {
         if (subBlock.getCreativeTab() != null && !creativeTabsList.contains(subBlock.getCreativeTab())) {
            creativeTabsList.add(subBlock.getCreativeTab());
         }

      });
      return (CreativeTabs[])creativeTabsList.toArray(new CreativeTabs[0]);
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      IBlockState iblockstate = world.getBlockState(pos);
      Block block = iblockstate.getBlock();
      if (!block.isReplaceable(world, pos)) {
         pos = pos.offset(facing);
      }

      ItemStack itemstack = player.getHeldItem(hand);
      int i = this.getMetadata(itemstack.getMetadata());
      if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && ((BlockSubBlockHolder)this.getActualBlock()).getSubBlock(i).canPlaceBlockAt(world, pos) && world.mayPlace(this.block, pos, false, facing, (Entity)null)) {
         IBlockState placementState = this.block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, i, player, hand);
         if (this.placeBlockAt(itemstack, player, world, pos, facing, hitX, hitY, hitZ, placementState)) {
            placementState = world.getBlockState(pos);
            SoundType soundtype = placementState.getBlock().getSoundType(placementState, world, pos, player);
            world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            itemstack.shrink(1);
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }

   public ResourceLocation getResourceLocation(ItemStack itemStack) {
      return ((BlockSubBlockHolder)this.getActualBlock()).getSubBlock(itemStack.getMetadata()).getTextureLocation();
   }

   public List<ResourceLocation> getAllVariants() {
      return (List)((BlockSubBlockHolder)this.getActualBlock()).getSubBlocks().values().stream().map(ISubBlock::getTextureLocation).collect(Collectors.toList());
   }
}
