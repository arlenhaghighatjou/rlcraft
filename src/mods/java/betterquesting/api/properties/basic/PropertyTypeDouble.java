package betterquesting.api.properties.basic;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.util.ResourceLocation;

public class PropertyTypeDouble extends PropertyTypeBase<Double> {
   public PropertyTypeDouble(ResourceLocation key, Double def) {
      super(key, def);
   }

   public Double readValue(NBTBase nbt) {
      return nbt != null && nbt instanceof NBTPrimitive ? ((NBTPrimitive)nbt).getDouble() : (Double)this.getDefault();
   }

   public NBTBase writeValue(Double value) {
      return value == null ? new NBTTagDouble(((Double)this.getDefault()).doubleValue()) : new NBTTagDouble(value.doubleValue());
   }
}
