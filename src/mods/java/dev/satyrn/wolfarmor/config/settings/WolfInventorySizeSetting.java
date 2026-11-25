package dev.satyrn.wolfarmor.config.settings;

import dev.satyrn.wolfarmor.api.config.settings.ValueSetting;
import dev.satyrn.wolfarmor.util.WolfInventorySize;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraftforge.common.config.Property.Type;

public class WolfInventorySizeSetting extends ValueSetting<WolfInventorySize> {
   public WolfInventorySizeSetting(WolfInventorySize defaultValue) {
      super(defaultValue);
      this.setPropertyType(Type.STRING);
   }

   public WolfInventorySize readTag(NBTBase tag) {
      WolfInventorySize wolfInvSize = new WolfInventorySize();
      wolfInvSize.deserializeNBT((NBTTagByteArray)tag);
      return wolfInvSize;
   }

   public NBTBase writeTag(WolfInventorySize value) {
      return value.serializeNBT();
   }

   @Nonnull
   public WolfInventorySize parse(String value) {
      return WolfInventorySize.parseWolfInventorySize(value);
   }
}
