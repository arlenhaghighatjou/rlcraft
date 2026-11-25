package net.blay09.mods.waystones.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.blay09.mods.waystones.worldgen.NameGenerator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileWaystone extends TileEntity {
   private boolean isDummy;
   private String waystoneName = "";
   private EaglercraftUUID owner;
   private boolean isGlobal;
   private boolean wasGenerated = true;
   private boolean isMossy;

   public TileWaystone() {
   }

   public TileWaystone(boolean isDummy) {
      this.isDummy = isDummy;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      tagCompound.setBoolean("IsDummy", this.isDummy);
      if (!this.isDummy) {
         if (!this.waystoneName.equals("%RANDOM%")) {
            tagCompound.setString("WaystoneName", this.waystoneName);
            tagCompound.setBoolean("WasGenerated", this.wasGenerated);
         } else {
            tagCompound.setBoolean("WasGenerated", true);
         }

         if (this.owner != null) {
            tagCompound.setTag("Owner", NBTUtil.createUUIDTag(this.owner));
         }

         tagCompound.setBoolean("IsGlobal", this.isGlobal);
         tagCompound.setBoolean("IsMossy", this.isMossy);
      }

      return tagCompound;
   }

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      this.isDummy = tagCompound.getBoolean("IsDummy");
      if (!this.isDummy) {
         this.waystoneName = tagCompound.getString("WaystoneName");
         this.wasGenerated = tagCompound.getBoolean("WasGenerated");
         if (tagCompound.hasKey("Owner")) {
            this.owner = NBTUtil.getUUIDFromTag(tagCompound.getCompoundTag("Owner"));
         }

         this.isGlobal = tagCompound.getBoolean("IsGlobal");
         this.isMossy = tagCompound.getBoolean("IsMossy");
      }

   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity pkt) {
      super.onDataPacket(net, pkt);
      this.generateNameIfNecessary();
      this.readFromNBT(pkt.getNbtCompound());
   }

   public NBTTagCompound getUpdateTag() {
      this.generateNameIfNecessary();
      return this.writeToNBT(new NBTTagCompound());
   }

   private void generateNameIfNecessary() {
      if (this.waystoneName.isEmpty()) {
         this.waystoneName = NameGenerator.get(this.world).getName(this.pos, this.world.provider.getDimension(), this.world.getBiome(this.pos), this.world.rand);
      }

   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   public String getWaystoneName() {
      return this.waystoneName;
   }

   public boolean isOwner(EntityPlayer player) {
      return this.owner == null || player.getGameProfile().getId().equals(this.owner) || player.capabilities.isCreativeMode;
   }

   public boolean isMossy() {
      return this.isMossy;
   }

   public void setMossy(boolean mossy) {
      this.isMossy = mossy;
   }

   public boolean wasGenerated() {
      return this.wasGenerated;
   }

   public void setWasGenerated(boolean wasGenerated) {
      this.wasGenerated = wasGenerated;
   }

   public void setWaystoneName(String waystoneName) {
      this.waystoneName = waystoneName;
      IBlockState state = this.world.getBlockState(this.pos);
      this.world.markAndNotifyBlock(this.pos, this.world.getChunkFromBlockCoords(this.pos), state, state, 3);
      this.markDirty();
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
      return oldState.getBlock() != newSate.getBlock();
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 2), (double)(this.pos.getZ() + 1));
   }

   public void setOwner(EntityPlayer owner) {
      this.owner = owner.getGameProfile().getId();
      this.markDirty();
   }

   public boolean isGlobal() {
      return this.isGlobal;
   }

   public boolean isDummy() {
      return this.isDummy;
   }

   public void setGlobal(boolean isGlobal) {
      this.isGlobal = isGlobal;
      this.markDirty();
   }

   public TileWaystone getParent() {
      if (this.isDummy) {
         TileEntity tileBelow = this.world.getTileEntity(this.pos.down());
         if (tileBelow instanceof TileWaystone) {
            return (TileWaystone)tileBelow;
         }
      }

      return this;
   }
}
