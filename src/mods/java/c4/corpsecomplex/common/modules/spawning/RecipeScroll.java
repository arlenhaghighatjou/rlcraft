package c4.corpsecomplex.common.modules.spawning;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeScroll extends ShapelessOreRecipe {
   public RecipeScroll() {
      super((ResourceLocation)null, NonNullList.from(Ingredient.EMPTY, new Ingredient[]{Ingredient.fromItem(Items.PAPER), Ingredient.fromItem(Items.ENDER_PEARL), Ingredient.fromItem(Items.ROTTEN_FLESH)}), new ItemStack(SpawningModule.scroll));
   }
}
