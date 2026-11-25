package dev.satyrn.wolfarmor.api.config.settings;

import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.Property.Type;

public class StringSetting extends ValueSetting<String> {
   public StringSetting(String defaultValue) {
      super(defaultValue);
      this.setPropertyType(Type.STRING);
   }

   public String readTag(NBTBase tag) {
      return ((NBTTagString)tag).getString();
   }

   public NBTBase writeTag(String value) {
      return new NBTTagString(value);
   }

   @Nonnull
   public String parse(String value) {
      return value;
   }
}
