package crafttweaker.mc1120.proxies;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.events.ClientEventHandler;
import crafttweaker.mc1120.game.MCGame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerEvents() {
      super.registerEvents();
      MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
   }

   public void registerReloadListener() {
      super.registerReloadListener();
      IReloadableResourceManager manager = (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager();
      manager.registerReloadListener((resourceManager, resourcePredicate) -> {
         if (resourcePredicate.test(VanillaResourceType.LANGUAGES)) {
            for(IAction action : MCGame.TRANSLATION_ACTIONS) {
               CraftTweakerAPI.apply(action);
            }
         }

      });
   }

   public void fixRecipeBook() {
      if (!Loader.isModLoaded("norecipebook")) {
         Minecraft minecraft = Minecraft.getMinecraft();
         if (!CraftTweaker.alreadyChangedThePlayer) {
            CraftTweaker.alreadyChangedThePlayer = true;
            RecipeBookClient.rebuildTable();
            if (CraftTweakerAPI.ENABLE_SEARCH_TREE_RECALCULATION) {
               try {
                  minecraft.populateSearchTreeManager();
                  ((SearchTree)minecraft.getSearchTreeManager().get(SearchTreeManager.ITEMS)).recalculate();
                  ((SearchTree)minecraft.getSearchTreeManager().get(SearchTreeManager.RECIPES)).recalculate();
               } catch (Exception var3) {
                  CraftTweakerAPI.logError("Error repopulating the SearchTree Managers. If this problem occurs more often you can disable it with '#disable_search_tree' in any CrT script.", var3);
               }
            }

            CraftTweakerAPI.logInfo("Fixed the RecipeBook");
         }

      }
   }
}
