package betterquesting.api.properties.basic;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;

public class PropertyTypeInteger extends PropertyTypeBase<Integer> {
   public PropertyTypeInteger(ResourceLocation key, Integer def) {
      super(key, def);
   }

   public Integer readValue(NBTBase nbt) {
      return nbt != null && nbt instanceof NBTPrimitive ? ((NBTPrimitive)nbt).getInt() : (Integer)this.getDefault();
   }

   public NBTBase writeValue(Integer value) {
      return value == null ? new NBTTagInt(((Integer)this.getDefault()).intValue()) : new NBTTagInt(value.intValue());
   }
}
