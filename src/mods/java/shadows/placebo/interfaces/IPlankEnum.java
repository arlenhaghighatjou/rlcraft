package shadows.placebo.interfaces;

import net.minecraft.item.ItemStack;

public interface IPlankEnum extends IPropertyEnum {
   ItemStack genLogStack();

   boolean isNether();
}
