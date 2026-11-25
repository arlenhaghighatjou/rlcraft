package snownee.kiwi.tile;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

public abstract class TileBase extends TileEntity {
   public TileBase() {
   }

   public final SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, -1, this.writePacketData(new NBTTagCompound()));
   }

   public final void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
      this.readPacketData(packet.getNbtCompound());
   }

   @Nonnull
   public final NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public final void handleUpdateTag(NBTTagCompound tag) {
      this.readFromNBT(tag);
   }

   protected abstract void readPacketData(NBTTagCompound var1);

   @Nonnull
   protected abstract NBTTagCompound writePacketData(NBTTagCompound var1);

   protected void refresh() {
      if (this.hasWorld() && !this.world.isRemote) {
         IBlockState state = this.world.getBlockState(this.pos);
         this.world.markAndNotifyBlock(this.pos, (Chunk)null, state, state, 11);
      }

   }
}
