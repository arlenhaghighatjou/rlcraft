package com.alcatrazescapee.alcatrazcore.util;

import com.alcatrazescapee.alcatrazcore.util.collections.ImmutablePair;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CoreHelpers {
   public CoreHelpers() {
   }

   @Nullable
   public static <TE extends TileEntity> TE getTE(IBlockAccess world, BlockPos pos, Class<TE> clazz) {
      TileEntity te = world.getTileEntity(pos);
      return (TE)(clazz.isInstance(te) ? te : null);
   }

   public static BlockPos getTopSolidBlock(World world, BlockPos pos) {
      Chunk chunk = world.getChunkFromBlockCoords(pos);
      MutableBlockPos mPos = new MutableBlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ());

      while(mPos.getY() > 0) {
         mPos.move(EnumFacing.DOWN, 1);
         IBlockState state = chunk.getBlockState(mPos);
         if (state.getMaterial().blocksMovement() && !state.getBlock().isLeaves(state, world, mPos) && !state.getBlock().isFoliage(world, mPos) && !state.getMaterial().isLiquid()) {
            break;
         }
      }

      return mPos.toImmutable();
   }

   public static BlockPos getTopNonAirBlock(World world, BlockPos pos) {
      Chunk chunk = world.getChunkFromBlockCoords(pos);
      MutableBlockPos mPos = new MutableBlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ());

      while(mPos.getY() > 0) {
         mPos.move(EnumFacing.DOWN, 1);
         IBlockState state = chunk.getBlockState(mPos);
         if (state.getBlock() != Blocks.AIR) {
            break;
         }
      }

      return mPos.toImmutable();
   }

   public static void dropItemInWorld(World world, BlockPos pos, ItemStack stack) {
      InventoryHelper.spawnItemStack(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), stack);
   }

   public static void dropItemInWorldExact(World world, BlockPos pos, ItemStack stack) {
      world.spawnEntity(new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, stack));
   }

   public static void dropItemInWorldExact(World world, double x, double y, double z, ItemStack stack) {
      world.spawnEntity(new EntityItem(world, x, y, z, stack));
   }

   public static void giveItemToPlayer(World world, EntityPlayer player, ItemStack stack) {
      if (!player.addItemStackToInventory(stack)) {
         dropItemInWorldExact(world, player.getPosition(), stack);
      }

   }

   public static ItemStack consumeItem(ItemStack stack) {
      return consumeItem(stack, 1);
   }

   public static ItemStack consumeItem(ItemStack stack, int amount) {
      if (stack.getCount() > amount) {
         stack.shrink(amount);
         return stack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public static ItemStack consumeItem(EntityPlayer player, ItemStack stack) {
      return player.isCreative() ? stack : consumeItem(stack, 1);
   }

   public static ItemStack consumeItem(EntityPlayer player, ItemStack stack, int amount) {
      return player.isCreative() ? stack : consumeItem(stack, amount);
   }

   public static boolean doStacksMatch(ItemStack stack1, ItemStack stack2) {
      if (stack1.isEmpty()) {
         return stack2.isEmpty();
      } else if (stack2.isEmpty()) {
         return false;
      } else if (stack1.getItem() != stack2.getItem()) {
         return false;
      } else if (stack1.getMetadata() != 32767 && stack2.getMetadata() != 32767) {
         return stack1.getMetadata() == stack2.getMetadata();
      } else {
         return true;
      }
   }

   public static boolean doStacksMatchIgnoreWildcard(ItemStack stack1, ItemStack stack2) {
      return stack1.isEmpty() ? stack2.isEmpty() : stack1.isItemEqual(stack2);
   }

   public static boolean doStacksMatchUseNBT(ItemStack stack1, ItemStack stack2) {
      return doStacksMatch(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
   }

   public static boolean canMergeStacks(ItemStack stack1, ItemStack stack2) {
      return stack1.isEmpty() || stack2.isEmpty() || doStacksMatchIgnoreWildcard(stack1, stack2);
   }

   public static boolean canMergeStacksUseNBT(ItemStack stack1, ItemStack stack2) {
      return stack1.isEmpty() || stack2.isEmpty() || doStacksMatchIgnoreWildcard(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
   }

   public static ItemStack mergeStacks(ItemStack stack1, ItemStack stack2) {
      if (stack1.isEmpty()) {
         return stack2;
      } else if (stack2.isEmpty()) {
         return stack1;
      } else {
         int amount = Math.min(stack1.getMaxStackSize(), stack1.getCount() + stack2.getCount());
         stack1.setCount(amount);
         return stack1;
      }
   }

   public static ImmutablePair<ItemStack, ItemStack> mergeStacksWithResult(ItemStack stack1, ItemStack stack2) {
      if (stack1.isEmpty()) {
         return ImmutablePair.<ItemStack, ItemStack>of(stack2, ItemStack.EMPTY);
      } else if (stack2.isEmpty()) {
         return ImmutablePair.<ItemStack, ItemStack>of(stack1, ItemStack.EMPTY);
      } else {
         int amount = stack1.getCount() + stack2.getCount();
         int maxAmount = stack1.getMaxStackSize();
         stack1.setCount(Math.min(maxAmount, amount));
         if (amount <= maxAmount) {
            return ImmutablePair.<ItemStack, ItemStack>of(stack1, ItemStack.EMPTY);
         } else {
            stack2.setCount(amount - maxAmount);
            return ImmutablePair.<ItemStack, ItemStack>of(stack1, stack2);
         }
      }
   }

   public static boolean doesStackMatchOre(ItemStack stack, String name) {
      if (stack.isEmpty()) {
         return false;
      } else {
         int[] ids = OreDictionary.getOreIDs(stack);

         for(int id : ids) {
            String oreName = OreDictionary.getOreName(id);
            if (name.equals(oreName)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesStackMatchOre(ItemStack stack, int id) {
      if (stack.isEmpty()) {
         return false;
      } else {
         int[] ids = OreDictionary.getOreIDs(stack);

         for(int i : ids) {
            if (id == i) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesStackMatchOrePrefix(ItemStack stack, String prefix) {
      if (stack.isEmpty()) {
         return false;
      } else {
         int[] ids = OreDictionary.getOreIDs(stack);

         for(int id : ids) {
            String oreName = OreDictionary.getOreName(id);
            if (oreName.length() >= prefix.length() && oreName.substring(0, prefix.length()).equals(prefix)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesOreHaveStack(String ore) {
      if (!OreDictionary.doesOreNameExist(ore)) {
         return false;
      } else {
         NonNullList<ItemStack> stacks = OreDictionary.getOres(ore, false);
         return !stacks.isEmpty();
      }
   }

   public static ItemStack getStackForOre(String ore) {
      if (OreDictionary.doesOreNameExist(ore)) {
         NonNullList<ItemStack> stacks = OreDictionary.getOres(ore, false);
         if (!stacks.isEmpty()) {
            return (ItemStack)stacks.get(0);
         }
      }

      return ItemStack.EMPTY;
   }

   public static ItemStack getStackForOre(String ore, String preferredModID) {
      if (OreDictionary.doesOreNameExist(ore)) {
         NonNullList<ItemStack> stacks = OreDictionary.getOres(ore, false);
         if (!stacks.isEmpty()) {
            if (stacks.size() == 1) {
               return (ItemStack)stacks.get(0);
            }

            return (ItemStack)stacks.stream().filter((x) -> {
               return x.getItem().getRegistryName().getResourceDomain().equals(preferredModID);
            }).findFirst().orElse(stacks.get(0));
         }
      }

      return ItemStack.EMPTY;
   }

   public static String getOreName(ItemStack stack) {
      int id = getOreID(stack);
      return id >= 0 ? OreDictionary.getOreName(id) : "";
   }

   public static int getOreID(ItemStack stack) {
      int[] ids = OreDictionary.getOreIDs(stack);
      return ids.length >= 1 ? ids[0] : -1;
   }

   public static ItemStack getStackByRegistryName(String name) {
      return getStackByRegistryName(name, 1, 0);
   }

   public static ItemStack getStackByRegistryName(String name, int meta) {
      return getStackByRegistryName(name, 1, meta);
   }

   public static ItemStack getStackByRegistryName(String name, int amount, int meta) {
      Item item = Item.getByNameOrId(name);
      return item == null ? ItemStack.EMPTY : new ItemStack(item, amount, meta);
   }

   public static boolean doStacksMatch(@Nullable FluidStack stack1, @Nullable FluidStack stack2) {
      if (stack1 == null) {
         return stack2 == null;
      } else if (stack2 == null) {
         return false;
      } else {
         return stack1.getFluid() == stack2.getFluid();
      }
   }

   public static boolean doStacksMatchUseNBT(@Nullable FluidStack stack1, @Nullable FluidStack stack2) {
      return stack1 != null ? stack1.isFluidEqual(stack2) : stack2 == null;
   }

   @Nonnull
   public static <T> T getNull() {
      return (T)null;
   }
}
