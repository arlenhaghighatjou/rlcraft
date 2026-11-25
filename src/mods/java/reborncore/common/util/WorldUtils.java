package reborncore.common.util;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldUtils {
   public WorldUtils() {
   }

   public static void updateBlock(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      world.notifyBlockUpdate(pos, state, state, 3);
   }

   public static boolean chunkExists(World world, int x, int z) {
      return world.isBlockLoaded(new BlockPos(x << 4, 64, z << 4));
   }

   public static void dropItem(ItemStack itemStack, World world, BlockPos pos) {
      Random rand = new Random();
      float dX = rand.nextFloat() * 0.8F + 0.1F;
      float dY = rand.nextFloat() * 0.8F + 0.1F;
      float dZ = rand.nextFloat() * 0.8F + 0.1F;
      EntityItem entityItem = new EntityItem(world, (double)((float)pos.getX() + dX), (double)((float)pos.getY() + dY), (double)((float)pos.getZ() + dZ), itemStack.copy());
      if (itemStack.hasTagCompound()) {
         entityItem.getItem().setTagCompound(itemStack.getTagCompound().copy());
      }

      float factor = 0.05F;
      entityItem.motionX = rand.nextGaussian() * (double)factor;
      entityItem.motionY = rand.nextGaussian() * (double)factor + 0.20000000298023224D;
      entityItem.motionZ = rand.nextGaussian() * (double)factor;
      if (!world.isRemote) {
         world.spawnEntity(entityItem);
      }

   }

   public static void dropItems(List<ItemStack> itemStackList, World world, BlockPos pos) {
      for(ItemStack itemStack : itemStackList) {
         dropItem(itemStack, world, pos);
         itemStack.setCount(0);
      }

   }
}
