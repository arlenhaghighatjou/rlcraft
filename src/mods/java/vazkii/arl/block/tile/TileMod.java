package vazkii.arl.block.tile;

import javax.annotation.Nonnull;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.util.VanillaPacketDispatcher;

public abstract class TileMod extends TileEntity {
   public TileMod() {
   }

   public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
      return oldState.getBlock() != newState.getBlock();
   }

   @Nonnull
   public NBTTagCompound writeToNBT(NBTTagCompound par1nbtTagCompound) {
      NBTTagCompound nbt = super.writeToNBT(par1nbtTagCompound);
      this.writeSharedNBT(par1nbtTagCompound);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
      super.readFromNBT(par1nbtTagCompound);
      this.readSharedNBT(par1nbtTagCompound);
   }

   public void writeSharedNBT(NBTTagCompound cmp) {
   }

   public void readSharedNBT(NBTTagCompound cmp) {
   }

   public void sync() {
      VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
   }

   @Nonnull
   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound cmp = new NBTTagCompound();
      this.writeSharedNBT(cmp);
      return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), cmp);
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity packet) {
      super.onDataPacket(net, packet);
      this.readSharedNBT(packet.getNbtCompound());
   }
}
