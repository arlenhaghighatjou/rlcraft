package com.alcatrazescapee.notreepunching.util;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.client.ModSounds;
import com.alcatrazescapee.notreepunching.common.items.ModItems;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
public final class PlayerInteractionHandler {
   public PlayerInteractionHandler() {
   }

   public static boolean hasAction(World world, BlockPos pos, ItemStack stack, @Nullable EnumFacing face) {
      IBlockState state = world.getBlockState(pos);
      if (stack.getItem() == Items.FLINT) {
         return ModConfig.BALANCE.flintKnappingChance > 0.0D && state.getMaterial() == Material.ROCK && state.isNormalCube() && face == EnumFacing.UP;
      } else {
         if (WoodRecipeHandler.isAxe(stack)) {
            IBlockState stateDown = world.getBlockState(pos.down());
            if (face == EnumFacing.UP && stateDown.isOpaqueCube()) {
               return WoodRecipeHandler.isLog(world, pos, state) && !WoodRecipeHandler.isLog(world, pos.down(), stateDown) || WoodRecipeHandler.isPlank(world, pos, state);
            }
         }

         return false;
      }
   }

   public static boolean performAction(World world, BlockPos pos, EntityPlayer player, ItemStack stack, @Nullable EnumFacing face, EnumHand hand) {
      if (stack.getItem() == Items.FLINT) {
         return handleFlint(world, pos, player, stack, hand);
      } else {
         return WoodRecipeHandler.isAxe(stack) ? handleChopping(world, pos, player, stack) : false;
      }
   }

   private static boolean handleFlint(World world, BlockPos pos, EntityPlayer player, ItemStack stack, EnumHand hand) {
      if (!world.isRemote) {
         if ((double)Util.RNG.nextFloat() < ModConfig.BALANCE.flintKnappingChance) {
            if ((double)Util.RNG.nextFloat() < ModConfig.BALANCE.flintKnappingSuccessChance) {
               CoreHelpers.dropItemInWorldExact(world, (double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D, new ItemStack(ModItems.FLINT_SHARD, 2));
            }

            player.setHeldItem(hand, CoreHelpers.consumeItem(player, stack));
         }

         world.playSound((EntityPlayer)null, pos, ModSounds.KNAPPING, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

      return true;
   }

   private static boolean handleChopping(World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
      if (!world.isRemote) {
         if ((double)Util.RNG.nextFloat() < ModConfig.BALANCE.logChoppingChance) {
            int amount = 1 + ((double)Util.RNG.nextFloat() < 0.75D ? 1 : 0);
            if (!WoodRecipeHandler.isWeakAxe(stack)) {
               ++amount;
            }

            IBlockState state = world.getBlockState(pos);
            ItemStack result = WoodRecipeHandler.getPlankForLog(world, pos, state);
            if (result != null) {
               result.setCount(amount);
               CoreHelpers.dropItemInWorldExact(world, (double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D, result);
               stack.damageItem(1, player);
               world.setBlockToAir(pos);
            } else if (WoodRecipeHandler.isPlank(world, pos, state)) {
               result = new ItemStack(Items.STICK, 1 + ((double)Util.RNG.nextFloat() < 0.25D ? 1 : 0));
               CoreHelpers.dropItemInWorldExact(world, (double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D, result);
               stack.damageItem(1, player);
               world.setBlockToAir(pos);
            }

            world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.PLAYERS, 1.2F, 1.0F);
         } else {
            world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.PLAYERS, 0.5F, 1.0F);
         }
      }

      return true;
   }
}
