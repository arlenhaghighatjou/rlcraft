package pl.asie.foamfix.coremod.patches.jei;

import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import pl.asie.foamfix.FoamFix;
import pl.asie.foamfix.client.jei.SearchTreeJEIItems;
import pl.asie.foamfix.shared.FoamFixShared;

public class SearchTreeJEIManagerInject implements IResourceManagerReloadListener {
   public SearchTreeJEIManagerInject() {
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      if (FoamFixShared.config.clJeiCreativeSearch && this == Minecraft.getMinecraft().getSearchTreeManager() && SearchTreeJEIPatchGlue.isValid()) {
         try {
            Field f = ReflectionHelper.findField(SearchTreeManager.class, "trees", "trees");
            Map m = (Map)f.get(Minecraft.getMinecraft().getSearchTreeManager());
            m.put(SearchTreeManager.ITEMS, new SearchTreeJEIItems());
            FoamFix.getLogger().info("JEI search tree manager injection complete!");
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

      this.onResourceManagerReload_foamfix_old(resourceManager);
   }

   public void onResourceManagerReload_foamfix_old(IResourceManager resourceManager) {
      Minecraft.getMinecraft().getSearchTreeManager().onResourceManagerReload(resourceManager);
   }
}
