package net.silentchaos512.lib.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySL extends TileEntity {
   public TileEntitySL() {
   }

   public final void sendUpdate() {
      if (this.world != null && !this.world.isRemote) {
         IBlockState state = this.world.getBlockState(this.pos);
         this.world.notifyBlockUpdate(this.pos, state, state, 3);
         this.markDirty();
      }

   }

   public void readFromNBT(NBTTagCompound tags) {
      super.readFromNBT(tags);
      this.readSyncVars(tags);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tags) {
      super.writeToNBT(tags);
      this.writeSyncVars(tags, SyncVariable.Type.WRITE);
      return tags;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tags = this.getUpdateTag();
      return new SPacketUpdateTileEntity(this.pos, 1, tags);
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound tags = super.getUpdateTag();
      this.writeSyncVars(tags, SyncVariable.Type.PACKET);
      return tags;
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity packet) {
      this.readSyncVars(packet.getNbtCompound());
   }

   protected void readSyncVars(NBTTagCompound tags) {
   }

   protected NBTTagCompound writeSyncVars(NBTTagCompound tags, SyncVariable.Type syncType) {
      return tags;
   }
}
