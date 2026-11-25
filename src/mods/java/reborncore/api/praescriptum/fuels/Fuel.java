package reborncore.api.praescriptum.fuels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
import reborncore.common.util.ItemUtils;

public class Fuel {
   private FuelHandler handler;
   private List<InputIngredient<?>> inputIngredients = new ArrayList();
   private double energyOutput = 0.0D;
   private NBTTagCompound metadata;

   public Fuel(FuelHandler handler) {
      this.handler = handler;
   }

   public Fuel addSources(Collection<InputIngredient<?>> inputs) {
      this.inputIngredients.addAll(inputs);
      return this;
   }

   public Fuel addItemSource(ItemStack itemStack) {
      if (ItemUtils.isEmpty(itemStack)) {
         throw new IllegalArgumentException("Source cannot be empty");
      } else {
         this.inputIngredients.add(ItemStackInputIngredient.of(itemStack));
         return this;
      }
   }

   public Fuel addItemSource(String oreDict) {
      this.inputIngredients.add(OreDictionaryInputIngredient.of(oreDict));
      return this;
   }

   public Fuel addItemSource(String oreDict, int amount) {
      if (amount <= 0) {
         throw new IllegalArgumentException("Source cannot be empty");
      } else {
         this.inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount));
         return this;
      }
   }

   public Fuel addItemSource(String oreDict, int amount, Integer meta) {
      if (amount <= 0) {
         throw new IllegalArgumentException("Source cannot be empty");
      } else {
         this.inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount, meta));
         return this;
      }
   }

   public Fuel addFluidSource(FluidStack fluidStack) {
      if (fluidStack.amount <= 0) {
         throw new IllegalArgumentException("Source cannot be empty");
      } else {
         this.inputIngredients.add(FluidStackInputIngredient.of(fluidStack));
         return this;
      }
   }

   public Fuel addFluidSource(Fluid fluid) {
      return this.addFluidSource(fluid, 1);
   }

   public Fuel addFluidSource(Fluid fluid, int amount) {
      if (amount <= 0) {
         throw new IllegalArgumentException("Source cannot be empty");
      } else {
         this.inputIngredients.add(FluidStackInputIngredient.of(new FluidStack(fluid, amount)));
         return this;
      }
   }

   public Fuel withEnergyOutput(double energyOutput) {
      if (energyOutput < 0.0D) {
         throw new IllegalArgumentException("Energy output cannot be less than 0.0D");
      } else {
         this.energyOutput = energyOutput;
         return this;
      }
   }

   public Fuel withEnergyPerTick(double energyPerTick) {
      if (energyPerTick < 0.0D) {
         throw new IllegalArgumentException("Energy per tick cannot be less than 0.0D");
      } else {
         if (this.metadata == null) {
            this.metadata = new NBTTagCompound();
         }

         this.metadata.setDouble("energyPerTick", energyPerTick);
         return this;
      }
   }

   public Fuel withMetadata(NBTTagCompound metadata) {
      this.metadata = metadata;
      return this;
   }

   public Fuel withMetadata(String key, int value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setInteger(key, value);
      return this;
   }

   public Fuel withMetadata(String key, short value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setShort(key, value);
      return this;
   }

   public Fuel withMetadata(String key, byte value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setByte(key, value);
      return this;
   }

   public Fuel withMetadata(String key, float value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setFloat(key, value);
      return this;
   }

   public Fuel withMetadata(String key, double value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setDouble(key, value);
      return this;
   }

   public Fuel withMetadata(String key, boolean value) {
      if (this.metadata == null) {
         this.metadata = new NBTTagCompound();
      }

      this.metadata.setBoolean(key, value);
      return this;
   }

   public void register() {
      this.register(false);
   }

   public void register(boolean replace) {
      boolean success = this.handler.addFuel(this, replace);
      if (!success) {
         this.handler.logger.warn("Registration failed for input " + this);
      }

   }

   public List<InputIngredient<?>> getInputIngredients() {
      return this.inputIngredients;
   }

   public double getEnergyOutput() {
      return this.energyOutput;
   }

   public double getEnergyPerTick() {
      if (this.metadata == null) {
         return 0.0D;
      } else {
         return this.metadata.hasKey("energyPerTick") ? this.metadata.getDouble("energyPerTick") : 0.0D;
      }
   }

   public NBTTagCompound getMetadata() {
      return this.metadata;
   }

   public static Fuel readFromNBT(FuelHandler fuelHandler, NBTTagCompound tag) {
      Fuel ret = fuelHandler.addFuel();
      NBTTagList inputIngredients = tag.getTagList("inputIngredients", 10);

      for(int index = 0; index < inputIngredients.tagCount(); ++index) {
         NBTTagCompound inputIngredient = inputIngredients.getCompoundTagAt(index);
         byte type = inputIngredient.getByte("type");
         switch(type) {
         case 0:
            ItemStack itemStack = new ItemStack(inputIngredient);
            inputIngredient.getBoolean("consumable");
            ret.addItemSource(itemStack);
            break;
         case 1:
            String oreDictionaryEntry = inputIngredient.getString("oreDictionaryEntry");
            int amount = inputIngredient.getInteger("amount");
            int meta = inputIngredient.getInteger("meta");
            inputIngredient.getBoolean("consumable");
            ret.addItemSource(oreDictionaryEntry, amount);
            break;
         case 2:
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(inputIngredient);
            inputIngredient.getBoolean("consumable");
            ret.addFluidSource((FluidStack)Objects.requireNonNull(fluidStack));
         }
      }

      ret.withEnergyOutput(tag.getDouble("energyOutput"));
      if (tag.hasKey("metadata")) {
         ret.withMetadata(tag.getCompoundTag("metadata"));
      }

      return ret;
   }

   public static NBTTagCompound writeToNBT(Fuel fuel) {
      NBTTagCompound ret = new NBTTagCompound();
      NBTTagList inputIngredients = new NBTTagList();

      for(InputIngredient<?> inputIngredient : fuel.inputIngredients) {
         if (inputIngredient instanceof ItemStackInputIngredient) {
            NBTTagCompound temp = new NBTTagCompound();
            temp.setByte("type", (byte)0);
            ((ItemStack)((ItemStackInputIngredient)inputIngredient).ingredient).writeToNBT(temp);
            temp.setBoolean("consumable", inputIngredient.consumable);
            inputIngredients.appendTag(temp);
         } else if (inputIngredient instanceof OreDictionaryInputIngredient) {
            NBTTagCompound temp = new NBTTagCompound();
            temp.setByte("type", (byte)1);
            temp.setString("oreDictionaryEntry", (String)((OreDictionaryInputIngredient)inputIngredient).ingredient);
            temp.setInteger("amount", ((OreDictionaryInputIngredient)inputIngredient).amount);
            temp.setInteger("meta", ((OreDictionaryInputIngredient)inputIngredient).meta.intValue());
            temp.setBoolean("consumable", inputIngredient.consumable);
            inputIngredients.appendTag(temp);
         } else if (inputIngredient instanceof FluidStackInputIngredient) {
            NBTTagCompound temp = new NBTTagCompound();
            temp.setByte("type", (byte)2);
            ((FluidStack)((FluidStackInputIngredient)inputIngredient).ingredient).writeToNBT(temp);
            temp.setBoolean("consumable", inputIngredient.consumable);
            inputIngredients.appendTag(temp);
         }
      }

      ret.setTag("inputIngredients", inputIngredients);
      ret.setDouble("energyOutput", fuel.energyOutput);
      if (fuel.metadata.hasNoTags()) {
         ret.setTag("metadata", fuel.metadata);
      }

      return ret;
   }
}
