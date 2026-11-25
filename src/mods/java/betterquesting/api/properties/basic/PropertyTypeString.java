package betterquesting.api.properties.basic;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

public class PropertyTypeString extends PropertyTypeBase<String> {
   public PropertyTypeString(ResourceLocation key, String def) {
      super(key, def);
   }

   public String readValue(NBTBase nbt) {
      return nbt != null && nbt.getId() == 8 ? ((NBTTagString)nbt).getString() : (String)this.getDefault();
   }

   public NBTBase writeValue(String value) {
      return value == null ? new NBTTagString((String)this.getDefault()) : new NBTTagString(value);
   }
}
