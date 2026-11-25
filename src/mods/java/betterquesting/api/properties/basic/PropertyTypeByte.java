package betterquesting.api.properties.basic;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.ResourceLocation;

public class PropertyTypeByte extends PropertyTypeBase<Byte> {
   public PropertyTypeByte(ResourceLocation key, Byte def) {
      super(key, def);
   }

   public Byte readValue(NBTBase nbt) {
      return nbt != null && nbt instanceof NBTPrimitive ? ((NBTPrimitive)nbt).getByte() : (Byte)this.getDefault();
   }

   public NBTBase writeValue(Byte value) {
      return value == null ? new NBTTagByte(((Byte)this.getDefault()).byteValue()) : new NBTTagByte(value.byteValue());
   }
}
