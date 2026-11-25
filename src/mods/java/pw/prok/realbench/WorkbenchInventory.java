package pw.prok.realbench;

import javax.annotation.Nullable;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import pw.prok.realbench.asm.ASMHooks;

public class WorkbenchInventory extends InventoryCrafting {
   private ContainerWorkbench container;
   private WorkbenchTile mTile;

   public WorkbenchInventory(Container container, int width, int height) {
      super(container, width, height);
      this.initRealBench(width * height);
   }

   private void initRealBench(int capacity) {
      this.container = this.eventHandler instanceof ContainerWorkbench ? (ContainerWorkbench)this.eventHandler : null;
      if (this.eventHandler != null) {
         this.mTile = ASMHooks.getTile(this.container);
         if (this.mTile != null) {
            this.mTile.ensureCraftMatrixCapacity(capacity);
            this.stackList = this.mTile.mCraftMatrix;
         }

      }
   }

   public void markDirty() {
      if (this.mTile != null) {
         this.mTile.markDirty();
         if (!this.mTile.getWorld().isRemote) {
            WorldServer world = (WorldServer)this.mTile.getWorld();
            world.getPlayerChunkMap().markBlockForUpdate(this.mTile.getPos());
         }
      }
   }

   public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
      super.setInventorySlotContents(index, stack);
      this.markDirty();
   }

   @MethodsReturnNonnullByDefault
   public ItemStack decrStackSize(int index, int count) {
      ItemStack var3;
      try {
         var3 = super.decrStackSize(index, count);
      } finally {
         this.markDirty();
      }

      return var3;
   }
}
