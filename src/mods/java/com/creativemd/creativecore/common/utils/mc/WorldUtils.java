package com.creativemd.creativecore.common.utils.mc;

import com.creativemd.creativecore.common.world.CreativeWorld;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldUtils {
   public WorldUtils() {
   }

   public static boolean checkIfChunkExists(Chunk chunk) {
      if (chunk == null) {
         return false;
      } else {
         return chunk.getWorld().isRemote ? checkIfChunkExistsClient(chunk) : true;
      }
   }

   @SideOnly(Side.CLIENT)
   private static boolean checkIfChunkExistsClient(Chunk chunk) {
      return !(chunk instanceof EmptyChunk);
   }

   public static void dropItem(World world, List<ItemStack> stacks, BlockPos pos) {
      for(int i = 0; i < stacks.size(); ++i) {
         dropItem(world, (ItemStack)stacks.get(i), pos);
      }

   }

   public static void dropItem(World world, ItemStack stack, BlockPos pos) {
      if (stack != null) {
         if (world instanceof CreativeWorld) {
            pos = ((CreativeWorld)world).transformToRealWorld(pos);
            world = ((CreativeWorld)world).getRealWorld();
         }

         float f = 0.7F;
         double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
         double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
         double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
         EntityItem entityitem = new EntityItem(world, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
         entityitem.setPickupDelay(10);
         world.spawnEntity(entityitem);
      }
   }

   public static void dropItem(EntityPlayer player, ItemStack stack) {
      if (stack != null) {
         dropItem(player.world, stack, player.getPosition());
      }

   }

   public static void dropItem(EntityPlayer player, List<ItemStack> stacks) {
      for(int i = 0; i < stacks.size(); ++i) {
         dropItem(player, (ItemStack)stacks.get(i));
      }

   }
}
