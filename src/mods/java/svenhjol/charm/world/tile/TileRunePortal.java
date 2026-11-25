package svenhjol.charm.world.tile;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.math.BlockPos;
import svenhjol.meson.iface.IMesonTile;

public class TileRunePortal extends TileEntityEndPortal implements IMesonTile {
   public BlockPos portal;

   public TileRunePortal() {
   }

   public String getModId() {
      return "charm";
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      NBTTagCompound nbt = super.writeToNBT(tag);
      this.writeTag(tag);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      this.readTag(tag);
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = new NBTTagCompound();
      this.writeTag(tag);
      return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound tag = super.getUpdateTag();
      this.writeTag(tag);
      return tag;
   }

   public void handleUpdateTag(NBTTagCompound tag) {
      super.handleUpdateTag(tag);
      this.readTag(tag);
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      super.onDataPacket(net, pkt);
      this.readTag(pkt.getNbtCompound());
   }

   public void writeTag(NBTTagCompound tag) {
      if (this.portal != null) {
         tag.setLong("portal", this.portal.toLong());
      }

   }

   public void readTag(NBTTagCompound tag) {
      this.portal = BlockPos.fromLong(tag.getLong("portal"));
   }
}
