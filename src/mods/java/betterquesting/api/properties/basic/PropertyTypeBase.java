package betterquesting.api.properties.basic;

import betterquesting.api.properties.IPropertyType;
import net.minecraft.util.ResourceLocation;

public abstract class PropertyTypeBase<T> implements IPropertyType<T> {
   private final ResourceLocation key;
   private final T def;

   public PropertyTypeBase(ResourceLocation key, T def) {
      this.key = key;
      this.def = def;
   }

   public ResourceLocation getKey() {
      return this.key;
   }

   public T getDefault() {
      return this.def;
   }
}
