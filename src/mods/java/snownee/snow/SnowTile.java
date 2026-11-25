package snownee.snow;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SnowTile extends TileEntity {
   private IBlockState state = Blocks.AIR.getDefaultState();

   public SnowTile() {
   }

   public IBlockState getState() {
      return this.state;
   }

   public void setState(IBlockState state) {
      if (this.state != state) {
         this.state = state;
         if (this.world != null) {
            this.markDirty();
            IBlockState blockState = this.world.getBlockState(this.pos);
            this.world.markAndNotifyBlock(this.pos, (Chunk)null, blockState, blockState, 7);
         }
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      Block block = null;
      if (compound.hasKey("blockId", 8)) {
         block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compound.getString("blockId")));
         if (block == null) {
            block = Blocks.AIR;
         }

         int meta = compound.getInteger("blockMeta");
         this.state = block.getStateFromMeta(meta);
      } else {
         this.state = NBTUtil.readBlockState(compound.getCompoundTag("BlockState"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      NBTTagCompound tag = NBTUtil.writeBlockState(new NBTTagCompound(), this.state);
      compound.setTag("BlockState", tag);
      return compound;
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
      return oldState.getBlock() != newState.getBlock();
   }

   public final SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, -1, this.writeToNBT(new NBTTagCompound()));
   }

   public final void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
      this.readFromNBT(packet.getNbtCompound());
   }

   @Nonnull
   public final NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public final void handleUpdateTag(NBTTagCompound tag) {
      this.readFromNBT(tag);
   }
}
