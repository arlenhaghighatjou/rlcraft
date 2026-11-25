package mezz.jei.plugins.vanilla.ingredients.enchant;

import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;

public class EnchantDataRenderer implements IIngredientRenderer<EnchantmentData> {
   private final IIngredientRenderer<ItemStack> itemRenderer;
   private final EnchantedBookCache cache;

   public EnchantDataRenderer(IIngredientRenderer<ItemStack> itemRenderer, EnchantedBookCache cache) {
      this.itemRenderer = itemRenderer;
      this.cache = cache;
   }

   public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable EnchantmentData ingredient) {
      if (ingredient != null) {
         ItemStack enchantBook = this.cache.getEnchantedBook(ingredient);
         this.itemRenderer.render(minecraft, xPosition, yPosition, enchantBook);
      }

   }

   public List<String> getTooltip(Minecraft minecraft, EnchantmentData ingredient, ITooltipFlag tooltipFlag) {
      ItemStack enchantBook = this.cache.getEnchantedBook(ingredient);
      return this.itemRenderer.getTooltip(minecraft, enchantBook, tooltipFlag);
   }

   public FontRenderer getFontRenderer(Minecraft minecraft, EnchantmentData ingredient) {
      ItemStack enchantBook = this.cache.getEnchantedBook(ingredient);
      return this.itemRenderer.getFontRenderer(minecraft, enchantBook);
   }
}
