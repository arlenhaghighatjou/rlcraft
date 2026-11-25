package reborncore.common.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import reborncore.RebornCore;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.api.recipe.IBaseRecipeType;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.recipe.RecipeHandler;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.util.Inventory;
import reborncore.common.util.ItemUtils;

public class RecipeCrafter implements IUpgradeHandler {
   public String recipeName;
   public TileEntity parentTile;
   public IEnergyInterfaceTile energy;
   public Optional<IUpgradeHandler> parentUpgradeHandler = Optional.empty();
   public int inputs;
   public int outputs;
   public IInventory inventory;
   public int[] inputSlots;
   public int[] outputSlots;
   public IBaseRecipeType currentRecipe;
   public int currentTickTime = 0;
   public int currentNeededTicks = 1;
   double lastEnergy;
   int ticksSinceLastChange;
   @Nullable
   public static ICrafterSoundHanlder soundHanlder = null;

   public RecipeCrafter(String recipeName, TileEntity parentTile, int inputs, int outputs, Inventory inventory, int[] inputSlots, int[] outputSlots) {
      this.recipeName = recipeName;
      this.parentTile = parentTile;
      if (parentTile instanceof IEnergyInterfaceTile) {
         this.energy = (IEnergyInterfaceTile)parentTile;
      }

      if (parentTile instanceof IUpgradeHandler) {
         this.parentUpgradeHandler = Optional.of((IUpgradeHandler)parentTile);
      }

      this.inputs = inputs;
      this.outputs = outputs;
      this.inventory = inventory;
      this.inputSlots = inputSlots;
      this.outputSlots = outputSlots;
      if (!(parentTile instanceof IRecipeCrafterProvider)) {
         RebornCore.logHelper.error(parentTile.getClass().getName() + " does not use IRecipeCrafterProvider report this to the issue tracker!");
      }

   }

   public void updateEntity() {
      if (!this.parentTile.getWorld().isRemote) {
         ++this.ticksSinceLastChange;
         if (this.ticksSinceLastChange == 20) {
            this.setInvDirty(true);
            this.ticksSinceLastChange = 0;
         }

         if (this.currentRecipe == null && this.isInvDirty()) {
            this.updateCurrentRecipe();
         }

         if (this.currentRecipe != null) {
            if (this.isInvDirty() && !this.hasAllInputs()) {
               this.currentRecipe = null;
               this.currentTickTime = 0;
               this.setIsActive();
            }

            if (this.currentRecipe != null && this.currentTickTime >= this.currentNeededTicks && this.hasAllInputs()) {
               boolean canGiveInvAll = true;

               for(int i = 0; i < this.currentRecipe.getOutputsSize(); ++i) {
                  if (!this.canFitStack(this.currentRecipe.getOutput(i), this.outputSlots[i], this.currentRecipe.useOreDic())) {
                     canGiveInvAll = false;
                  }
               }

               ArrayList<Integer> filledSlots = new ArrayList();
               if (canGiveInvAll && this.currentRecipe.onCraft(this.parentTile)) {
                  for(int i = 0; i < this.currentRecipe.getOutputsSize(); ++i) {
                     if (!filledSlots.contains(Integer.valueOf(this.outputSlots[i]))) {
                        this.fitStack(this.currentRecipe.getOutput(i).copy(), this.outputSlots[i]);
                        filledSlots.add(Integer.valueOf(this.outputSlots[i]));
                     }
                  }

                  this.useAllInputs();
                  this.currentRecipe = null;
                  this.currentTickTime = 0;
                  this.updateCurrentRecipe();
                  if (this.currentRecipe == null) {
                     this.setIsActive();
                  }
               }
            } else if (this.currentRecipe != null && this.currentTickTime < this.currentNeededTicks && this.energy.canUseEnergy(this.getEuPerTick((double)this.currentRecipe.euPerTick()))) {
               this.energy.useEnergy(this.getEuPerTick((double)this.currentRecipe.euPerTick()));
               ++this.currentTickTime;
               if (this.currentTickTime == 1 || this.currentTickTime % 20 == 0 && soundHanlder != null) {
                  soundHanlder.playSound(false, this.parentTile);
               }
            }
         }

         this.setInvDirty(false);
      }
   }

