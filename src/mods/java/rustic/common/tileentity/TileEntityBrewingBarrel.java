package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustic.common.Config;
import rustic.common.blocks.fluids.FluidBooze;
import rustic.common.crafting.IBrewingBarrelRecipe;
import rustic.common.crafting.Recipes;
import rustic.common.inventory.ExternalItemHandler;
import rustic.common.items.ModItems;

public class TileEntityBrewingBarrel extends TileEntity implements ITickable {
   protected ItemStackHandler internalStackHandler = new ItemStackHandler(6) {
      protected void onContentsChanged(int slot) {
         IBlockState state = TileEntityBrewingBarrel.this.world.getBlockState(TileEntityBrewingBarrel.this.pos);
         TileEntityBrewingBarrel.this.world.addBlockEvent(TileEntityBrewingBarrel.this.pos, TileEntityBrewingBarrel.this.getBlockType(), 1, 0);
         TileEntityBrewingBarrel.this.getWorld().notifyBlockUpdate(TileEntityBrewingBarrel.this.pos, state, state, 3);
         TileEntityBrewingBarrel.this.world.notifyNeighborsOfStateChange(TileEntityBrewingBarrel.this.pos, TileEntityBrewingBarrel.this.getBlockType(), true);
         TileEntityBrewingBarrel.this.markDirty();
      }
   };
   private ExternalItemHandler externalStackHandler;
   protected FluidTank input;
   protected FluidTank output;
   protected FluidTank auxiliary;
   protected int brewTime;
   protected IBrewingBarrelRecipe recipe;

   public TileEntityBrewingBarrel() {
      this.externalStackHandler = new ExternalItemHandler(this.internalStackHandler) {
         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return this.isStackAllowedInSlot(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
         }

         private boolean isStackAllowedInSlot(int slot, ItemStack stack) {
            if (slot == 1) {
               return stack.getItem() == Items.GLASS_BOTTLE;
            } else if (slot == 2) {
               if (FluidUtil.getFluidHandler(stack) != null) {
                  FluidStack fluid = FluidUtil.getFluidContained(stack);
                  if (fluid != null && fluid.getFluid() != null) {
                     return fluid.getFluid() instanceof FluidBooze;
                  }
               }

               return stack.getItem() == Items.GLASS_BOTTLE;
            } else if (slot != 0) {
               return false;
            } else {
               return FluidUtil.getFluidHandler(stack) != null || stack.getItem() instanceof UniversalBucket || stack.getItem() instanceof ItemFluidContainer || stack.getItem() == Items.GLASS_BOTTLE || stack.getItem() instanceof ItemBucket;
            }
         }
      };
      this.input = new FluidTank(8000);
      this.output = new FluidTank(8000);
      this.auxiliary = new FluidTank(1000);
      this.recipe = null;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      super.writeToNBT(tag);
      tag.setTag("items", this.internalStackHandler.serializeNBT());
      NBTTagCompound inputTank = new NBTTagCompound();
      this.input.writeToNBT(inputTank);
      tag.setTag("inputTank", inputTank);
      NBTTagCompound outputTank = new NBTTagCompound();
      this.output.writeToNBT(outputTank);
      tag.setTag("outputTank", outputTank);
      NBTTagCompound auxTank = new NBTTagCompound();
      this.auxiliary.writeToNBT(auxTank);
      tag.setTag("auxTank", auxTank);
      tag.setInteger("brewTime", this.brewTime);
      return tag;
   }

   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      if (tag.hasKey("items")) {
         this.internalStackHandler.deserializeNBT((NBTTagCompound)tag.getTag("items"));
      }

