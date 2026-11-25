package com.alcatrazescapee.notreepunching.common.items;

import com.alcatrazescapee.alcatrazcore.item.tool.ItemToolCore;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemMattock extends ItemToolCore {
   public ItemMattock(ToolMaterial material) {
      super(material, Math.max(material.getAttackDamage() - 2.0F, 1.0F), -2.7F);
      this.addToolClass(ToolClass.AXE);
      this.addToolClass(ToolClass.SHOVEL);
      this.setHarvestLevel("mattock", this.harvestLevel);
      this.effectiveMaterials.add(Material.WOOD);
      this.effectiveMaterials.add(Material.PLANTS);
      this.effectiveMaterials.add(Material.VINE);
   }

   @Nonnull
   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
         return EnumActionResult.FAIL;
      } else {
         int hook = ForgeEventFactory.onHoeUse(itemstack, player, worldIn, pos);
         if (hook != 0) {
            return hook > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
         } else {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            if (facing != EnumFacing.DOWN && worldIn.isAirBlock(pos.up())) {
               if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
                  this.setBlock(itemstack, player, worldIn, pos, Blocks.FARMLAND.getDefaultState());
                  return EnumActionResult.SUCCESS;
               }

               if (block == Blocks.DIRT) {
                  switch((DirtType)iblockstate.getValue(BlockDirt.VARIANT)) {
                  case DIRT:
                     this.setBlock(itemstack, player, worldIn, pos, Blocks.FARMLAND.getDefaultState());
                     return EnumActionResult.SUCCESS;
                  case COARSE_DIRT:
                     this.setBlock(itemstack, player, worldIn, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT));
                     return EnumActionResult.SUCCESS;
                  }
               }
            }

            return EnumActionResult.PASS;
         }
      }
   }

   private void setBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state) {
      worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
      if (!worldIn.isRemote) {
         worldIn.setBlockState(pos, state, 11);
         stack.damageItem(1, player);
      }

   }
}
