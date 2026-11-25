package svenhjol.charm.crafting.tile;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import svenhjol.charm.crafting.block.BlockComposter;
import svenhjol.charm.crafting.feature.Composter;
import svenhjol.charm.crafting.message.MessageComposterAddLevel;
import svenhjol.meson.MesonTile;
import svenhjol.meson.handler.NetworkHandler;
import svenhjol.meson.helper.EntityHelper;
import svenhjol.meson.helper.ItemHelper;

public class TileComposter extends MesonTile {
   public ItemStackHandler input = new ItemStackHandler(1) {
      @Nonnull
      public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
         return TileComposter.this.world.getBlockState(TileComposter.this.pos).getBlock() instanceof BlockComposter && !stack.isEmpty() && TileComposter.this.addItem(stack, false) ? ItemStack.EMPTY : stack;
      }
   };

   public TileComposter() {
   }

   public String getModId() {
      return "charm";
   }

   public IBlockState getBlockState() {
      IBlockState state = this.world.getBlockState(this.pos);
      return state.getBlock() instanceof BlockComposter ? state : null;
   }

   public boolean addItem(ItemStack inputStack, boolean keepInput) {
      IBlockState state = this.world.getBlockState(this.pos);
      int level = ((Integer)state.getValue(BlockComposter.LEVEL)).intValue();
      int newLevel = level;
      if (level == 8) {
         String itemName = (String)Composter.outputs.get(this.world.rand.nextInt(Composter.outputs.size()));
         List<ItemStack> stacks = ItemHelper.getItemStacksFromItemString(itemName);
         if (stacks.isEmpty()) {
            return false;
         }

         if (!this.world.isRemote) {
            ItemStack stack = (ItemStack)stacks.get(this.world.rand.nextInt(stacks.size()));
            stack.setCount(this.world.rand.nextInt(Composter.maxOutput) + 1);
            boolean inserted = false;
            TileEntity n = this.world.getTileEntity(this.pos.down());
            if (n != null) {
               IItemHandler ni = (IItemHandler)n.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
               if (ni != null) {
                  ItemStack copy = stack.copy();
                  if (ItemHandlerHelper.insertItemStacked(ni, copy, false).isEmpty()) {
                     if (ni instanceof TileEntityHopper) {
                        ((TileEntityHopper)ni).setTransferCooldown(7);
                     }

                     inserted = true;
                     this.markDirty();
                  }
               }
            }

            if (!inserted) {
               EntityHelper.spawnEntityItem(this.world, this.pos.up(), stack);
            }
         }

         newLevel = 0;
      } else {
         if (!Composter.hasInput(inputStack)) {
            return false;
         }

         if (!this.world.isRemote) {
            if (!keepInput) {
               inputStack.shrink(1);
            }

            if (this.world.rand.nextFloat() < Composter.getItemChance(inputStack)) {
               newLevel = level + 1;
               NetworkHandler.INSTANCE.sendToAll(new MessageComposterAddLevel(this.pos, newLevel));
            }
         }
      }

      if (newLevel != level) {
         this.world.setBlockState(this.pos, state.withProperty(BlockComposter.LEVEL, Integer.valueOf(newLevel)), 2);
         this.world.updateComparatorOutputLevel(this.pos, (Block)null);
      }

      return true;
   }

   public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return this.getCapability(capability, facing) != null;
   }

   @Nullable
   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != EnumFacing.DOWN ? this.input : super.getCapability(capability, facing));
   }
}