      this.input.readFromNBT(tag.getCompoundTag("inputTank"));
      this.output.readFromNBT(tag.getCompoundTag("outputTank"));
      this.auxiliary.readFromNBT(tag.getCompoundTag("auxTank"));
      if (tag.hasKey("brewTime")) {
         this.brewTime = tag.getInteger("brewTime");
      }

   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
      return oldState.getBlock() != newState.getBlock();
   }

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

   public ITextComponent getDisplayName() {
      return new TextComponentTranslation(this.getName(), new Object[0]);
   }

   public String getName() {
      return "container.rustic.brewing_barrel";
   }

   public int getInputAmount() {
      return this.input.getFluidAmount();
   }

   public int getOutputAmount() {
      return this.output.getFluidAmount();
   }

   public int getAuxiliaryAmount() {
      return this.auxiliary.getFluidAmount();
   }

   public int getInputCapacity() {
      return this.input.getCapacity();
   }

   public int getOutputCapacity() {
      return this.output.getCapacity();
   }

   public int getAuxiliaryCapacity() {
      return this.auxiliary.getCapacity();
   }

   public FluidStack getInputFluid() {
      return this.input.getFluid() != null ? this.input.getFluid().copy() : null;
   }

   public FluidStack getOutputFluid() {
      return this.output.getFluid() != null ? this.output.getFluid().copy() : null;
   }

   public FluidStack getAuxiliaryFluid() {
      return this.auxiliary.getFluid() != null ? this.auxiliary.getFluid().copy() : null;
   }

   public boolean slot0Empty() {
      return this.internalStackHandler.getStackInSlot(0).isEmpty();
   }

   public boolean slot1Empty() {
      return this.internalStackHandler.getStackInSlot(1).isEmpty();
   }

   public boolean slot2Empty() {
      return this.internalStackHandler.getStackInSlot(2).isEmpty();
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.externalStackHandler : super.getCapability(capability, facing));
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

   public void update() {
      boolean fluidChanged = false;
      if (!this.internalStackHandler.getStackInSlot(0).isEmpty() && !this.world.isRemote) {
         ItemStack stack = this.internalStackHandler.getStackInSlot(0);
         ItemStack in = stack.copy();
         in.setCount(1);
         if (stack.getItem() == Items.GLASS_BOTTLE && this.input.getFluidAmount() > 0) {
            ItemStack out = new ItemStack(ModItems.FLUID_BOTTLE);
            IFluidHandlerItem fluidHandlerDummy = (IFluidHandlerItem)out.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null);
            int amount = fluidHandlerDummy.fill(this.input.getFluid(), true);
            out = fluidHandlerDummy.getContainer();
            if (amount > 0 && this.internalStackHandler.insertItem(3, out, true).isEmpty()) {
               this.input.drain(amount, true);
               this.internalStackHandler.getStackInSlot(0).shrink(1);
               this.internalStackHandler.insertItem(3, out, false);
               fluidChanged |= true;
            }
         } else if (in.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null)) {
            FluidStack fluid = FluidUtil.getFluidContained(in);
            if (fluid != null && fluid.getFluid() != null && this.input.fill(fluid, false) == fluid.amount) {
               ItemStack out = new ItemStack(in.getItem());
               IFluidHandlerItem fluidHandlerDummy = (IFluidHandlerItem)out.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null);
               if (fluidHandlerDummy != null) {
                  fluidHandlerDummy.drain(this.input.getCapacity() - this.input.getFluidAmount(), true);
                  out = fluidHandlerDummy.getContainer();
                  if (out.getItem() == ForgeModContainer.getInstance().universalBucket) {
                     out = new ItemStack(Items.BUCKET);
                  }

                  if (this.internalStackHandler.insertItem(3, out, true).isEmpty()) {
                     this.input.fill(fluid, true);
                     this.internalStackHandler.getStackInSlot(0).shrink(1);
                     this.internalStackHandler.insertItem(3, out, false);
                     fluidChanged |= true;
                  }
               }
            } else if ((fluid == null || fluid.getFluid() == null) && this.input.getFluidAmount() > 0) {
               ItemStack out = new ItemStack(in.getItem());
               IFluidHandlerItem fluidHandlerDummy = (IFluidHandlerItem)out.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null);
               int amount = fluidHandlerDummy.fill(this.input.getFluid(), true);
               out = fluidHandlerDummy.getContainer();
               if (amount > 0 && this.internalStackHandler.insertItem(3, out, true).isEmpty()) {
                  this.input.drain(amount, true);
                  this.internalStackHandler.getStackInSlot(0).shrink(1);
                  this.internalStackHandler.insertItem(3, out, false);
                  fluidChanged |= true;
               }
            }
         }
      }

      if (!this.internalStackHandler.getStackInSlot(1).isEmpty() && !this.world.isRemote) {
         ItemStack stack = this.internalStackHandler.getStackInSlot(1);
         ItemStack in = stack.copy();
         in.setCount(1);
         if (stack.getItem() == Items.GLASS_BOTTLE && this.output.getFluidAmount() > 0) {
            ItemStack out = new ItemStack(ModItems.FLUID_BOTTLE);
            IFluidHandlerItem fluidHandlerDummy = (IFluidHandlerItem)out.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null);
            int amount = fluidHandlerDummy.fill(this.output.getFluid(), true);
            out = fluidHandlerDummy.getContainer();
            if (amount > 0 && this.internalStackHandler.insertItem(4, out, true).isEmpty()) {
               this.output.drain(amount, true);
               this.internalStackHandler.getStackInSlot(1).shrink(1);
               this.internalStackHandler.insertItem(4, out, false);
               fluidChanged |= true;
            }
         } else if (in.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null)) {
            FluidStack fluid = FluidUtil.getFluidContained(in);
            if ((fluid == null || fluid.getFluid() == null) && this.output.getFluidAmount() > 0) {
               ItemStack out = new ItemStack(in.getItem());
               IFluidHandlerItem fluidHandlerDummy = (IFluidHandlerItem)out.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null);
               int amount = fluidHandlerDummy.fill(this.output.getFluid(), true);
               out = fluidHandlerDummy.getContainer();
               if (amount > 0 && this.internalStackHandler.insertItem(4, out, true).isEmpty()) {
                  this.output.drain(amount, true);
                  this.internalStackHandler.getStackInSlot(1).shrink(1);
                  this.internalStackHandler.insertItem(4, out, false);
                  fluidChanged |= true;
               }
            }
         }
      }

      if (!this.internalStackHandler.getStackInSlot(2).isEmpty() && !this.world.isRemote) {
         ItemStack stack = this.internalStackHandler.getStackInSlot(2);
         ItemStack in = stack.copy();
         in.setCount(1);
         if (stack.getItem() == Items.GLASS_BOTTLE && this.auxiliary.getFluidAmount() > 0) {
            ItemStack out = new ItemStack(ModItems.FLUID_BOTTLE);
            IFluidHandlerItem fluidHandlerDummy = (IFluidHandlerItem)out.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null);
            int amount = fluidHandlerDummy.fill(this.auxiliary.getFluid(), true);
            out = fluidHandlerDummy.getContainer();
            if (out.hasTagCompound() && out.getTagCompound().hasKey("Fluid")) {
               NBTTagCompound fluidTag = out.getTagCompound().getCompoundTag("Fluid");
               if (!fluidTag.hasKey("Tag") && this.auxiliary.getFluid().tag != null) {
                  fluidTag.setTag("Tag", this.auxiliary.getFluid().tag);
               }
            }

            if (amount > 0 && this.internalStackHandler.insertItem(5, out, true).isEmpty()) {
               this.auxiliary.drain(amount, true);
               this.internalStackHandler.getStackInSlot(2).shrink(1);
               this.internalStackHandler.insertItem(5, out, false);
               fluidChanged |= true;
            }
         } else if (in.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null)) {
            FluidStack fluid = FluidUtil.getFluidContained(in);
            if (fluid != null && fluid.getFluid() != null && fluid.getFluid() instanceof FluidBooze && this.auxiliary.fill(fluid, false) == fluid.amount) {
               ItemStack out = new ItemStack(in.getItem());
               IFluidHandlerItem fluidHandlerDummy = (IFluidHandlerItem)out.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null);
               fluidHandlerDummy.drain(this.auxiliary.getCapacity() - this.auxiliary.getFluidAmount(), true);
               out = fluidHandlerDummy.getContainer();
               if (out.getItem() == ForgeModContainer.getInstance().universalBucket) {
                  out = new ItemStack(Items.BUCKET);
               }

               if (this.internalStackHandler.insertItem(5, out, true).isEmpty()) {
                  this.auxiliary.fill(fluid, true);
                  this.internalStackHandler.getStackInSlot(2).shrink(1);
                  this.internalStackHandler.insertItem(5, out, false);
                  fluidChanged |= true;
               }
            } else if ((fluid == null || fluid.getFluid() == null) && this.auxiliary.getFluidAmount() > 0) {
               ItemStack out = new ItemStack(in.getItem());
               IFluidHandlerItem fluidHandlerDummy = (IFluidHandlerItem)out.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null);
               int amount = fluidHandlerDummy.fill(this.auxiliary.getFluid(), true);
               out = fluidHandlerDummy.getContainer();
               if (amount > 0 && this.internalStackHandler.insertItem(5, out, true).isEmpty()) {
                  this.auxiliary.drain(amount, true);
                  this.internalStackHandler.getStackInSlot(2).shrink(1);
                  this.internalStackHandler.insertItem(5, out, false);
                  fluidChanged |= true;
               }
            }
         }
      }

      if (fluidChanged) {
         this.brewTime = 0;
         if (this.recipe != null && !this.recipe.matches(this.input.getFluid(), this.auxiliary.getFluid())) {
            this.recipe = null;
         }

         this.getWorld().notifyBlockUpdate(this.pos, this.getWorld().getBlockState(this.pos), this.getWorld().getBlockState(this.pos), 3);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
         this.markDirty();
      }

      if (this.recipe == null && this.input.getFluidAmount() > 0) {
         this.recipe = this.getRecipe();
      }

      if (this.recipe != null && this.brewTime % 20 == 0) {
         this.recipe = this.getRecipe();
      }

      if (this.recipe != null) {
         ++this.brewTime;
         if (this.brewTime >= this.getMaxBrewTime()) {
            this.brew();
            this.brewTime = 0;
            this.recipe = null;
         }
      } else {
         this.brewTime = 0;
      }

   }

   private void brew() {
      this.brewTime = 0;
      int amount = this.output.getCapacity() - this.output.getFluidAmount();
      amount = Math.min(amount, this.input.getFluidAmount());
      if (amount > 0 && this.recipe.matches(this.input.getFluid(), this.auxiliary.getFluid()) && !this.world.isRemote) {
         FluidStack out = this.recipe.getResult(this.input.getFluid(), this.auxiliary.getFluid());
         if (this.output.getFluidAmount() > 0 && this.output.getFluid().getFluid() == out.getFluid()) {
            out = this.output.getFluid().copy();
         }

         out.amount = amount;
         if (this.output.fill(out, false) == amount && this.input.drain(amount, false).amount == amount) {
            this.output.fill(out, true);
            this.input.drain(amount, true);
            this.recipe = null;
         }
      }

      this.getWorld().notifyBlockUpdate(this.pos, this.getWorld().getBlockState(this.pos), this.getWorld().getBlockState(this.pos), 3);
      this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
      this.markDirty();
   }

   private IBrewingBarrelRecipe getRecipe() {
      if (this.input.getFluidAmount() > 0) {
         for(IBrewingBarrelRecipe recipe : Recipes.brewingRecipes) {
            if (recipe.matches(this.input.getFluid(), this.auxiliary.getFluid()) && (this.output.getFluid() == null || recipe.getResult(this.input.getFluid(), this.auxiliary.getFluid()).getFluid() == this.output.getFluid().getFluid() && this.output.getFluidAmount() < this.output.getCapacity())) {
               return recipe;
            }
         }
      }

      return null;
   }

   public boolean isBrewing() {
      return this.brewTime > 0;
   }

   public int getBrewTime() {
      return this.brewTime;
   }

   public int getMaxBrewTime() {
      return Config.MAX_BREW_TIME;
   }
}
