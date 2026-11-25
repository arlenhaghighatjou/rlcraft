package vazkii.quark.base.command;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraftforge.common.config.Property.Type;

public class ConfigPropertyDescriptor {
   @Nonnull
   public final String key;
   @Nonnull
   public final Type type;

   public ConfigPropertyDescriptor(@Nonnull String key, @Nonnull Type type) {
      this.key = key;
      this.type = type;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ConfigPropertyDescriptor that = (ConfigPropertyDescriptor)o;
         return Objects.equals(this.key, that.key) && this.type == that.type;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.key, this.type});
   }
}
