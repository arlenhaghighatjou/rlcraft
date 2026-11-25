package mezz.jei.recipes;

import com.google.common.base.Stopwatch;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Log;

public class RecipeRegistryPluginSafeWrapper implements IRecipeRegistryPlugin {
   private final IRecipeRegistryPlugin plugin;
   private final Stopwatch stopWatch = Stopwatch.createUnstarted();

   public RecipeRegistryPluginSafeWrapper(IRecipeRegistryPlugin plugin) {
      this.plugin = plugin;
   }

   public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
      return (List)this.callPluginMethod(() -> {
         return this.plugin.getRecipeCategoryUids(focus);
      }, Collections.emptyList());
   }

   public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
      return (List)this.callPluginMethod(() -> {
         return this.plugin.getRecipeWrappers(recipeCategory, focus);
      }, Collections.emptyList());
   }

   public <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory) {
      return (List)this.callPluginMethod(() -> {
         return this.plugin.<T>getRecipeWrappers(recipeCategory);
      }, Collections.emptyList());
   }

   private <T> T callPluginMethod(Supplier<T> supplier, T defaultValue) {
      try {
         this.stopWatch.reset();
         this.stopWatch.start();
         T result = (T)supplier.get();
         this.stopWatch.stop();
         if (this.stopWatch.elapsed(TimeUnit.MILLISECONDS) > 10L) {
            Log.get().warn("Recipe registry plugin is slow, took {}. {}", this.stopWatch, this.plugin.getClass());
         }

         return result;
      } catch (LinkageError | RuntimeException var4) {
         Log.get().error("Recipe registry plugin crashed: {}", this.plugin.getClass(), var4);
         return defaultValue;
      }
   }
}
