package mezz.jei.api;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ISubtypeRegistry {
   void useNbtForSubtypes(Item... var1);

   /** @deprecated */
   @Deprecated
   void registerNbtInterpreter(Item var1, ISubtypeRegistry.ISubtypeInterpreter var2);

   void registerSubtypeInterpreter(Item var1, ISubtypeRegistry.ISubtypeInterpreter var2);

   @Nullable
   String getSubtypeInfo(ItemStack var1);

   boolean hasSubtypeInterpreter(ItemStack var1);

   @FunctionalInterface
   public interface ISubtypeInterpreter extends Function<ItemStack, String> {
      String NONE = "";

      String apply(ItemStack var1);

      /** @deprecated */
      @Deprecated
      @Nullable
      default String getSubtypeInfo(ItemStack itemStack) {
         return this.apply(itemStack);
      }
   }
}
