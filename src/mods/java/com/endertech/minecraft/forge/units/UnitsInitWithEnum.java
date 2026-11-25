package com.endertech.minecraft.forge.units;

import com.endertech.common.CommonPath;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.UnitConfig;
import java.nio.file.Path;
import net.minecraft.util.IStringSerializable;

public abstract class UnitsInitWithEnum<T, E extends Enum<?> & IForgeEnum> extends UnitsInit<T> {
   public UnitsInitWithEnum(ForgeMod mod, Class<E> builtIn) {
      super(mod);

      for(E enumValue : (Enum[])builtIn.getEnumConstants()) {
         UnitConfig config = new UnitConfig(this.getConfigsDir(), ((IStringSerializable)enumValue).getName());
         T unit = (T)this.createDefaultUnitFrom(config, enumValue);
         this.add(unit);
         config.save();
      }

      for(Path path : UnitConfig.listCustomConfigs(this.getConfigsDir(), builtIn)) {
         UnitConfig config = new UnitConfig(path.toFile());
         String name = CommonPath.getFileNameOnly(path);
         T unit = (T)this.createCustomUnitFrom(config, name);
         this.add(unit);
         config.save();
      }

   }

   public abstract T createDefaultUnitFrom(UnitConfig var1, E var2);

   public abstract T createCustomUnitFrom(UnitConfig var1, String var2);
}
