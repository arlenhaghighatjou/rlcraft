package mezz.jei.plugins.vanilla.ingredients.enchant;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import java.awt.Color;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantDataHelper implements IIngredientHelper<EnchantmentData> {
   private final EnchantedBookCache cache;
   private final IIngredientHelper<ItemStack> itemStackHelper;

   public EnchantDataHelper(EnchantedBookCache cache, IIngredientHelper<ItemStack> itemStackHelper) {
      this.cache = cache;
      this.itemStackHelper = itemStackHelper;
   }

   @Nullable
   public EnchantmentData getMatch(Iterable<EnchantmentData> ingredients, EnchantmentData toMatch) {
      for(EnchantmentData enchantData : ingredients) {
         if (enchantData.enchantment.getRegistryName() == toMatch.enchantment.getRegistryName() && enchantData.enchantmentLevel == toMatch.enchantmentLevel) {
            return enchantData;
         }
      }

      return null;
   }

   public IFocus<?> translateFocus(IFocus<EnchantmentData> focus, IIngredientHelper.IFocusFactory focusFactory) {
      EnchantmentData enchantData = focus.getValue();
      ItemStack itemStack = this.cache.getEnchantedBook(enchantData);
      return focusFactory.createFocus(focus.getMode(), itemStack);
   }

   public String getDisplayName(EnchantmentData ingredient) {
      return ingredient.enchantment.getTranslatedName(ingredient.enchantmentLevel);
   }

   public String getUniqueId(EnchantmentData ingredient) {
      return "enchantment:" + ingredient.enchantment.getName() + ".lvl" + ingredient.enchantmentLevel;
   }

   public String getWildcardId(EnchantmentData ingredient) {
      return this.getUniqueId(ingredient);
   }

   public String getModId(EnchantmentData ingredient) {
      ResourceLocation registryName = ingredient.enchantment.getRegistryName();
      if (registryName == null) {
         String stackInfo = this.getErrorInfo(ingredient);
         throw new IllegalStateException("enchantment.getRegistryName() returned null for: " + stackInfo);
      } else {
         return registryName.getResourceDomain();
      }
   }

   public String getDisplayModId(EnchantmentData ingredient) {
      ItemStack enchantedBook = this.cache.getEnchantedBook(ingredient);
      return this.itemStackHelper.getDisplayModId(enchantedBook);
   }

   public Iterable<Color> getColors(EnchantmentData ingredient) {
      ItemStack enchantedBook = this.cache.getEnchantedBook(ingredient);
      return this.itemStackHelper.getColors(enchantedBook);
   }

   public String getResourceId(EnchantmentData ingredient) {
      ResourceLocation registryName = ingredient.enchantment.getRegistryName();
      if (registryName == null) {
         String stackInfo = this.getErrorInfo(ingredient);
         throw new IllegalStateException("enchantment.getRegistryName() returned null for: " + stackInfo);
      } else {
         return registryName.getResourcePath();
      }
   }

   public ItemStack getCheatItemStack(EnchantmentData ingredient) {
      return this.cache.getEnchantedBook(ingredient);
   }

   public EnchantmentData copyIngredient(EnchantmentData ingredient) {
      return new EnchantmentData(ingredient.enchantment, ingredient.enchantmentLevel);
   }

   public boolean isIngredientOnServer(EnchantmentData ingredient) {
      ItemStack enchantedBook = this.cache.getEnchantedBook(ingredient);
      return this.itemStackHelper.isIngredientOnServer(enchantedBook);
   }

   public String getErrorInfo(@Nullable EnchantmentData ingredient) {
      if (ingredient == null) {
         return "null";
      } else {
         ToStringHelper toStringHelper = MoreObjects.toStringHelper(EnchantmentData.class);
         toStringHelper.add("Enchantment", ingredient.enchantment.getName());
         toStringHelper.add("Level", ingredient.enchantmentLevel);
         return toStringHelper.toString();
      }
   }
}
