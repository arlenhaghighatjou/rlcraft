package betterquesting.api.properties.basic;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.ResourceLocation;

public class PropertyTypeBoolean extends PropertyTypeBase<Boolean> {
   public PropertyTypeBoolean(ResourceLocation key, Boolean def) {
      super(key, def);
   }

   public Boolean readValue(NBTBase nbt) {
      if (nbt != null && nbt.getId() >= 1 && nbt.getId() <= 6) {
         try {
            return ((NBTPrimitive)nbt).getByte() > 0;
         } catch (Exception var3) {
            return (Boolean)this.getDefault();
         }
      } else {
         return (Boolean)this.getDefault();
      }
   }

   public NBTBase writeValue(Boolean value) {
      return value == null ? new NBTTagByte((byte)(((Boolean)this.getDefault()).booleanValue() ? 1 : 0)) : new NBTTagByte((byte)(value.booleanValue() ? 1 : 0));
   }
}
