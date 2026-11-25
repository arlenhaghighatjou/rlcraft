package mezz.jei.recipes;

import com.google.common.collect.ImmutableTable;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.collect.Table;
import mezz.jei.startup.StackHelper;
import mezz.jei.transfer.BasicRecipeTransferHandler;
import mezz.jei.transfer.BasicRecipeTransferInfo;
import mezz.jei.util.ErrorUtil;
import net.minecraft.inventory.Container;

public class RecipeTransferRegistry implements IRecipeTransferRegistry {
   private final Table<Class, String, IRecipeTransferHandler> recipeTransferHandlers = Table.<Class, String, IRecipeTransferHandler>hashBasedTable();
   private final StackHelper stackHelper;
   private final IRecipeTransferHandlerHelper handlerHelper;

   public RecipeTransferRegistry(StackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper) {
      this.stackHelper = stackHelper;
      this.handlerHelper = handlerHelper;
   }

   public <C extends Container> void addRecipeTransferHandler(Class<C> containerClass, String recipeCategoryUid, int recipeSlotStart, int recipeSlotCount, int inventorySlotStart, int inventorySlotCount) {
      ErrorUtil.checkNotNull(containerClass, "containerClass");
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      IRecipeTransferInfo<C> recipeTransferHelper = new BasicRecipeTransferInfo<C>(containerClass, recipeCategoryUid, recipeSlotStart, recipeSlotCount, inventorySlotStart, inventorySlotCount);
      this.addRecipeTransferHandler(recipeTransferHelper);
   }

   public <C extends Container> void addRecipeTransferHandler(IRecipeTransferInfo<C> recipeTransferInfo) {
      ErrorUtil.checkNotNull(recipeTransferInfo, "recipeTransferInfo");
      IRecipeTransferHandler<C> recipeTransferHandler = new BasicRecipeTransferHandler<C>(this.stackHelper, this.handlerHelper, recipeTransferInfo);
      this.addRecipeTransferHandler(recipeTransferHandler, recipeTransferInfo.getRecipeCategoryUid());
   }

   public void addRecipeTransferHandler(IRecipeTransferHandler<?> recipeTransferHandler, String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipeTransferHandler, "recipeTransferHandler");
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      Class<?> containerClass = recipeTransferHandler.getContainerClass();
      this.recipeTransferHandlers.put(containerClass, recipeCategoryUid, recipeTransferHandler);
   }

   public void addUniversalRecipeTransferHandler(IRecipeTransferHandler<?> recipeTransferHandler) {
      ErrorUtil.checkNotNull(recipeTransferHandler, "recipeTransferHandler");
      Class<?> containerClass = recipeTransferHandler.getContainerClass();
      this.recipeTransferHandlers.put(containerClass, "universal recipe transfer handler", recipeTransferHandler);
   }

   public ImmutableTable<Class, String, IRecipeTransferHandler> getRecipeTransferHandlers() {
      return this.recipeTransferHandlers.toImmutable();
   }
}
