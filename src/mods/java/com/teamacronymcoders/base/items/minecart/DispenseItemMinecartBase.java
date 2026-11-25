package com.teamacronymcoders.base.items.minecart;

import com.teamacronymcoders.base.entities.EntityMinecartBase;
import com.teamacronymcoders.base.util.ItemStackUtils;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DispenseItemMinecartBase extends BehaviorDefaultDispenseItem {
   public DispenseItemMinecartBase() {
   }

   @Nonnull
   public ItemStack dispenseStack(IBlockSource dispenser, ItemStack cartItemStack) {
      World world = dispenser.getWorld();
      EnumActionResult cartSpawned = EnumActionResult.PASS;
      if (ItemStackUtils.isItemInstanceOf(cartItemStack, ItemMinecartBase.class)) {
         ItemMinecartBase itemMinecartBase = (ItemMinecartBase)cartItemStack.getItem();
         EntityMinecartBase entityMinecartBase = itemMinecartBase.getEntityFromItem(world, cartItemStack);
         EnumFacing enumfacing = (EnumFacing)dispenser.getBlockState().getValue(BlockDispenser.FACING);
         BlockPos blockpos = dispenser.getBlockPos().offset(enumfacing);
         cartSpawned = itemMinecartBase.placeCart(cartItemStack, world, blockpos, entityMinecartBase);
      }

      if (cartSpawned != EnumActionResult.SUCCESS) {
         cartItemStack = super.dispenseStack(dispenser, cartItemStack);
      }

      return cartItemStack;
   }
}
