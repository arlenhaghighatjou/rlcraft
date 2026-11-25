package cursedflames.bountifulbaubles.integration;

import cursedflames.bountifulbaubles.item.ModItems;
import cursedflames.bountifulbaubles.recipe.AnvilRecipes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

@JEIPlugin
public class JeiIntegration implements IModPlugin {
   public JeiIntegration() {
   }

   public void register(IModRegistry registry) {
      List<IRecipeWrapper> recipes = new ArrayList();

      for(Pair<Item, Item> ingredients : AnvilRecipes.simpleRecipes.keySet()) {
         Pair<Integer, ItemStack> result = (Pair)AnvilRecipes.simpleRecipes.get(ingredients);
         recipes.add(registry.getJeiHelpers().getVanillaRecipeFactory().createAnvilRecipe(new ItemStack((Item)ingredients.getLeft()), Arrays.asList(new ItemStack((Item)ingredients.getRight())), Arrays.asList((ItemStack)result.getRight())));
      }

      recipes.add(registry.getJeiHelpers().getVanillaRecipeFactory().createAnvilRecipe(new ItemStack(ModItems.shieldCobalt), Arrays.asList(new ItemStack(ModItems.trinketObsidianSkull)), Arrays.asList(new ItemStack(ModItems.shieldObsidian))));
      recipes.add(registry.getJeiHelpers().getVanillaRecipeFactory().createAnvilRecipe(new ItemStack(ModItems.shieldObsidian), Arrays.asList(new ItemStack(ModItems.trinketAnkhCharm)), Arrays.asList(new ItemStack(ModItems.shieldAnkh))));
      registry.addRecipes(recipes, "minecraft.anvil");
   }
}
