package pl.asie.foamfix.client.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JEIPlugin;
import pl.asie.foamfix.coremod.patches.jei.SearchTreeJEIPatchGlue;

@JEIPlugin
public class SearchTreeJEIPlugin implements IModPlugin {
   public SearchTreeJEIPlugin() {
   }

   public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
      SearchTreeJEIPatchGlue.src = jeiRuntime.getIngredientFilter();
   }
}
