package net.ilexiconn.llibrary.server.entity.block;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.network.BlockEntityMessage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public abstract class BlockEntity extends TileEntity implements ITickable {
   private NBTTagCompound lastCompound;
   private int trackingUpdateTimer = 0;

   public BlockEntity() {
   }

   public final void update() {
      int trackingUpdateFrequency = this.getTrackingUpdateTime();
      if (this.trackingUpdateTimer < trackingUpdateFrequency) {
         ++this.trackingUpdateTimer;
      }

      if (this.trackingUpdateTimer >= trackingUpdateFrequency) {
         this.trackingUpdateTimer = 0;
         NBTTagCompound compound = new NBTTagCompound();
         this.saveTrackingSensitiveData(compound);
         if (!compound.equals(this.lastCompound)) {
            if (!this.world.isRemote) {
               this.onSync();
               TargetPoint point = new TargetPoint(this.world.provider.getDimension(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 0.0D);
               LLibrary.NETWORK_WRAPPER.sendToAllTracking(new BlockEntityMessage(this), point);
            }

            this.lastCompound = compound;
         }
      }

      this.onUpdate();
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, super.getUpdateTag());
   }

   public void onDataPacket(NetworkManager networkManager, SPacketUpdateTileEntity packet) {
      this.readFromNBT(packet.getNbtCompound());
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound compound = new NBTTagCompound();
      this.saveNBTData(compound);
      return compound;
   }

   public final void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.loadNBTData(compound);
   }

   public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      this.saveNBTData(compound);
      return compound;
   }

   public void saveTrackingSensitiveData(NBTTagCompound compound) {
      this.saveNBTData(compound);
   }

   public void loadTrackingSensitiveData(NBTTagCompound compound) {
      this.loadNBTData(compound);
   }

   public abstract void saveNBTData(NBTTagCompound var1);

   public abstract void loadNBTData(NBTTagCompound var1);

   public void onUpdate() {
   }

   public void onSync() {
   }

   public int getTrackingUpdateTime() {
      return 0;
   }
}
