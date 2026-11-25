package rustic.common.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.items.CapabilityItemHandler;
import rustic.common.util.ItemStackHandlerRustic;
import vazkii.quark.api.IDropoffManager;

@Interface(
   modid = "quark",
   iface = "vazkii.quark.api.IDropoffManager",
   striprefs = true
)
public class TileEntityVase extends TileEntityLockableLoot implements IDropoffManager {
   private ItemStackHandlerRustic itemStackHandler = new ItemStackHandlerRustic(27) {
      protected void onContentsChanged(int slot) {
         TileEntityVase.this.markDirty();
      }
   };

   public TileEntityVase() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (!this.checkLootAndRead(compound) && compound.hasKey("items")) {
         this.itemStackHandler.deserializeNBT((NBTTagCompound)compound.getTag("items"));
      }

      if (compound.hasKey("CustomName", 8)) {
         this.setCustomName(compound.getString("CustomName"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      if (!this.checkLootAndWrite(compound)) {
         compound.setTag("items", this.itemStackHandler.serializeNBT());
      }

      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }

      return compound;
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return !this.isInvalid() && playerIn.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (!this.getWorld().isRemote) {
            this.fillWithLoot((EntityPlayer)null);
         }

         return (T)this.itemStackHandler;
      } else {
         return (T)super.getCapability(capability, facing);
      }
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      this.invalidate();
      if (this.itemStackHandler != null && !world.isRemote) {
         this.fillWithLoot((EntityPlayer)null);

         for(int i = 0; i < this.itemStackHandler.getSlots(); ++i) {
            if (this.itemStackHandler.getStackInSlot(i) != null) {
               Block.spawnAsEntity(world, pos, this.itemStackHandler.getStackInSlot(i));
            }
         }
      }

      world.setTileEntity(pos, (TileEntity)null);
   }

   public int getSizeInventory() {
      return this.itemStackHandler.getSlots();
   }

   public boolean isEmpty() {
      for(int i = 0; i < this.itemStackHandler.getSlots(); ++i) {
         if (this.itemStackHandler.getStackInSlot(i) != ItemStack.EMPTY) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int index) {
      this.fillWithLoot((EntityPlayer)null);
      return this.itemStackHandler.getStackInSlot(index);
   }

   public ItemStack decrStackSize(int index, int count) {
      this.fillWithLoot((EntityPlayer)null);
      return this.itemStackHandler.extractItem(index, count, false);
   }

   public ItemStack removeStackFromSlot(int index) {
      this.fillWithLoot((EntityPlayer)null);
      ItemStack stack = this.itemStackHandler.getStackInSlot(index);
      this.itemStackHandler.setStackInSlot(index, ItemStack.EMPTY);
      return stack;
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.fillWithLoot((EntityPlayer)null);
      this.itemStackHandler.setStackInSlot(index, stack);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      return true;
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.fillWithLoot((EntityPlayer)null);

      for(int i = 0; i < this.itemStackHandler.getSlots(); ++i) {
         this.itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
      }

   }

   public String getName() {
      return this.hasCustomName() ? this.customName : "container.rustic.vase";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      if (!this.world.isRemote) {
         this.fillWithLoot(playerIn);
      }

      return new ContainerVase(playerInventory, this);
   }

   public String getGuiID() {
      return "rustic:vase";
   }

   protected NonNullList<ItemStack> getItems() {
      return this.itemStackHandler.getStacks();
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.getPos(), 0, this.getUpdateTag());
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }

   public void setLootTable(ResourceLocation file, long seed) {
      super.setLootTable(file, seed);
      this.markDirty();
   }

   public boolean acceptsDropoff(EntityPlayer entityPlayer) {
      return this.canInteractWith(entityPlayer);
   }
}
