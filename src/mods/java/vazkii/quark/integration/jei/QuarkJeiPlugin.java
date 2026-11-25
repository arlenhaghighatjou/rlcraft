package vazkii.quark.integration.jei;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IGlobalGuiHandler;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.management.client.gui.GuiButtonTrash;
import vazkii.quark.management.feature.DeleteItems;
import vazkii.quark.misc.recipe.ElytraDuplicationRecipe;
import vazkii.quark.oddities.client.gui.GuiBackpackInventory;

@JEIPlugin
public class QuarkJeiPlugin implements IModPlugin {
   public QuarkJeiPlugin() {
   }

   public void register(IModRegistry registry) {
      try {
         registry.addGlobalGuiHandlers(new IGlobalGuiHandler[]{new QuarkJeiPlugin.GlobalTrashGuiHandler()});
         registry.addRecipeClickArea(GuiBackpackInventory.class, 137, 29, 10, 13, new String[]{"minecraft.crafting"});
         registry.getRecipeTransferRegistry().addRecipeTransferHandler(new BackpackRecipeTransferHandler(registry.getJeiHelpers().recipeTransferHandlerHelper()), "minecraft.crafting");
      } catch (LinkageError | RuntimeException var3) {
         ;
      }

      registry.handleRecipes(ElytraDuplicationRecipe.class, ElytraDuplicationRecipeWrapper::new, "minecraft.crafting");
   }

   private static class GlobalTrashGuiHandler implements IGlobalGuiHandler {
      private GlobalTrashGuiHandler() {
      }

      @Nonnull
      public Collection<Rectangle> getGuiExtraAreas() {
         if (ModuleLoader.isFeatureEnabled(DeleteItems.class)) {
            GuiButtonTrash trash = DeleteItems.getTrash();
            if (trash != null) {
               Rectangle trashArea = new Rectangle(trash.x, trash.y, trash.width, trash.height);
               return Collections.singleton(trashArea);
            }
         }

         return Collections.emptySet();
      }
   }
}
