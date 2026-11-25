package snownee.kiwi.crafting.input;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface ProcessingInput {
   @Nonnull
   List<ItemStack> examples();

   boolean matches(@Nonnull ItemStack var1);

   boolean isEmpty();

   boolean equals(Object var1);

   int count();
}
