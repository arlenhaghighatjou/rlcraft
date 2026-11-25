package net.mcft.copy.backpacks.block.entity;

import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.api.IBackpackData;
import net.mcft.copy.backpacks.config.ModConfig;
import net.mcft.copy.backpacks.misc.BackpackDataItems;
import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityBackpack extends TileEntity implements ITickable, IBackpack {
   private static final int DESPAWN_DISTANCE = 20;
   private static final int DESPAWN_TIME = 6000;
   public static final String TAG_AGE = "age";
   public static final String TAG_STACK = "stack";
   public static final String TAG_DATA = "data";
   public static final String TAG_FACING = "facing";
   public static final String TAG_DESPAWN_TIMER = "despawnTimer";
   private int _age = 0;
   private ItemStack _stack = ItemStack.EMPTY;
   private IBackpackData _data = null;
   private int _playersUsing = 0;
   private int _lidTicks = 0;
   private int _prevLidTicks = 0;
   private int _despawnTimer = -1;
   public EnumFacing facing = EnumFacing.NORTH;

   public TileEntityBackpack() {
   }

   public int getAge() {
      return this._age;
   }

   public void update() {
      ++this._age;
      if (this.world.isRemote) {
         BackpackHelper.updateLidTicks(this, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D);
      } else if (this._despawnTimer >= 0) {
         EntityPlayer player = this.world.getClosestPlayer((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D, 20.0D, false);
         if (player == null && ++this._despawnTimer > 6000) {
            this._stack = ItemStack.EMPTY;
            this._data = null;
            this.world.setBlockToAir(this.pos);
         }
      }

   }

   public boolean receiveClientEvent(int id, int type) {
      if (id == 0) {
         this._playersUsing = type;
         return true;
      } else {
         return false;
      }
   }

   public void setPlacedOnDeath(boolean mayDespawn) {
      this._age = -10;
      this._despawnTimer = mayDespawn ? 0 : -1;
   }

   public void readNBT(NBTTagCompound compound, boolean isClient) {
      this._age = !isClient ? compound.getInteger("age") : 0;
      this.facing = EnumFacing.getFront(((Byte)NbtUtils.get(compound, Byte.valueOf((byte)0), "facing")).byteValue() + 2);
      this._stack = NbtUtils.readItem(compound.getCompoundTag("stack"));
      if (!this._stack.isEmpty() && !isClient) {
         this._data = BackpackHelper.getBackpackType(this._stack).createBackpackData(this._stack);
         NBTBase dataTag = compound.getTag("data");
         if (dataTag != null) {
            this._data.deserializeNBT(dataTag);
         }

         this._despawnTimer = compound.hasKey("despawnTimer") ? compound.getInteger("despawnTimer") : -1;
      } else {
         this._data = null;
      }
   }

   public NBTTagCompound writeNBT(NBTTagCompound compound, boolean isClient) {
      NbtUtils.addToCompound(compound, "age", !isClient ? this._age : null, "facing", (byte)(this.facing.ordinal() - 2), "stack", !this._stack.isEmpty() ? this._stack.serializeNBT() : null, "data", this._data != null && !isClient ? this._data.serializeNBT() : null, "despawnTimer", this._despawnTimer >= 0 && !isClient ? this._despawnTimer : null);
      return compound;
   }

   public final SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), this.writeNBT(new NBTTagCompound(), true));
   }

   public final NBTTagCompound getUpdateTag() {
      return this.writeNBT(super.writeToNBT(new NBTTagCompound()), true);
   }

   public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readNBT(pkt.getNbtCompound(), true);
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.readNBT(compound, false);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      return this.writeNBT(super.writeToNBT(compound), false);
   }

   public ItemStack getStack() {
      return this._stack;
   }

   public void setStack(ItemStack value) {
      this._stack = value;
   }

   public IBackpackData getData() {
      return this._data;
   }

   public void setData(IBackpackData value) {
      this._data = value;
   }

   public int getPlayersUsing() {
      return this._playersUsing;
   }

   public void setPlayersUsing(int value) {
      if (value > 0 != this._playersUsing > 0) {
         this.world.addBlockEvent(this.pos, this.getBlockType(), 0, value > 0 ? 1 : 0);
      }

      this._playersUsing = value;
      this._despawnTimer = -1;
   }

   public int getLidTicks() {
      return this._lidTicks;
   }

   public int getPrevLidTicks() {
      return this._prevLidTicks;
   }

   public void setLidTicks(int value) {
      this._prevLidTicks = this._lidTicks;
      this._lidTicks = value;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == IBackpack.CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && ModConfig.server.enableMachineInteraction && this.getData() instanceof BackpackDataItems || super.hasCapability(capability, facing);
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (!this.hasCapability(capability, facing)) {
         return (T)null;
      } else {
         return (T)(capability == IBackpack.CAPABILITY ? IBackpack.CAPABILITY.cast(this) : (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(((BackpackDataItems)this.getData()).getItems()) : super.getCapability(capability, facing)));
      }
   }
}
