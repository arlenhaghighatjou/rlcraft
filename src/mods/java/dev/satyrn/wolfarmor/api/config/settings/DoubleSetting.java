package dev.satyrn.wolfarmor.api.config.settings;

import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraftforge.common.config.Property.Type;

public class DoubleSetting extends ValueSetting<Double> {
   public DoubleSetting(Double defaultValue) {
      super(defaultValue);
      this.setPropertyType(Type.DOUBLE);
   }

   public Double readTag(NBTBase tag) {
      return ((NBTTagDouble)tag).getDouble();
   }

   public NBTBase writeTag(Double value) {
      return new NBTTagDouble(value.doubleValue());
   }

   @Nonnull
   public Double parse(String value) {
      return Double.parseDouble(value);
   }
}
