package mezz.jei.gui.overlay;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import mezz.jei.config.Config;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.PageNavigation;
import mezz.jei.gui.ghost.IGhostIngredientDragSource;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IMouseHandler;
import mezz.jei.input.IPaged;
import mezz.jei.input.IShowsRecipeFocuses;
import mezz.jei.input.MouseHelper;
import mezz.jei.render.IngredientListSlot;
import mezz.jei.render.IngredientRenderer;
import mezz.jei.util.CommandUtil;
import mezz.jei.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;

public class IngredientGridWithNavigation implements IShowsRecipeFocuses, IMouseHandler, IGhostIngredientDragSource {
   private static final int NAVIGATION_HEIGHT = 20;
   private int firstItemIndex = 0;
   private final IPaged pageDelegate;
   private final PageNavigation navigation;
   private final GuiScreenHelper guiScreenHelper;
   private final IngredientGrid ingredientGrid;
   private final IIngredientGridSource ingredientSource;
   private Rectangle area = new Rectangle();

   public IngredientGridWithNavigation(IIngredientGridSource ingredientSource, GuiScreenHelper guiScreenHelper, GridAlignment alignment) {
      this.ingredientGrid = new IngredientGrid(alignment);
      this.ingredientSource = ingredientSource;
      this.guiScreenHelper = guiScreenHelper;
      this.pageDelegate = new IngredientGridWithNavigation.IngredientGridPaged();
      this.navigation = new PageNavigation(this.pageDelegate, false);
   }

   public void updateLayout(boolean resetToFirstPage) {
      if (resetToFirstPage) {
         this.firstItemIndex = 0;
      }

      List<IIngredientListElement> ingredientList = this.ingredientSource.getIngredientList();
      if (this.firstItemIndex >= ingredientList.size()) {
         this.firstItemIndex = 0;
      }

      this.ingredientGrid.guiIngredientSlots.set(this.firstItemIndex, ingredientList);
      this.navigation.updatePageState();
   }

   public boolean updateBounds(Rectangle availableArea, Set<Rectangle> guiExclusionAreas, int minWidth) {
      Rectangle estimatedNavigationArea = new Rectangle(availableArea.x, availableArea.y, availableArea.width, 20);
      Rectangle movedNavigationArea = MathUtil.moveDownToAvoidIntersection(guiExclusionAreas, estimatedNavigationArea);
      int navigationMaxY = movedNavigationArea.y + movedNavigationArea.height;
      Rectangle boundsWithoutNavigation = new Rectangle(availableArea.x, navigationMaxY, availableArea.width, availableArea.height - navigationMaxY);
      boolean gridHasRoom = this.ingredientGrid.updateBounds(boundsWithoutNavigation, minWidth, guiExclusionAreas);
      if (!gridHasRoom) {
         return false;
      } else {
         Rectangle displayArea = this.ingredientGrid.getArea();
         Rectangle navigationArea = new Rectangle(displayArea.x, movedNavigationArea.y, displayArea.width, 20);
         this.navigation.updateBounds(navigationArea);
         this.area = displayArea.union(navigationArea);
         return true;
      }
   }

   public Rectangle getArea() {
      return this.area;
   }

   public void draw(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
      this.ingredientGrid.draw(minecraft, mouseX, mouseY);
      this.navigation.draw(minecraft, mouseX, mouseY, partialTicks);
   }

   public void drawTooltips(Minecraft minecraft, int mouseX, int mouseY) {
      this.ingredientGrid.drawTooltips(minecraft, mouseX, mouseY);
   }

   public boolean isMouseOver(int mouseX, int mouseY) {
      return this.area.contains(mouseX, mouseY) && !this.guiScreenHelper.isInGuiExclusionArea(mouseX, mouseY);
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
      return !this.guiScreenHelper.isInGuiExclusionArea(mouseX, mouseY) && (this.ingredientGrid.handleMouseClicked(mouseX, mouseY) || this.navigation.handleMouseClickedButtons(mouseX, mouseY));
   }

   public boolean handleMouseScrolled(int mouseX, int mouseY, int scrollDelta) {
      if (scrollDelta < 0) {
         this.pageDelegate.nextPage();
         return true;
      } else if (scrollDelta > 0) {
         this.pageDelegate.previousPage();
         return true;
      } else {
         return false;
      }
   }

   public boolean onKeyPressed(char typedChar, int keyCode) {
      if (KeyBindings.nextPage.isActiveAndMatches(keyCode)) {
         this.pageDelegate.nextPage();
         return true;
      } else if (KeyBindings.previousPage.isActiveAndMatches(keyCode)) {
         this.pageDelegate.previousPage();
         return true;
      } else {
         return this.checkHotbarKeys(keyCode);
      }
   }