   public void updateCurrentRecipe() {
      this.currentTickTime = 0;

      for(IBaseRecipeType recipe : RecipeHandler.getRecipeClassFromName(this.recipeName)) {
         if (recipe.canCraft(this.parentTile) && this.hasAllInputs(recipe)) {
            for(int i = 0; i < recipe.getOutputsSize(); ++i) {
               if (!this.canFitStack(recipe.getOutput(i), this.outputSlots[i], recipe.useOreDic())) {
                  this.currentRecipe = null;
                  this.currentTickTime = 0;
                  this.setIsActive();
                  return;
               }
            }

            this.setCurrentRecipe(recipe);
            this.currentNeededTicks = Math.max((int)((double)this.currentRecipe.tickTime() * (1.0D - this.getSpeedMultiplier())), 1);
            this.currentTickTime = 0;
            this.setIsActive();
            return;
         }
      }

   }

   public boolean hasAllInputs() {
      if (this.currentRecipe == null) {
         return false;
      } else {
         for(Object input : this.currentRecipe.getInputs()) {
            boolean hasItem = false;
            boolean useOreDict = input instanceof String || input instanceof OreRecipeInput || this.currentRecipe.useOreDic();
            boolean checkSize = input instanceof ItemStack || input instanceof OreRecipeInput;

            for(int inputslot : this.inputSlots) {
               if (ItemUtils.isInputEqual(input, this.inventory.getStackInSlot(inputslot), true, true, useOreDict)) {
                  ItemStack stack = RecipeTranslator.getStackFromObject(input);
                  if (!checkSize || this.inventory.getStackInSlot(inputslot).getCount() >= stack.getCount()) {
                     hasItem = true;
                  }
               }
            }

            if (!hasItem) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean hasAllInputs(IBaseRecipeType recipeType) {
      if (recipeType == null) {
         return false;
      } else {
         for(Object input : recipeType.getInputs()) {
            boolean hasItem = false;
            boolean useOreDict = input instanceof String || input instanceof OreRecipeInput || recipeType.useOreDic();
            boolean checkSize = input instanceof ItemStack || input instanceof OreRecipeInput;

            for(int inputslot : this.inputSlots) {
               if (ItemUtils.isInputEqual(input, this.inventory.getStackInSlot(inputslot), true, true, useOreDict)) {
                  ItemStack stack = RecipeTranslator.getStackFromObject(input);
                  if (!checkSize || this.inventory.getStackInSlot(inputslot).getCount() >= stack.getCount()) {
                     hasItem = true;
                  }
               }
            }

            if (!hasItem) {
               return false;
            }
         }

         return true;
      }
   }

   public void useAllInputs() {
      if (this.currentRecipe != null) {
         for(Object input : this.currentRecipe.getInputs()) {
            for(int inputSlot : this.inputSlots) {
               if (ItemUtils.isInputEqual(input, this.inventory.getStackInSlot(inputSlot), true, true, this.currentRecipe.useOreDic())) {
                  int count = 1;
                  if (input instanceof ItemStack || input instanceof OreRecipeInput) {
                     count = RecipeTranslator.getStackFromObject(input).getCount();
                  }

                  if (this.inventory.getStackInSlot(inputSlot).getCount() >= count) {
                     this.inventory.decrStackSize(inputSlot, count);
                     break;
                  }
               }
            }
         }

      }
   }

   public boolean canFitStack(ItemStack stack, int slot, boolean oreDic) {
      if (stack.isEmpty()) {
         return true;
      } else if (this.inventory.getStackInSlot(slot).isEmpty()) {
         return true;
      } else {
         return ItemUtils.isItemEqual(this.inventory.getStackInSlot(slot), stack, true, true, oreDic) && stack.getCount() + this.inventory.getStackInSlot(slot).getCount() <= stack.getMaxStackSize();
      }
   }

   public void fitStack(ItemStack stack, int slot) {
      if (!stack.isEmpty()) {
         if (this.inventory.getStackInSlot(slot).isEmpty()) {
            this.inventory.setInventorySlotContents(slot, stack);
         } else {
            if (ItemUtils.isItemEqual(this.inventory.getStackInSlot(slot), stack, true, true, this.currentRecipe.useOreDic()) && stack.getCount() + this.inventory.getStackInSlot(slot).getCount() <= stack.getMaxStackSize()) {
               ItemStack newStack = stack.copy();
               newStack.setCount(this.inventory.getStackInSlot(slot).getCount() + stack.getCount());
               this.inventory.setInventorySlotContents(slot, newStack);
            }

         }
      }
   }

   public void readFromNBT(NBTTagCompound tag) {
      NBTTagCompound data = tag.getCompoundTag("Crater");
      if (data.hasKey("currentTickTime")) {
         this.currentTickTime = data.getInteger("currentTickTime");
      }

      if (this.parentTile != null && this.parentTile.getWorld() != null && this.parentTile.getWorld().isRemote) {
         this.parentTile.getWorld().notifyBlockUpdate(this.parentTile.getPos(), this.parentTile.getWorld().getBlockState(this.parentTile.getPos()), this.parentTile.getWorld().getBlockState(this.parentTile.getPos()), 3);
         this.parentTile.getWorld().markBlockRangeForRenderUpdate(this.parentTile.getPos().getX(), this.parentTile.getPos().getY(), this.parentTile.getPos().getZ(), this.parentTile.getPos().getX(), this.parentTile.getPos().getY(), this.parentTile.getPos().getZ());
      }

   }

   public void writeToNBT(NBTTagCompound tag) {
      NBTTagCompound data = new NBTTagCompound();
      data.setDouble("currentTickTime", (double)this.currentTickTime);
      tag.setTag("Crater", data);
   }

   private boolean isActive() {
      return this.currentRecipe != null && this.energy.getEnergy() >= (double)this.currentRecipe.euPerTick();
   }

   public boolean canCraftAgain() {
      for(IBaseRecipeType recipe : RecipeHandler.getRecipeClassFromName(this.recipeName)) {
         if (recipe.canCraft(this.parentTile) && this.hasAllInputs(recipe)) {
            boolean canGiveInvAll = true;

            for(int i = 0; i < recipe.getOutputsSize(); ++i) {
               if (!this.canFitStack(recipe.getOutput(i), this.outputSlots[i], recipe.useOreDic())) {
                  canGiveInvAll = false;
                  return false;
               }
            }

            if (this.energy.getEnergy() < (double)recipe.euPerTick()) {
               return false;
            }

            return canGiveInvAll;
         }
      }

      return false;
   }

   public void setIsActive() {
      if (this.parentTile instanceof RebornMachineTile) {
         ((RebornMachineTile)this.parentTile).setActive(this.isActive());
      }

   }

   public void setCurrentRecipe(IBaseRecipeType recipe) {
      try {
         this.currentRecipe = (IBaseRecipeType)recipe.clone();
      } catch (CloneNotSupportedException var3) {
         var3.printStackTrace();
      }

   }

   public boolean isInvDirty() {
      if (this.inventory instanceof Inventory) {
         return ((Inventory)this.inventory).isDirty;
      } else {
         return this.inventory instanceof Inventory ? ((Inventory)this.inventory).hasChanged : true;
      }
   }

   public void setInvDirty(boolean isDiry) {
      if (this.inventory instanceof Inventory) {
         ((Inventory)this.inventory).isDirty = isDiry;
      } else if (this.inventory instanceof Inventory) {
         ((Inventory)this.inventory).hasChanged = isDiry;
      }

   }

   public boolean isStackValidInput(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         label36:
         for(IBaseRecipeType recipe : RecipeHandler.getRecipeClassFromName(this.recipeName)) {
            Iterator var4 = recipe.getInputs().iterator();

            while(true) {
               if (!var4.hasNext()) {
                  continue label36;
               }

               Object input = var4.next();
               boolean useOreDict = input instanceof String || input instanceof OreRecipeInput || recipe.useOreDic();
               if (ItemUtils.isInputEqual(input, stack, true, true, useOreDict)) {
                  break;
               }
            }

            return true;
         }

         return false;
      }
   }

   public void resetSpeedMulti() {
      this.parentUpgradeHandler.ifPresent(IUpgradeHandler::resetSpeedMulti);
   }

   public double getSpeedMultiplier() {
      return Math.min(((Double)this.parentUpgradeHandler.map(IUpgradeHandler::getSpeedMultiplier).orElse(Double.valueOf(0.0D))).doubleValue(), 0.975D);
   }

   public void addPowerMulti(double amount) {
      this.parentUpgradeHandler.ifPresent((iUpgradeHandler) -> {
         iUpgradeHandler.addPowerMulti(amount);
      });
   }

   public void resetPowerMulti() {
      this.parentUpgradeHandler.ifPresent(IUpgradeHandler::resetPowerMulti);
   }

   public double getPowerMultiplier() {
      return ((Double)this.parentUpgradeHandler.map(IUpgradeHandler::getPowerMultiplier).orElse(Double.valueOf(1.0D))).doubleValue();
   }

   public double getEuPerTick(double baseEu) {
      double power = ((Double)this.parentUpgradeHandler.map((iUpgradeHandler) -> {
         return iUpgradeHandler.getEuPerTick(baseEu);
      }).orElse(Double.valueOf(1.0D))).doubleValue();
      return Math.min(power, this.energy.getMaxPower());
   }

   public void addSpeedMulti(double amount) {
      this.parentUpgradeHandler.ifPresent((iUpgradeHandler) -> {
         iUpgradeHandler.addSpeedMulti(amount);
      });
   }
}
