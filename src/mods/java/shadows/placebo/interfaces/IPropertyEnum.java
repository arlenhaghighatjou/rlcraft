package shadows.placebo.interfaces;

import java.util.Locale;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IPropertyEnum extends IStringSerializable {
   default String getName() {
      return ((Enum)this).name().toLowerCase(Locale.ENGLISH);
   }

   default int getPredicateIndex() {
      return ((Enum)this).ordinal() / 16;
   }

   default boolean useForRecipes() {
      return false;
   }

   default int getMetadata() {
      return ((Enum)this).ordinal() % 16;
   }

   ItemStack get();

   default ItemStack get(int size) {
      ItemStack s = this.get();
      s.setCount(size);
      return s;
   }

   default void set(IForgeRegistryEntry<?> thing) {
   }
}
