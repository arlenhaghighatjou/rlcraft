package reborncore.common.tile;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IContainerProvider;
import reborncore.api.tile.IInventoryProvider;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.common.RebornCoreConfig;
import reborncore.common.blocks.RebornMachineBlock;
import reborncore.common.blocks.RebornOrientableTileBlock;
import reborncore.common.container.RebornContainer;
import reborncore.common.fluids.RebornFluidHandler;
import reborncore.common.fluids.RebornFluidTank;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.CustomDescriptionPacket;
import reborncore.common.recipes.IUpgradeHandler;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.Inventory;

public class RebornMachineTile extends TileEntity implements ITickable, ISidedInventory, IUpgradeable, IUpgradeHandler {
   public Inventory upgradeInventory = new Inventory(this.getUpgradeSlotCount(), "upgrades", 1, this);
   public SlotConfiguration slotConfiguration;
   public FluidConfiguration fluidConfiguration;
   double speedMultiplier = 0.0D;
   double powerMultiplier = 1.0D;
   private EnumFacing upgradeFacing = null;

   public RebornMachineTile() {
   }

   public void syncWithAll() {
      if (!this.world.isRemote) {
         NetworkManager.sendToAllAround(new CustomDescriptionPacket(this.pos, this.writeToNBT(new NBTTagCompound())), new TargetPoint(this.world.provider.getDimension(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 64.0D));
      }

   }

   public void resetUpgrades() {
      this.resetPowerMulti();
      this.resetSpeedMulti();
   }

   public int getFacingInt() {
      Block block = this.world.getBlockState(this.pos).getBlock();
      return block instanceof RebornMachineBlock ? ((RebornMachineBlock)block).getFacing(this.world, this.pos).getIndex() : 0;
   }

   public EnumFacing getFacing() {
      Block block = this.world.getBlockState(this.pos).getBlock();
      if (block instanceof RebornMachineBlock) {
         return ((RebornMachineBlock)block).getFacing(this.world, this.pos);
      } else {
         return block instanceof RebornOrientableTileBlock ? ((RebornOrientableTileBlock)block).getFacing(this.world, this.pos) : null;
      }
   }

   public boolean canSetFacing(EnumFacing facing) {
      return facing != this.getFacing();
   }

   public boolean setFacing(EnumFacing facing) {
      Block block = this.world.getBlockState(this.pos).getBlock();
      if (block instanceof RebornMachineBlock) {
         this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty(RebornMachineBlock.FACING, facing));
         return true;
      } else {
         if (block instanceof RebornOrientableTileBlock) {
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty(RebornOrientableTileBlock.FACING, facing));
         }

         return false;
      }
   }

   public Optional<Inventory> getInventoryForTile() {
      if (this instanceof IInventoryProvider) {
         IInventoryProvider inventory = (IInventoryProvider)this;
         return inventory.getInventory() == null ? Optional.empty() : Optional.of((Inventory)inventory.getInventory());
      } else {
         return Optional.empty();
      }
   }

   protected Optional<RecipeCrafter> getCrafterForTile() {
      if (this instanceof IRecipeCrafterProvider) {
         IRecipeCrafterProvider crafterProvider = (IRecipeCrafterProvider)this;
         return crafterProvider.getRecipeCrafter() == null ? Optional.empty() : Optional.of(crafterProvider.getRecipeCrafter());
      } else {
         return Optional.empty();
      }
   }

   protected Optional<RebornContainer> getContainerForTile() {
      if (this instanceof IContainerProvider) {
         IContainerProvider containerProvider = (IContainerProvider)this;
         return containerProvider.getContainer() == null ? Optional.empty() : Optional.of(containerProvider.getContainer());
      } else {
         return Optional.empty();
      }
   }

   public boolean hasSlotConfig() {
      return true;
   }

   @Nullable
   public RebornFluidTank getTank() {
      return null;
   }

   public boolean showTankConfig() {
      return this.getTank() != null;
   }

   public int slotTransferSpeed() {
      return 4;
   }

   public int fluidTransferAmount() {
      return 250;
   }

   public void setActive(boolean active) {
      Block block = this.world.getBlockState(this.pos).getBlock();
      if (block instanceof RebornMachineBlock) {
         ((RebornMachineBlock)block).setActive(Boolean.valueOf(active), this.world, this.pos);
      }

   }

   public boolean isActive() {
      Block block = this.world.getBlockState(this.pos).getBlock();
      return block instanceof RebornMachineBlock ? ((RebornMachineBlock)block).isActive(this.world.getBlockState(this.pos)) : false;
   }

   public void onLoad() {
      super.onLoad();
      if (this.slotConfiguration == null) {
         if (this.getInventoryForTile().isPresent()) {
            this.slotConfiguration = new SlotConfiguration((Inventory)this.getInventoryForTile().get());
         } else {
            this.slotConfiguration = new SlotConfiguration();
         }
      }

      if (this.getTank() != null && this.fluidConfiguration == null) {
         this.fluidConfiguration = new FluidConfiguration();
      }

      if (this.upgradeFacing != null) {
         this.setFacing(this.upgradeFacing);
         this.upgradeFacing = null;
      }

   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound compound = super.writeToNBT(new NBTTagCompound());
      this.writeToNBT(compound);
      return compound;
   }

   public void onDataPacket(net.minecraft.network.NetworkManager net, SPacketUpdateTileEntity pkt) {
      super.onDataPacket(net, pkt);
      this.readFromNBT(pkt.getNbtCompound());
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
      return oldState.getBlock() != newSate.getBlock();
   }

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      if (tagCompound.hasKey("facing")) {
         byte facingValue = tagCompound.getByte("facing");
         this.upgradeFacing = EnumFacing.VALUES[facingValue];
      }

      if (this.getInventoryForTile().isPresent()) {
         ((Inventory)this.getInventoryForTile().get()).readFromNBT(tagCompound);
      }

      if (this.getCrafterForTile().isPresent()) {
         ((RecipeCrafter)this.getCrafterForTile().get()).readFromNBT(tagCompound);
      }

      if (tagCompound.hasKey("slotConfig")) {
         this.slotConfiguration = new SlotConfiguration(tagCompound.getCompoundTag("slotConfig"));
      } else if (this.getInventoryForTile().isPresent()) {
         this.slotConfiguration = new SlotConfiguration((Inventory)this.getInventoryForTile().get());
      } else {
         this.slotConfiguration = new SlotConfiguration();
      }

      if (tagCompound.hasKey("fluidConfig") && this.getTank() != null) {
         this.fluidConfiguration = new FluidConfiguration(tagCompound.getCompoundTag("fluidConfig"));
      } else if (this.getTank() != null && this.fluidConfiguration == null) {
         this.fluidConfiguration = new FluidConfiguration();
      }

      this.upgradeInventory.readFromNBT(tagCompound, "Upgrades");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      if (this.getInventoryForTile().isPresent()) {
         ((Inventory)this.getInventoryForTile().get()).writeToNBT(tagCompound);
      }

      if (this.getCrafterForTile().isPresent()) {
         ((RecipeCrafter)this.getCrafterForTile().get()).writeToNBT(tagCompound);
      }

      if (this.slotConfiguration != null) {
         tagCompound.setTag("slotConfig", this.slotConfiguration.serializeNBT());
      }

      if (this.fluidConfiguration != null) {
         tagCompound.setTag("fluidConfig", this.fluidConfiguration.serializeNBT());
      }

      this.upgradeInventory.writeToNBT(tagCompound, "Upgrades");
      return tagCompound;
   }

   public ITextComponent getDisplayName() {
      return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).getDisplayName() : null;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         return true;
      } else if (this.getTank() != null && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
         if (this.fluidConfiguration != null && this.fluidConfiguration.getSideDetail(facing) != null) {
            FluidConfiguration.FluidConfig fluidConfig = this.fluidConfiguration.getSideDetail(facing);
            return fluidConfig.getIoConfig().isEnabled();
         } else {
            return true;
         }
      } else {
         return super.hasCapability(capability, facing);
      }
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing));
      } else {
         return (T)(this.getTank() != null && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new RebornFluidHandler(this, facing)) : super.getCapability(capability, facing));
      }
   }

   public void update() {
      RecipeCrafter crafter = null;
      if (this.getCrafterForTile().isPresent()) {
         crafter = (RecipeCrafter)this.getCrafterForTile().get();
      }

      if (this.canBeUpgraded()) {
         this.resetUpgrades();

         for(int i = 0; i < this.getUpgradeSlotCount(); ++i) {
            ItemStack stack = this.getUpgradeInventory().getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IUpgrade) {
               ((IUpgrade)stack.getItem()).process(this, stack);
            }
         }
      }

      if (!this.world.isRemote) {
         if (crafter != null) {
            crafter.updateEntity();
         }

         if (this.slotConfiguration != null) {
            this.slotConfiguration.update(this);
         }

         if (this.fluidConfiguration != null) {
            this.fluidConfiguration.update(this);
         }
      }

   }

   public int getSizeInventory() {
      return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).getSizeInventory() : 0;
   }

   public boolean isEmpty() {
      if (!this.getInventoryForTile().isPresent()) {
         return true;
      } else {
         for(ItemStack itemstack : ((Inventory)this.getInventoryForTile().get()).contents) {
            if (!itemstack.isEmpty()) {
               return false;
            }
         }

         return true;
      }
   }

   public ItemStack getStackInSlot(int index) {
      return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).getStackInSlot(index) : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int index, int count) {
      return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).decrStackSize(index, count) : ItemStack.EMPTY;
   }

   public ItemStack removeStackFromSlot(int index) {
      return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).removeStackFromSlot(index) : ItemStack.EMPTY;
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      if (this.getInventoryForTile().isPresent()) {
         ((Inventory)this.getInventoryForTile().get()).setInventorySlotContents(index, stack);
      }

   }

   public int getInventoryStackLimit() {
      return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).getInventoryStackLimit() : 0;
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
   }

   public void openInventory(EntityPlayer player) {
      if (this.getInventoryForTile().isPresent()) {
         ((Inventory)this.getInventoryForTile().get()).openInventory(player);
      }

   }

   public void closeInventory(EntityPlayer player) {
      if (this.getInventoryForTile().isPresent()) {
         ((Inventory)this.getInventoryForTile().get()).closeInventory(player);
      }

   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      if (this.slotConfiguration == null) {
         return false;
      } else {
         SlotConfiguration.SlotConfigHolder slotConfigHolder = this.slotConfiguration.getSlotDetails(index);
         if (slotConfigHolder.filter() && this.getCrafterForTile().isPresent()) {
            RecipeCrafter crafter = (RecipeCrafter)this.getCrafterForTile().get();
            if (!crafter.isStackValidInput(stack)) {
               return false;
            }
         }

         return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).isItemValidForSlot(index, stack) : false;
      }
   }

   public int getField(int id) {
      return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).getField(id) : 0;
   }

   public void setField(int id, int value) {
      if (this.getInventoryForTile().isPresent()) {
         ((Inventory)this.getInventoryForTile().get()).setField(id, value);
      }

   }

   public int getFieldCount() {
      return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).getFieldCount() : 0;
   }

   public void clear() {
      if (this.getInventoryForTile().isPresent()) {
         ((Inventory)this.getInventoryForTile().get()).clear();
      }

   }

   public String getName() {
      return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).getName() : null;
   }

   public boolean hasCustomName() {
      return this.getInventoryForTile().isPresent() ? ((Inventory)this.getInventoryForTile().get()).hasCustomName() : false;
   }

   public int[] getSlotsForFace(EnumFacing side) {
      return this.slotConfiguration == null ? new int[0] : this.slotConfiguration.getSlotsForSide(side).stream().filter(Objects::nonNull).filter((slotConfig) -> {
         return slotConfig.slotIO.ioConfig != SlotConfiguration.ExtractConfig.NONE;
      }).mapToInt((value) -> {
         return value.slotID;
      }).toArray();
   }

   public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
      SlotConfiguration.SlotConfigHolder slotConfigHolder = this.slotConfiguration.getSlotDetails(index);
      SlotConfiguration.SlotConfig slotConfig = slotConfigHolder.getSideDetail(direction);
      if (slotConfig.slotIO.ioConfig.isInsert()) {
         if (slotConfigHolder.filter() && this.getCrafterForTile().isPresent()) {
            RecipeCrafter crafter = (RecipeCrafter)this.getCrafterForTile().get();
            if (!crafter.isStackValidInput(itemStackIn)) {
               return false;
            }
         }

         if (!this.getContainerForTile().isPresent()) {
            return true;
         }

         RebornContainer container = (RebornContainer)this.getContainerForTile().get();
         if (container.slotMap.containsKey(Integer.valueOf(index))) {
            Slot slot = (Slot)container.slotMap.get(Integer.valueOf(index));
            return slot.isItemValid(itemStackIn);
         }
      }

      return false;
   }

   public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
      SlotConfiguration.SlotConfigHolder slotConfigHolder = this.slotConfiguration.getSlotDetails(index);
      SlotConfiguration.SlotConfig slotConfig = slotConfigHolder.getSideDetail(direction);
      if (slotConfig.slotIO.ioConfig.isExtact()) {
         if (!this.getContainerForTile().isPresent()) {
            return true;
         }

         RebornContainer container = (RebornContainer)this.getContainerForTile().get();
         if (container.slotMap.containsKey(Integer.valueOf(index))) {
            BaseSlot slot = (BaseSlot)container.slotMap.get(Integer.valueOf(index));
            return slot.canWorldBlockRemove();
         }
      }

      return false;
   }

   public ISidedInventory getUpgradeInventory() {
      return this.upgradeInventory;
   }

   public int getUpgradeSlotCount() {
      return 4;
   }

   public void resetSpeedMulti() {
      this.speedMultiplier = 0.0D;
   }

   public double getSpeedMultiplier() {
      return this.speedMultiplier;
   }

   public void addPowerMulti(double amount) {
      this.powerMultiplier *= 1.0D + amount;
   }

   public void resetPowerMulti() {
      this.powerMultiplier = 1.0D;
   }

   public double getPowerMultiplier() {
      return this.powerMultiplier;
   }

   public double getEuPerTick(double baseEu) {
      return baseEu * this.powerMultiplier;
   }

   public void addSpeedMulti(double amount) {
      if (RebornCoreConfig.exponentialMachineSpeedScaling) {
         double percentSpeedToApply = 1.0D - amount;
         double percentSpeedCurrent = 1.0D - this.speedMultiplier;
         double newPercentSpeed = percentSpeedToApply * percentSpeedCurrent;
         this.speedMultiplier = 1.0D - newPercentSpeed;
      } else if (this.speedMultiplier + amount <= 0.99D) {
         this.speedMultiplier += amount;
      } else {
         this.speedMultiplier = 0.99D;
      }

   }
}
