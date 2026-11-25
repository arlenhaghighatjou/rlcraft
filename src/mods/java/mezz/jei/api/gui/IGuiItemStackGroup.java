package mezz.jei.api.gui;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public interface IGuiItemStackGroup extends IGuiIngredientGroup<ItemStack> {
   void init(int var1, boolean var2, int var3, int var4);

   void set(int var1, @Nullable ItemStack var2);

   void addTooltipCallback(ITooltipCallback<ItemStack> var1);
}
