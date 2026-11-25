package reborncore.api.praescriptum.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
import reborncore.common.util.ItemUtils;

public class Recipe {
   private final RecipeHandler handler;
   private List<InputIngredient<?>> inputIngredients = new ArrayList();
   private ItemStack[] itemOutputs = new ItemStack[0];
   private FluidStack[] fluidOutputs = new FluidStack[0];
   private NBTTagCompound metadata;
   private boolean useNBT;

   public Recipe(RecipeHandler handler) {
      this.handler = handler;
      this.useNBT = false;
   }

   public Recipe withInput(Collection<InputIngredient<?>> inputs) {
      this.inputIngredients.addAll(inputs);
      return this;
   }

   public Recipe withInput(ItemStack itemStack) {
      if (ItemUtils.isEmpty(itemStack)) {
         throw new IllegalArgumentException("Input cannot be empty");
      } else {
         this.inputIngredients.add(ItemStackInputIngredient.of(itemStack));
         return this;
      }
   }

   public Recipe withInput(String oreDict) {
      this.inputIngredients.add(OreDictionaryInputIngredient.of(oreDict));
      return this;
   }

   public Recipe withInput(String oreDict, int amount) {
      if (amount <= 0) {
         throw new IllegalArgumentException("Input cannot be empty");
      } else {
         this.inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount));
         return this;
      }
   }

   public Recipe withInput(String oreDict, int amount, Integer meta) {
      if (amount <= 0) {
         throw new IllegalArgumentException("Input cannot be empty");
      } else {
         this.inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount, meta));
         return this;
      }
   }

   public Recipe withInput(FluidStack fluidStack) {
      if (fluidStack.amount <= 0) {
         throw new IllegalArgumentException("Input cannot be empty");
      } else {
         this.inputIngredients.add(FluidStackInputIngredient.of(fluidStack));
         return this;
      }
   }

   public Recipe withOutput(ItemStack itemStack) {
      if (ItemUtils.isEmpty(itemStack)) {
         throw new IllegalArgumentException("Output cannot be empty");
      } else {
         this.itemOutputs = (ItemStack[])Arrays.copyOf(this.itemOutputs, this.itemOutputs.length + 1);
         this.itemOutputs[this.itemOutputs.length - 1] = itemStack;
         return this;
      }
   }

   public Recipe withOutput(FluidStack fluidStack) {
      if (fluidStack.amount <= 0) {
         throw new IllegalArgumentException("Output cannot be empty");
      } else {
         this.fluidOutputs = (FluidStack[])Arrays.copyOf(this.fluidOutputs, this.fluidOutputs.length + 1);
         this.fluidOutputs[this.fluidOutputs.length - 1] = fluidStack;
         return this;
      }
   }

   public Recipe withEnergyCostPerTick(int energyCostPerTick) {
      if (energyCostPerTick < 0) {
         throw new IllegalArgumentException("Energy cost per tick cannot be less than 0");
      } else {
         if (this.metadata == null) {
            this.metadata = new NBTTagCompound();
         }

         this.metadata.setInteger("energyCostPerTick", energyCostPerTick);
         return this;
      }
   }

   public Recipe withOperationDuration(int operationDuration) {
      if (operationDuration < 0) {
         throw new IllegalArgumentException("Operation duration cannot be less than 0");
      } else {
         if (this.metadata == null) {
            this.metadata = new NBTTagCompound();
         }

         this.metadata.setInteger("operationDuration", operationDuration);
         return this;
      }
   }

   public Recipe withMetadata(NBTTagCompound metadata) {
      this.metadata = metadata;
      return this;
   }

   public Recipe withMetadata(String key, int value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setInteger(key, value);
      return this;
   }

   public Recipe withMetadata(String key, short value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setShort(key, value);
      return this;
   }

   public Recipe withMetadata(String key, byte value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setByte(key, value);
      return this;
   }

   public Recipe withMetadata(String key, float value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setFloat(key, value);
      return this;
   }

   public Recipe withMetadata(String key, double value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setDouble(key, value);
      return this;
   }

   public Recipe withMetadata(String key, boolean value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setBoolean(key, value);
      return this;
   }

   public Recipe withNBT(boolean value) {
      this.useNBT = value;
      return this;
   }

   protected Recipe withOutput(ItemStack[] itemOutputs) {
      this.itemOutputs = itemOutputs;
      return this;
   }

   protected Recipe withOutput(FluidStack[] fluidOutputs) {
      this.fluidOutputs = fluidOutputs;
      return this;
   }

   protected Recipe withOutput(ItemStack[] itemOutputs, FluidStack[] fluidOutputs) {
      this.itemOutputs = itemOutputs;
      this.fluidOutputs = fluidOutputs;
      return this;
   }

   public void register() {
      this.register(false);
   }

   public void register(boolean replace) {
      boolean success = this.handler.addRecipe(this, replace);
      if (!success) {
         this.handler.logger.warn("Registration failed for input " + this);
      }

   }

   public List<InputIngredient<?>> getInputIngredients() {
      return this.inputIngredients;
   }

   public ItemStack[] getItemOutputs() {
      ItemStack[] ret = new ItemStack[this.itemOutputs.length];

      for(int index = 0; index < ret.length; ++index) {
         ret[index] = this.itemOutputs[index].copy();
      }

      return ret;
   }

   public FluidStack[] getFluidOutputs() {
      FluidStack[] ret = new FluidStack[this.fluidOutputs.length];

      for(int index = 0; index < ret.length; ++index) {
         ret[index] = this.fluidOutputs[index].copy();
      }

      return this.fluidOutputs;
   }

   public int getEnergyCostPerTick() {
      if (this.metadata == null) {
         return 0;
      } else {
         return this.metadata.hasKey("energyCostPerTick") ? this.metadata.getInteger("energyCostPerTick") : 0;
      }
   }

   public int getOperationDuration() {
      if (this.metadata == null) {
         return 0;
      } else {
         return this.metadata.hasKey("operationDuration") ? this.metadata.getInteger("operationDuration") : 0;
      }
   }

   public NBTTagCompound getMetadata() {
      return this.metadata;
   }

   public boolean shouldUseNBT() {
      return this.useNBT;
   }
}
