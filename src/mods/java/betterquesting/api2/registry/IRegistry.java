package betterquesting.api2.registry;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

/** @deprecated */
@Deprecated
public interface IRegistry<T extends IFactory<E>, E> {
   void register(T var1);

   T getFactory(ResourceLocation var1);

   @Nullable
   E createNew(ResourceLocation var1);

   List<T> getAll();
}
