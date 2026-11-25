package svenhjol.meson.iface;

import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.util.IStringSerializable;

public interface IMesonEnum extends IStringSerializable {
   @Nonnull
   default String getName() {
      return ((Enum)this).name().toLowerCase(Locale.ENGLISH);
   }

   default String getCapitalizedName() {
      String name = this.getName();
      return name.substring(0, 1).toUpperCase() + name.substring(1);
   }
}
