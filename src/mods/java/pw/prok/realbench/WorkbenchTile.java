package pw.prok.realbench;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorkbenchTile extends TileEntity {
   protected NonNullList<ItemStack> mCraftMatrix = NonNullList.withSize(9, ItemStack.EMPTY);

   public WorkbenchTile() {
   }

   public List<ItemStack> getCraftMatrixItems() {
      Builder<ItemStack> builder = ImmutableList.builder();

      for(ItemStack stack : this.mCraftMatrix) {
         if (stack != null) {
            builder.add(stack);
         }
      }

      return builder.build();
   }

   private void writeSlots(NBTTagCompound nbt) {
      nbt.setInteger("Capacity", this.mCraftMatrix.size());

      for(int i = 0; i < 9; ++i) {
         if (this.mCraftMatrix.get(i) == ItemStack.EMPTY) {
            nbt.removeTag("Slot" + i);
         } else {
            NBTTagCompound slot = new NBTTagCompound();
            ((ItemStack)this.mCraftMatrix.get(i)).writeToNBT(slot);
            nbt.setTag("Slot" + i, slot);
         }
      }

   }

   private void readSlots(NBTTagCompound nbt) {
      int capacity = 9;
      if (nbt.hasKey("Capacity", 3)) {
         capacity = nbt.getInteger("Capacity");
      }

      this.ensureCraftMatrixCapacity(capacity);

      for(int i = 0; i < 9; ++i) {
         if (!nbt.hasKey("Slot" + i, 10)) {
            this.mCraftMatrix.set(i, ItemStack.EMPTY);
         } else {
            this.mCraftMatrix.set(i, new ItemStack(nbt.getCompoundTag("Slot" + i)));
         }
      }

   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.readSlots(nbt);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      super.writeToNBT(nbt);
      this.writeSlots(nbt);
      return nbt;
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound nbt = super.getUpdateTag();
      this.writeSlots(nbt);
      return nbt;
   }

   public void handleUpdateTag(NBTTagCompound tag) {
      super.handleUpdateTag(tag);
      this.readSlots(tag);
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.getPos(), 0, this.getUpdateTag());
   }

   @SideOnly(Side.CLIENT)
   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.handleUpdateTag(pkt.getNbtCompound());
   }

   public void ensureCraftMatrixCapacity(int capacity) {
      if (this.mCraftMatrix.size() != capacity) {
         this.mCraftMatrix = NonNullList.withSize(capacity, ItemStack.EMPTY);
      }

   }
}
