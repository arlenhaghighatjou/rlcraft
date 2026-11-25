package betterquesting.api.properties.basic;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

public class PropertyTypeEnum<E extends Enum<E>> extends PropertyTypeBase<E> {
   private final Class<E> eClazz;

   public PropertyTypeEnum(ResourceLocation key, E def) {
      super(key, def);
      this.eClazz = def.getDeclaringClass();
   }

   public E readValue(NBTBase nbt) {
      if (nbt != null && nbt.getId() == 8) {
         try {
            return (E)Enum.valueOf(this.eClazz, ((NBTTagString)nbt).getString());
         } catch (Exception var3) {
            return (E)(this.getDefault());
         }
      } else {
         return (E)(this.getDefault());
      }
   }

   public NBTBase writeValue(E value) {
      return value == null ? new NBTTagString(((Enum)this.getDefault()).toString()) : new NBTTagString(value.toString());
   }
}
