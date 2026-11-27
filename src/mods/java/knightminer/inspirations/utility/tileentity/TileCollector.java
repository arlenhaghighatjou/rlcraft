package knightminer.inspirations.utility.tileentity;

import javax.annotation.Nonnull;
import knightminer.inspirations.utility.client.GuiCollector;
import knightminer.inspirations.utility.inventory.ContainerCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.tileentity.TileInventory;

public class TileCollector extends TileInventory implements IInventoryGui {
   public TileCollector() {
      super("gui.inspirations.collector.name", 9);
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
      return oldState.getBlock() != newSate.getBlock();
   }

   public void collect(EnumFacing facing) {
      BlockPos offset = this.pos.offset(facing);
      TileEntity te = this.world.getTileEntity(offset);
      if (te != null) {
         IItemHandler neighbor = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
         if (neighbor != null) {
            for(int i = 0; i < neighbor.getSlots(); ++i) {
               ItemStack simulated = neighbor.extractItem(i, 1, true);
               if (!simulated.isEmpty() && ItemHandlerHelper.insertItemStacked(this.itemHandler, simulated, false).isEmpty()) {
                  neighbor.extractItem(i, 1, false);
                  break;
               }
            }

            return;
         }
      }

      AxisAlignedBB aabb = new AxisAlignedBB((double)offset.getX(), (double)offset.getY(), (double)offset.getZ(), (double)(offset.getX() + 1), (double)(offset.getY() + 1), (double)(offset.getZ() + 1));
      boolean collected = false;

      for(EntityItem entity : this.world.getEntitiesWithinAABB(EntityItem.class, aabb)) {
         ItemStack insert = entity.getItem();
         ItemStack remainder = ItemHandlerHelper.insertItemStacked(this.itemHandler, insert, false);
         if (remainder.getCount() < insert.getCount()) {
            collected = true;
            if (remainder.isEmpty()) {
               entity.setDead();
            } else {
               entity.setItem(remainder);
            }
         }
      }

      this.world.playEvent(collected ? 1000 : 1001, this.pos, 0);
   }

   public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
      return slot < this.getSizeInventory();
   }

   public ContainerCollector createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
      return new ContainerCollector(inventoryplayer, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
      return new GuiCollector(this.createContainer(inventoryplayer, world, pos));
   }

   @Nonnull
   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = this.getTileData().copy();
      this.writeToNBT(tag);
      return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound tag = pkt.getNbtCompound();
      this.readFromNBT(tag);
   }
}
