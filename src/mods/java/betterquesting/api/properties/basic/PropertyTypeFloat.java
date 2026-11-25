package betterquesting.api.properties.basic;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.ResourceLocation;

public class PropertyTypeFloat extends PropertyTypeBase<Float> {
   public PropertyTypeFloat(ResourceLocation key, Float def) {
      super(key, def);
   }

   public Float readValue(NBTBase nbt) {
      return nbt != null && nbt instanceof NBTPrimitive ? ((NBTPrimitive)nbt).getFloat() : (Float)this.getDefault();
   }

   public NBTBase writeValue(Float value) {
      return value == null ? new NBTTagFloat(((Float)this.getDefault()).floatValue()) : new NBTTagFloat(value.floatValue());
   }
}
