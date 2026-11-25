package betterquesting.api.properties.basic;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.ResourceLocation;

public class PropertyTypeLong extends PropertyTypeBase<Long> {
   public PropertyTypeLong(ResourceLocation key, Long def) {
      super(key, def);
   }

   public Long readValue(NBTBase nbt) {
      return nbt != null && nbt instanceof NBTPrimitive ? ((NBTPrimitive)nbt).getLong() : (Long)this.getDefault();
   }

   public NBTBase writeValue(Long value) {
      return value == null ? new NBTTagLong(((Long)this.getDefault()).longValue()) : new NBTTagLong(value.longValue());
   }
}
