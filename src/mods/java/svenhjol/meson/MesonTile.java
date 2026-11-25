package svenhjol.meson;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import svenhjol.meson.iface.IMesonTile;

public abstract class MesonTile extends TileEntity implements IMesonTile {
   protected String name;

   public MesonTile() {
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
      return oldState.getBlock() != newState.getBlock();
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
      tag.setString("name", this.getName());
   }

   public void readTag(NBTTagCompound tag) {
      this.name = tag.getString("name");
   }

   public String getDefaultName() {
      return "";
   }

   public String getName() {
      return this.hasCustomName() ? this.name : this.getDefaultName();
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean hasCustomName() {
      return this.name != null && !this.name.isEmpty();
   }
}
