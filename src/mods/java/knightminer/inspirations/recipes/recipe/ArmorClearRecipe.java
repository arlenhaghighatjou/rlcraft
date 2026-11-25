package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ArmorClearRecipe implements ICauldronRecipe {
   private final ArmorMaterial material;

   public ArmorClearRecipe(ArmorMaterial material) {
      this.material = material;
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      if (level != 0 && state.isWater()) {
         Item item = stack.getItem();
         if (!(item instanceof ItemArmor)) {
            return false;
         } else {
            ItemArmor armor = (ItemArmor)item;
            return armor.getArmorMaterial() == this.material && armor.hasColor(stack);
         }
      } else {
         return false;
      }
   }

   public ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      stack = stack.copy();
      ((ItemArmor)stack.getItem()).removeColor(stack);
      return stack;
   }

   public int getLevel(int level) {
      return level - 1;
   }

   public ItemStack getContainer(ItemStack stack) {
      return ItemStack.EMPTY;
   }
}
