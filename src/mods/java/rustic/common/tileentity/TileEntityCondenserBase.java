package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.inventory.ExternalItemHandler;

public abstract class TileEntityCondenserBase extends TileFluidHandler implements ITickable {
   public static final int SLOT_RESULT = 0;
   public static final int SLOT_FUEL = 1;
   public static final int SLOT_BOTTLE = 2;
   public static final int SLOT_INGREDIENTS_START = 3;
   public static final int CAPACITY = 8000;
   protected ItemStackHandler internalStackHandler = new ItemStackHandler(this.getInternalSize()) {
      protected void onContentsChanged(int slot) {
         IBlockState state = TileEntityCondenserBase.this.world.getBlockState(TileEntityCondenserBase.this.pos);
         TileEntityCondenserBase.this.world.addBlockEvent(TileEntityCondenserBase.this.pos, TileEntityCondenserBase.this.getBlockType(), 1, 0);
         TileEntityCondenserBase.this.world.notifyBlockUpdate(TileEntityCondenserBase.this.pos, state, state, 3);
         TileEntityCondenserBase.this.world.notifyNeighborsOfStateChange(TileEntityCondenserBase.this.pos, TileEntityCondenserBase.this.getBlockType(), true);
         TileEntityCondenserBase.this.markDirty();
         TileEntityCondenserBase.this.hasContentChanged = true;
      }
   };
   protected ExternalItemHandler externalStackHandler;
   protected ICondenserRecipe currentRecipe;
   protected boolean hasContentChanged;
   public int condenserBurnTime;
   public int currentItemBurnTime;
   public int brewTime;
   public int totalBrewTime;

   protected abstract int getInternalSize();

   public TileEntityCondenserBase() {
      this.externalStackHandler = new ExternalItemHandler(this.internalStackHandler) {
         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == 1 && !TileEntityFurnace.isItemFuel(stack)) {
               return stack;
            } else if (slot == 2 && TileEntityFurnace.isItemFuel(stack)) {
               return stack;
            } else {
               return slot == 0 ? stack : super.insertItem(slot, stack, simulate);
            }
         }
      };
      this.tank = new FluidTank(8000) {
         protected void onContentsChanged() {
            TileEntityCondenserBase.this.markDirty();
            TileEntityCondenserBase.this.hasContentChanged = true;
         }
      };
      this.tank.setTileEntity(this);
      this.tank.setCanFill(true);
      this.tank.setCanDrain(true);
      this.currentRecipe = null;
      this.hasContentChanged = true;
   }

   public abstract String getName();

   public ITextComponent getDisplayName() {
      return new TextComponentTranslation(this.getName(), new Object[0]);
   }

   public int getCapacity() {
      return this.tank.getCapacity();
   }

   public int getAmount() {
      return this.tank.getFluidAmount();
   }

   public FluidTank getTank() {
      return this.tank;
   }

   public Fluid getFluid() {
      return this.tank.getFluid() != null ? this.tank.getFluid().getFluid() : null;
   }

   public void update() {
      boolean isBrewing = this.canBrew();
      boolean hasRecipe = isBrewing && this.hasValidRecipe();
      if (this.burnFuel(isBrewing && hasRecipe) && isBrewing && hasRecipe) {
         ++this.brewTime;
         this.renderParticles();
         if (this.brewTime >= this.currentRecipe.getTime()) {
            this.brewTime = 0;
            if (!this.world.isRemote) {
               this.brew();
            }
         }
      } else if (this.brewTime > 0) {
         this.brewTime = MathHelper.clamp(this.brewTime - 2, 0, this.totalBrewTime);
      }

   }

   protected abstract boolean canBrew();

   protected boolean hasValidRecipe() {
      if (this.condenserBurnTime <= 0 && this.internalStackHandler.getStackInSlot(1).isEmpty()) {
         return false;
      } else {
         if (this.hasContentChanged) {
            this.refreshCurrentRecipe();
         }

         if (this.currentRecipe == null) {
            return false;
         } else {
            return this.getAmount() < this.currentRecipe.getFluid().amount ? false : this.internalStackHandler.insertItem(0, this.currentRecipe.getResult(), true).isEmpty();
         }
      }
   }

   protected abstract void refreshCurrentRecipe();

   private boolean burnFuel(boolean consumeNewFuel) {
      if (this.condenserBurnTime > 0) {
         --this.condenserBurnTime;
         return true;
      } else {
         ItemStack fuelStack = this.internalStackHandler.getStackInSlot(1);
         if (consumeNewFuel && !fuelStack.isEmpty()) {
            this.condenserBurnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
            this.currentItemBurnTime = this.condenserBurnTime;
            if (!this.world.isRemote) {
               Item fuelItem = fuelStack.getItem();
               fuelStack.shrink(1);
               if (fuelStack.isEmpty()) {
                  this.internalStackHandler.setStackInSlot(1, fuelItem.getContainerItem(fuelStack));
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean isBurning() {
      return this.condenserBurnTime > 0;
   }

   protected abstract void renderParticles();

   protected abstract void brew();

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.getPos(), 0, this.getUpdateTag());
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }

   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      if (tag.hasKey("items")) {
         this.internalStackHandler.deserializeNBT((NBTTagCompound)tag.getTag("items"));
      }

      this.condenserBurnTime = tag.getInteger("BurnTime");
      this.brewTime = tag.getInteger("BrewTime");
      this.totalBrewTime = tag.getInteger("BrewTimeTotal");
      this.currentItemBurnTime = tag.getInteger("ItemBurnTime");
      this.hasContentChanged = true;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      tag = super.writeToNBT(tag);
      tag.setTag("items", this.internalStackHandler.serializeNBT());
      tag.setInteger("BurnTime", (short)this.condenserBurnTime);
      tag.setInteger("BrewTime", (short)this.brewTime);
      tag.setInteger("BrewTimeTotal", (short)this.totalBrewTime);
      tag.setInteger("ItemBurnTime", this.currentItemBurnTime);
      return tag;
   }

   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.getWorld().notifyBlockUpdate(this.pos, this.getWorld().getBlockState(this.pos), this.getWorld().getBlockState(this.pos), 3);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
         this.markDirty();
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      this.invalidate();
      if (this.internalStackHandler != null && !world.isRemote) {
         for(int i = 0; i < this.internalStackHandler.getSlots(); ++i) {
            if (this.internalStackHandler.getStackInSlot(i) != null) {
               Block.spawnAsEntity(world, pos, this.internalStackHandler.getStackInSlot(i));
            }
         }
      }

      world.setTileEntity(pos, (TileEntity)null);
   }

   public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack heldItem = player.getHeldItem(hand);
      if (heldItem != ItemStack.EMPTY) {
         if (FluidUtil.getFluidHandler(heldItem) != null || heldItem.getItem() instanceof ItemBucket || heldItem.getItem() instanceof UniversalBucket) {
            boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, (IFluidHandler)this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side));
            if (didFill) {
               this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
               this.getWorld().notifyBlockUpdate(pos, state, state, 3);
               this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
               this.markDirty();
               return true;
            }
         }
      } else if (player.isSneaking() && this.tank.getFluidAmount() > 0) {
         FluidStack drained = this.tank.drainInternal(8000, true);
         SoundEvent soundevent = drained.getFluid().getEmptySound(drained);
         this.world.playSound((EntityPlayer)null, this.pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
         this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
         this.getWorld().notifyBlockUpdate(pos, state, state, 3);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
         this.markDirty();
         return true;
      }

      return false;
   }
}
