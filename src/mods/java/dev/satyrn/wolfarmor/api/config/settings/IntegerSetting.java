package dev.satyrn.wolfarmor.api.config.settings;

import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.config.Property.Type;

public class IntegerSetting extends ValueSetting<Integer> {
   public IntegerSetting(Integer defaultValue) {
      super(defaultValue);
      this.setPropertyType(Type.INTEGER);
   }

   public Integer readTag(NBTBase tag) {
      return ((NBTTagInt)tag).getInt();
   }

   public NBTBase writeTag(Integer value) {
      return new NBTTagInt(value.intValue());
   }

   @Nonnull
   public Integer parse(String value) {
      return Integer.parseInt(value);
   }
}
