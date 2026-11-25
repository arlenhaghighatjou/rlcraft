package noppes.vc.containers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.vc.VCBlocks;
import noppes.vc.VCRecipes;
import noppes.vc.blocks.BlockCarpentryBench;

public class ContainerCarpentryBench extends Container {
   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 4, 4);
   public InventoryCraftResult craftResult = new InventoryCraftResult();
   private EntityPlayer player;
   private World world;
   private BlockPos pos;

   public ContainerCarpentryBench(InventoryPlayer par1InventoryPlayer, World par2World, BlockPos pos) {
      this.world = par2World;
      this.pos = pos;
      this.player = par1InventoryPlayer.player;
      this.addSlotToContainer(new SlotCrafting(par1InventoryPlayer.player, this.craftMatrix, this.craftResult, 0, 133, 41));

      for(int var6 = 0; var6 < 4; ++var6) {
         for(int var7 = 0; var7 < 4; ++var7) {
            this.addSlotToContainer(new Slot(this.craftMatrix, var7 + var6 * 4, 17 + var7 * 18, 14 + var6 * 18));
         }
      }

      for(int var61 = 0; var61 < 3; ++var61) {
         for(int var7 = 0; var7 < 9; ++var7) {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var61 * 9 + 9, 8 + var7 * 18, 98 + var61 * 18));
         }
      }

      for(int var71 = 0; var71 < 9; ++var71) {
         this.addSlotToContainer(new Slot(par1InventoryPlayer, var71, 8 + var71 * 18, 156));
      }

      this.onCraftMatrixChanged(this.craftMatrix);
   }

   public int getType() {
      IBlockState state = this.world.getBlockState(this.pos);
      return state != null && state.getBlock() == VCBlocks.carpentry_bench ? ((Integer)state.getValue(BlockCarpentryBench.TYPE)).intValue() : 0;
   }

   public void onCraftMatrixChanged(IInventory par1IInventory) {
      if (!this.world.isRemote) {
         EntityPlayerMP entityplayermp = (EntityPlayerMP)this.player;
         ItemStack itemstack = ItemStack.EMPTY;
         IRecipe irecipe = VCRecipes.match(this.craftMatrix, this.world);
         if (irecipe != null && (irecipe.isDynamic() || !this.world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(irecipe))) {
            this.craftResult.setRecipeUsed(irecipe);
            itemstack = irecipe.getCraftingResult(this.craftMatrix);
         }

         this.craftResult.setInventorySlotContents(0, itemstack);
         entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, itemstack));
      }

   }

   public void onContainerClosed(EntityPlayer par1EntityPlayer) {
      super.onContainerClosed(par1EntityPlayer);
      if (!this.world.isRemote) {
         for(int var2 = 0; var2 < 16; ++var2) {
            ItemStack var3 = this.craftMatrix.removeStackFromSlot(var2);
            if (!var3.isEmpty()) {
               par1EntityPlayer.dropItem(var3, false);
            }
         }
      }

   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      return this.world.getBlockState(this.pos).getBlock() != VCBlocks.carpentry_bench ? false : par1EntityPlayer.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1) {
      ItemStack var2 = ItemStack.EMPTY;
      Slot var3 = (Slot)this.inventorySlots.get(par1);
      if (var3 != null && var3.getHasStack()) {
         ItemStack var4 = var3.getStack();
         var2 = var4.copy();
         if (par1 == 0) {
            if (!this.mergeItemStack(var4, 17, 53, true)) {
               return ItemStack.EMPTY;
            }

            var3.onSlotChange(var4, var2);
         } else if (par1 >= 17 && par1 < 44) {
            if (!this.mergeItemStack(var4, 44, 53, false)) {
               return ItemStack.EMPTY;
            }
         } else if (par1 >= 44 && par1 < 53) {
            if (!this.mergeItemStack(var4, 17, 44, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(var4, 17, 53, false)) {
            return ItemStack.EMPTY;
         }

         if (var4.getCount() == 0) {
            var3.putStack(ItemStack.EMPTY);
         } else {
            var3.onSlotChanged();
         }

         if (var4.getCount() == var2.getCount()) {
            return ItemStack.EMPTY;
         }

         var3.onTake(par1EntityPlayer, var4);
      }

      return var2;
   }

   public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
      return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
   }
}
