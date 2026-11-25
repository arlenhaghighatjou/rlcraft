package vazkii.quark.decoration.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockChest.Type;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.VanillaDoubleChestItemHandler;
import vazkii.quark.decoration.feature.VariedChests;

public class TileCustomChest extends TileEntityChest {
   public VariedChests.ChestType chestType = VariedChests.ChestType.NONE;

   public TileCustomChest() {
   }

   public TileCustomChest(Type typeIn) {
      super(typeIn);
   }

   @Nonnull
   public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
      super.writeToNBT(nbt);
      nbt.setString("type", this.chestType.name);
      return nbt;
   }

   @Nonnull
   public NBTTagCompound getUpdateTag() {
      NBTTagCompound nbt = super.getUpdateTag();
      nbt.setString("type", this.chestType.name);
      return nbt;
   }

   public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
      super.handleUpdateTag(tag);
      this.chestType = VariedChests.ChestType.getType(tag.getString("type"));
   }

   public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
      return oldState.getBlock() != newState.getBlock();
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setString("type", this.chestType.name);
      return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), nbt);
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.chestType = VariedChests.ChestType.getType(pkt.getNbtCompound().getString("type"));
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.chestType = VariedChests.ChestType.getType(nbt.getString("type"));
   }

   private void setNeighbor(TileEntityChest chestTe, EnumFacing side) {
      if (chestTe.isInvalid()) {
         this.adjacentChestChecked = false;
      } else if (this.adjacentChestChecked) {
         switch(side) {
         case NORTH:
            if (this.adjacentChestZNeg != chestTe) {
               this.adjacentChestChecked = false;
            }
            break;
         case SOUTH:
            if (this.adjacentChestZPos != chestTe) {
               this.adjacentChestChecked = false;
            }
            break;
         case EAST:
            if (this.adjacentChestXPos != chestTe) {
               this.adjacentChestChecked = false;
            }
            break;
         case WEST:
            if (this.adjacentChestXNeg != chestTe) {
               this.adjacentChestChecked = false;
            }
         }
      }

   }

   @Nullable
   protected TileEntityChest getAdjacentChest(@Nonnull EnumFacing side) {
      BlockPos blockpos = this.pos.offset(side);
      if (this.isChestAt(blockpos)) {
         TileEntity tileentity = this.getWorld().getTileEntity(blockpos);
         if (tileentity instanceof TileCustomChest) {
            TileCustomChest chest = (TileCustomChest)tileentity;
            chest.setNeighbor(this, side.getOpposite());
            return chest;
         }
      }

      return null;
   }

   private boolean isChestAt(BlockPos posIn) {
      Block block = this.getWorld().getBlockState(posIn).getBlock();
      TileEntity te = this.getWorld().getTileEntity(posIn);
      return block instanceof BlockChest && ((BlockChest)block).chestType == this.getChestType() && te instanceof TileCustomChest && ((TileCustomChest)te).chestType == this.chestType;
   }

   public void openInventory(EntityPlayer player) {
      super.openInventory(player);
      if (!player.isSpectator() && this.getChestType() == VariedChests.CUSTOM_TYPE_QUARK_TRAP) {
         this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
      }

   }

   public void closeInventory(EntityPlayer player) {
      super.closeInventory(player);
      if (!player.isSpectator() && this.getBlockType() instanceof BlockChest && this.getChestType() == VariedChests.CUSTOM_TYPE_QUARK_TRAP) {
         this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
      }

   }

   @Nonnull
   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB((double)(this.pos.getX() - 1), (double)this.pos.getY(), (double)(this.pos.getZ() - 1), (double)(this.pos.getX() + 2), (double)(this.pos.getY() + 2), (double)(this.pos.getZ() + 2));
   }

   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (this.doubleChestHandler == null || this.doubleChestHandler.needsRefresh()) {
            this.doubleChestHandler = getDoubleChestHandler(this);
         }

         if (this.doubleChestHandler != null && this.doubleChestHandler != VanillaDoubleChestItemHandler.NO_ADJACENT_CHESTS_INSTANCE) {
            return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.doubleChestHandler);
         }
      }

      return (T)super.getCapability(capability, facing);
   }

   @Nullable
   public static VanillaDoubleChestItemHandler getDoubleChestHandler(TileCustomChest chest) {
      World world = chest.getWorld();
      BlockPos pos = chest.getPos();
      if (!world.isBlockLoaded(pos)) {
         return null;
      } else {
         Block blockType = chest.getBlockType();
         EnumFacing[] horizontals = EnumFacing.HORIZONTALS;

         for(int i = horizontals.length - 1; i >= 0; --i) {
            EnumFacing enumfacing = horizontals[i];
            BlockPos blockpos = pos.offset(enumfacing);
            Block block = world.getBlockState(blockpos).getBlock();
            if (block == blockType) {
               TileEntity otherTE = world.getTileEntity(blockpos);
               if (otherTE instanceof TileCustomChest) {
                  TileCustomChest otherChest = (TileCustomChest)otherTE;
                  if (otherChest.chestType.equals(chest.chestType)) {
                     return new VanillaDoubleChestItemHandler(chest, otherChest, enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH);
                  }
               }
            }
         }

         return VanillaDoubleChestItemHandler.NO_ADJACENT_CHESTS_INSTANCE;
      }
   }
}
