package svenhjol.charm.base.integration.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import svenhjol.charm.Charm;
import svenhjol.charm.brewing.feature.FlavoredCake;
import svenhjol.charm.crafting.feature.Composter;
import svenhjol.charm.crafting.feature.Crate;
import svenhjol.charm.tweaks.feature.CompassBinding;
import svenhjol.charm.world.feature.Moonstone;
import svenhjol.meson.helper.ItemHelper;

@JEIPlugin
public class CharmJeiPlugin implements IModPlugin {
   private ComposterCategory composter;

   public CharmJeiPlugin() {
   }

   public void registerCategories(IRecipeCategoryRegistration registry) {
      this.composter = new ComposterCategory(registry.getJeiHelpers().getGuiHelper());
      registry.addRecipeCategories(new IRecipeCategory[]{this.composter});
   }

   public void register(IModRegistry registry) {
      List<String> composterOutputs = Composter.outputs;
      List<ItemStack> outputs = new ArrayList();
      composterOutputs.forEach((name) -> {
         outputs.addAll(ItemHelper.getItemStacksFromItemString(name));
      });
      Map<Float, List<String>> composterInputs = Composter.getInputsByChance();
      Map<Integer, Map<Float, List<ItemStack>>> inputs = new TreeMap();
      Float[] chances = (Float[])composterInputs.keySet().toArray(new Float[0]);
      Arrays.sort(chances);
      int page = 0;

      for(final Float chance : chances) {
         int i = 0;

         for(String itemName : (List)composterInputs.get(chance)) {
            for(ItemStack stack : ItemHelper.getItemStacksFromItemString(itemName)) {
               if (!inputs.containsKey(Integer.valueOf(page))) {
                  inputs.put(Integer.valueOf(page), new HashMap<Float, List<ItemStack>>() {
                     {
                        this.put(chance, new ArrayList());
                     }
                  });
               }

               ((List)((Map)inputs.get(Integer.valueOf(page))).get(chance)).add(stack);
               ++i;
               page += i % 36 == 0 ? 1 : 0;
            }
         }

         ++page;
      }

      registry.addRecipeCatalyst(new ItemStack(Composter.composter), new String[]{this.composter.getUid()});
      registry.addRecipes((Collection)inputs.keySet().stream().map((p) -> {
         Entry<Float, List<ItemStack>> entry = (Entry)((Map)inputs.get(p)).entrySet().iterator().next();
         return new ComposterRecipe((List)entry.getValue(), outputs, ((Float)entry.getKey()).floatValue());
      }).collect(Collectors.toList()), this.composter.getUid());
      if (Charm.hasFeature(FlavoredCake.class)) {
         List<ItemStack> cakeItems = new ArrayList();
         FlavoredCake.cakes.values().forEach((cake) -> {
            cakeItems.add(new ItemStack(cake));
         });
         registry.addIngredientInfo(cakeItems, VanillaTypes.ITEM, new String[]{"charm.jei.flavoredcake.desc"});
      }

      if (Charm.hasFeature(CompassBinding.class)) {
         registry.addIngredientInfo(new ItemStack(CompassBinding.boundCompass), VanillaTypes.ITEM, new String[]{"charm.jei.boundcompass.desc"});
      }

      if (Charm.hasFeature(Crate.class)) {
         List<ItemStack> crates = new ArrayList();

         for(int i = 0; i < 6; ++i) {
            crates.addAll(Arrays.asList(new ItemStack(Crate.crate, 1, i), new ItemStack(Crate.crateSealed, 1, i)));
         }

         registry.addIngredientInfo(crates, VanillaTypes.ITEM, new String[]{"charm.jei.crates.desc"});
      }

      if (Charm.hasFeature(Moonstone.class)) {
         List<ItemStack> moonstones = new ArrayList();

         for(int i = 0; i < 16; ++i) {
            moonstones.add(new ItemStack(Moonstone.moonstone, 1, i));
         }

         registry.addIngredientInfo(moonstones, VanillaTypes.ITEM, new String[]{"charm.jei.moonstone.desc"});
      }

   }
}
