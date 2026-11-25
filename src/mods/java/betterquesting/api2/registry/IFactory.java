package betterquesting.api2.registry;

import net.minecraft.util.ResourceLocation;

/** @deprecated */
@Deprecated
public interface IFactory<T> {
   ResourceLocation getRegistryName();

   T createNew();
}
