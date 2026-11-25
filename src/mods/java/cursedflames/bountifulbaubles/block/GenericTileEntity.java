package cursedflames.bountifulbaubles.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class GenericTileEntity extends TileEntity {
   public GenericTileEntity() {
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return !this.isInvalid() && playerIn.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public NBTTagCompound getSyncTag() {
      return this.getUpdateTag();
   }

   public void handleSyncTag(NBTTagCompound tag) {
      this.readFromNBT(tag);
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), this.getSyncTag());
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
      this.handleSyncTag(packet.getNbtCompound());
   }

   public abstract NBTTagCompound writeDataToNBT(NBTTagCompound var1);

   public abstract void readDataFromNBT(NBTTagCompound var1);

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      return this.writeDataToNBT(super.writeToNBT(tag));
   }

   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      this.readDataFromNBT(tag);
   }

   @Nullable
   public abstract NBTTagCompound getBlockBreakNBT();

   public abstract void loadBlockPlaceNBT(@Nonnull NBTTagCompound var1);

   public void notifyUpdate() {
   }
}
