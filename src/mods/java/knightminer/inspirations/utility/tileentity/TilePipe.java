package knightminer.inspirations.utility.tileentity;

import javax.annotation.Nonnull;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.utility.client.GuiPipe;
import knightminer.inspirations.utility.inventory.ContainerPipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.tileentity.TileInventory;

public class TilePipe extends TileInventory implements IInventoryGui, ITickable {
   private short cooldown = 0;
   private static final String TAG_COOLDOWN = "cooldown";

   public TilePipe() {
      super("gui.inspirations.pipe.name", 1);
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
      return oldState.getBlock() != newSate.getBlock();
   }

   public void update() {
      if (this.world != null && !this.world.isRemote) {
         EnumFacing facing = this.getFacing();
         if (Config.pipeUpwards || facing != EnumFacing.UP) {
            --this.cooldown;
            if (this.cooldown <= 0) {
               this.cooldown = 0;
               ItemStack stack = this.getStackInSlot(0);
               if (!stack.isEmpty()) {
                  TileEntity te = this.world.getTileEntity(this.pos.offset(facing));
                  if (te != null) {
                     IItemHandler neighbor = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
                     if (neighbor != null) {
                        ItemStack copy = stack.copy();
                        copy.setCount(1);
                        if (ItemHandlerHelper.insertItemStacked(neighbor, copy, false).isEmpty()) {
                           if (te instanceof TileEntityHopper) {
                              ((TileEntityHopper)te).setTransferCooldown(7);
                           }

                           stack.shrink(1);
                           if (stack.isEmpty()) {
                              this.setInventorySlotContents(0, ItemStack.EMPTY);
                           }

                           this.cooldown = 8;
                           this.markDirty();
                        }
                     }
                  }

               }
            }
         }
      }
   }

   public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
      super.setInventorySlotContents(slot, itemstack);
      this.cooldown = 7;
   }

   private EnumFacing getFacing() {
      return EnumFacing.getFront(this.getBlockMetadata() & 7);
   }

   public ContainerPipe createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
      return new ContainerPipe(inventoryplayer, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
      return new GuiPipe(this.createContainer(inventoryplayer, world, pos));
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

   public void readFromNBT(NBTTagCompound tags) {
      super.readFromNBT(tags);
      this.cooldown = tags.getShort("cooldown");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tags) {
      super.writeToNBT(tags);
      tags.setShort("cooldown", this.cooldown);
      return tags;
   }
}