   protected boolean checkHotbarKeys(int keyCode) {
      GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
      if (Config.isCheatItemsEnabled() && guiScreen != null && !(guiScreen instanceof RecipesGui)) {
         int mouseX = MouseHelper.getX();
         int mouseY = MouseHelper.getY();
         if (this.isMouseOver(mouseX, mouseY)) {
            GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;

            for(int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
               if (gameSettings.keyBindsHotbar[hotbarSlot].isActiveAndMatches(keyCode)) {
                  IClickedIngredient<?> ingredientUnderMouse = this.getIngredientUnderMouse(mouseX, mouseY);
                  if (ingredientUnderMouse != null) {
                     ItemStack itemStack = ingredientUnderMouse.getCheatItemStack();
                     if (!itemStack.isEmpty()) {
                        CommandUtil.setHotbarStack(itemStack, hotbarSlot);
                     }

                     ingredientUnderMouse.onClickHandled();
                  }

                  return true;
               }
            }
         }
      }

      return false;
   }

   @Nullable
   public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
      return this.ingredientGrid.getIngredientUnderMouse(mouseX, mouseY);
   }

   @Nullable
   public IIngredientListElement getElementUnderMouse() {
      return this.ingredientGrid.getElementUnderMouse();
   }

   public boolean canSetFocusWithMouse() {
      return this.ingredientGrid.canSetFocusWithMouse();
   }

   public List<IIngredientListElement> getVisibleElements() {
      List<IIngredientListElement> visibleElements = new ArrayList();

      for(IngredientListSlot slot : this.ingredientGrid.guiIngredientSlots.getAllGuiIngredientSlots()) {
         IngredientRenderer renderer = slot.getIngredientRenderer();
         if (renderer != null) {
            visibleElements.add(renderer.getElement());
         }
      }

      return visibleElements;
   }

   private class IngredientGridPaged implements IPaged {
      private IngredientGridPaged() {
      }

      public boolean nextPage() {
         int itemsCount = IngredientGridWithNavigation.this.ingredientSource.size();
         if (itemsCount > 0) {
            IngredientGridWithNavigation.this.firstItemIndex = IngredientGridWithNavigation.this.firstItemIndex + IngredientGridWithNavigation.this.ingredientGrid.size();
            if (IngredientGridWithNavigation.this.firstItemIndex >= itemsCount) {
               IngredientGridWithNavigation.this.firstItemIndex = 0;
            }

            IngredientGridWithNavigation.this.updateLayout(false);
            return true;
         } else {
            IngredientGridWithNavigation.this.firstItemIndex = 0;
            IngredientGridWithNavigation.this.updateLayout(false);
            return false;
         }
      }

      public boolean previousPage() {
         int itemsPerPage = IngredientGridWithNavigation.this.ingredientGrid.size();
         if (itemsPerPage == 0) {
            IngredientGridWithNavigation.this.firstItemIndex = 0;
            IngredientGridWithNavigation.this.updateLayout(false);
            return false;
         } else {
            int itemsCount = IngredientGridWithNavigation.this.ingredientSource.size();
            int pageNum = IngredientGridWithNavigation.this.firstItemIndex / itemsPerPage;
            if (pageNum == 0) {
               pageNum = itemsCount / itemsPerPage;
            } else {
               --pageNum;
            }

            IngredientGridWithNavigation.this.firstItemIndex = itemsPerPage * pageNum;
            if (IngredientGridWithNavigation.this.firstItemIndex > 0 && IngredientGridWithNavigation.this.firstItemIndex == itemsCount) {
               --pageNum;
               IngredientGridWithNavigation.this.firstItemIndex = itemsPerPage * pageNum;
            }

            IngredientGridWithNavigation.this.updateLayout(false);
            return true;
         }
      }

      public boolean hasNext() {
         int itemsPerPage = IngredientGridWithNavigation.this.ingredientGrid.size();
         return itemsPerPage > 0 && IngredientGridWithNavigation.this.ingredientSource.size() > itemsPerPage;
      }

      public boolean hasPrevious() {
         int itemsPerPage = IngredientGridWithNavigation.this.ingredientGrid.size();
         return itemsPerPage > 0 && IngredientGridWithNavigation.this.ingredientSource.size() > itemsPerPage;
      }

      public int getPageCount() {
         int itemCount = IngredientGridWithNavigation.this.ingredientSource.size();
         int stacksPerPage = IngredientGridWithNavigation.this.ingredientGrid.size();
         if (stacksPerPage == 0) {
            return 1;
         } else {
            int pageCount = MathUtil.divideCeil(itemCount, stacksPerPage);
            pageCount = Math.max(1, pageCount);
            return pageCount;
         }
      }

      public int getPageNumber() {
         int stacksPerPage = IngredientGridWithNavigation.this.ingredientGrid.size();
         return stacksPerPage == 0 ? 0 : IngredientGridWithNavigation.this.firstItemIndex / stacksPerPage;
      }
   }
}
