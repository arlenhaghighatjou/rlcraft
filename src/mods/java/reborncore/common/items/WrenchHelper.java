package reborncore.common.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.api.IToolDrop;
import reborncore.api.ToolManager;
import reborncore.api.tile.IWrenchable;
import reborncore.common.misc.ModSounds;
import reborncore.common.util.InventoryHelper;

public class WrenchHelper {
   public WrenchHelper() {
   }

   public static boolean handleWrench(ItemStack stack, World world, BlockPos pos, EntityPlayer playerIn, EnumFacing side) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity == null) {
         return false;
      } else if (ToolManager.INSTANCE.handleTool(stack, pos, world, playerIn, side, true)) {
         if (playerIn.isSneaking()) {
            if (tileEntity instanceof IToolDrop) {
               ItemStack drop = ((IToolDrop)tileEntity).getToolDrop(playerIn);
               if (drop == null) {
                  return false;
               }

               if (!world.isRemote) {
                  InventoryHelper.dropInventoryItems(world, pos);
                  if (!drop.isEmpty()) {
                     net.minecraft.inventory.InventoryHelper.spawnItemStack(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), drop);
                  }

                  world.removeTileEntity(pos);
                  world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
               }

               world.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, ModSounds.BLOCK_DISMANTLE, SoundCategory.BLOCKS, 0.6F, 1.0F);
            }
         } else {
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof IWrenchable) {
               ((IWrenchable)block).setFacing(world, pos, side, playerIn);
            } else {
               block.rotateBlock(world, pos, side);
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
