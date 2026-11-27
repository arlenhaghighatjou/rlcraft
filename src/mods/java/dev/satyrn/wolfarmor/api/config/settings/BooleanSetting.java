package dev.satyrn.wolfarmor.api.config.settings;

import net.lax1dude.eaglercraft.v1_8.HString;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraftforge.common.config.Property.Type;

public class BooleanSetting extends ValueSetting<Boolean> {
   public BooleanSetting(boolean defaultValue) {
      super(Boolean.valueOf(defaultValue));
      this.setPropertyType(Type.BOOLEAN);
   }

   public Boolean readTag(NBTBase tag) {
      return ((NBTTagByte)tag).getByte() != 0;
   }

   public NBTBase writeTag(Boolean value) {
      return new NBTTagByte((byte)(((Boolean)this.getValue()).booleanValue() ? 1 : 0));
   }

   @Nonnull
   public Boolean parse(String value) {
      String var2 = value.toLowerCase();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case 3569038:
         if (var2.equals("true")) {
            var3 = 0;
         }
         break;
      case 97196323:
         if (var2.equals("false")) {
            var3 = 1;
         }
      }

      switch(var3) {
      case 0:
         return true;
      case 1:
         return false;
      default:
         throw new IllegalArgumentException(HString.format("The string '%s' is not a valid boolean.", value));
      }
   }
}
