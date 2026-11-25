package net.mcft.copy.backpacks.misc.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public final class WorldUtils {
   private WorldUtils() {
   }

   public static EntityItem spawnItem(World world, double x, double y, double z, ItemStack stack) {
      if (stack.isEmpty()) {
         return null;
      } else {
         EntityItem item = new EntityItem(world, x, y, z, stack);
         world.spawnEntity(item);
         return item;
      }
   }

   public static EntityItem spawnItemWithMotion(World world, double x, double y, double z, ItemStack stack) {
      EntityItem item = spawnItem(world, x, y, z, stack);
      if (item == null) {
         return null;
      } else {
         item.motionX = RandomUtils.getGaussian() * 0.05000000074505806D;
         item.motionY = RandomUtils.getGaussian() * 0.05000000074505806D + 0.20000000298023224D;
         item.motionZ = RandomUtils.getGaussian() * 0.05000000074505806D;
         return item;
      }
   }

   public static EntityItem dropStackFromBlock(World world, BlockPos pos, ItemStack stack) {
      float itemX = (float)pos.getX() + RandomUtils.getFloat(0.1F, 0.9F);
      float itemY = (float)pos.getY() + RandomUtils.getFloat(0.1F, 0.9F);
      float itemZ = (float)pos.getZ() + RandomUtils.getFloat(0.1F, 0.9F);
      return spawnItemWithMotion(world, (double)itemX, (double)itemY, (double)itemZ, stack);
   }

   public static EntityItem dropStackFromBlock(TileEntity entity, ItemStack stack) {
      return dropStackFromBlock(entity.getWorld(), entity.getPos(), stack);
   }

   public static void dropStacksFromBlock(World world, BlockPos pos, Iterable<ItemStack> stacks) {
      for(ItemStack stack : stacks) {
         dropStackFromBlock(world, pos, stack);
      }

   }

   public static void dropStacksFromBlock(World world, BlockPos pos, ItemStackHandler items) {
      for(int i = 0; i < items.getSlots(); ++i) {
         dropStackFromBlock(world, pos, items.getStackInSlot(i));
      }

   }

   public static void dropStacksFromBlock(TileEntity entity, Iterable<ItemStack> stacks) {
      for(ItemStack stack : stacks) {
         dropStackFromBlock(entity, stack);
      }

   }

   public static void dropStacksFromBlock(TileEntity entity, ItemStackHandler items) {
      for(int i = 0; i < items.getSlots(); ++i) {
         dropStackFromBlock(entity, items.getStackInSlot(i));
      }

   }

   public static EntityItem dropStackFromEntity(Entity entity, ItemStack stack, float speed) {
      EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
      if (player == null) {
         double y = entity.posY + (double)entity.getEyeHeight() - 0.3D;
         EntityItem item = spawnItem(entity.world, entity.posX, y, entity.posZ, stack);
         if (item == null) {
            return null;
         } else {
            item.setPickupDelay(40);
            float f1 = RandomUtils.getFloat(0.5F);
            float f2 = RandomUtils.getFloat(6.2831855F);
            item.motionX = -Math.sin((double)f2) * (double)f1;
            item.motionY = 0.2D;
            item.motionZ = Math.cos((double)f2) * (double)f1;
            return item;
         }
      } else {
         EntityItem item = player.dropItem(stack, true, false);
         if (item != null) {
            item.motionX *= (double)(speed / 4.0F);
            item.motionZ *= (double)(speed / 4.0F);
         }

         return item;
      }
   }

   public static void dropStacksFromEntity(Entity entity, Iterable<ItemStack> stacks, float speed) {
      for(ItemStack stack : stacks) {
         dropStackFromEntity(entity, stack, speed);
      }

   }

   public static void dropStacksFromEntity(Entity entity, ItemStackHandler items, float speed) {
      for(int i = 0; i < items.getSlots(); ++i) {
         dropStackFromEntity(entity, items.getStackInSlot(i), speed);
      }

   }
}
