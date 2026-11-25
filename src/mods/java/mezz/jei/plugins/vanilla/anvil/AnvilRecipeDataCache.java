package mezz.jei.plugins.vanilla.anvil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class AnvilRecipeDataCache {
   private static final LoadingCache<AnvilRecipeWrapper, AnvilRecipeDisplayData> cachedDisplayData = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<AnvilRecipeWrapper, AnvilRecipeDisplayData>() {
      public AnvilRecipeDisplayData load(AnvilRecipeWrapper key) {
         return new AnvilRecipeDisplayData();
      }
   });

   public AnvilRecipeDataCache() {
   }

   public static AnvilRecipeDisplayData getDisplayData(AnvilRecipeWrapper recipeWrapper) {
      return (AnvilRecipeDisplayData)cachedDisplayData.getUnchecked(recipeWrapper);
   }
}
