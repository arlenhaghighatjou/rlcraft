package vazkii.quark.automation.tile;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import vazkii.arl.block.tile.TileSimpleInventory;
import vazkii.quark.automation.block.BlockChute;

public class TileChute extends TileSimpleInventory {
   public TileChute() {
   }

   public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
      if (!itemstack.isEmpty()) {
         EntityItem entity = new EntityItem(this.world, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() - 0.5D, (double)this.pos.getZ() + 0.5D, itemstack);
         entity.motionX = entity.motionY = entity.motionZ = 0.0D;
         this.world.spawnEntity(entity);
      }

   }

   public int getSizeInventory() {
      return 1;
   }

   protected boolean needsToSyncInventory() {
      return false;
   }

   public boolean isAutomationEnabled() {
      if (!((Boolean)this.world.getBlockState(this.pos).getValue(BlockChute.ENABLED)).booleanValue()) {
         return false;
      } else {
         BlockPos below = this.pos.down();
         IBlockState state = this.world.getBlockState(below);
         Block block = state.getBlock();
         return block.isAir(state, this.world, below) || state.getCollisionBoundingBox(this.world, below) == null;
      }
   }
}
