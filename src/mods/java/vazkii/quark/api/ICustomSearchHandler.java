package vazkii.quark.api;

import net.minecraft.item.ItemStack;

public interface ICustomSearchHandler {
   boolean stackMatchesSearchQuery(ItemStack var1, String var2, ICustomSearchHandler.StringMatcher var3, ICustomSearchHandler.SearchMethod var4);

   public interface SearchMethod {
      boolean namesMatch(ItemStack var1, String var2);
   }

   public interface StringMatcher {
      boolean matches(String var1, String var2);
   }
}
