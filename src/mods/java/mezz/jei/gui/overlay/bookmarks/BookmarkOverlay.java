package mezz.jei.gui.overlay.bookmarks;

import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.Set;
import javax.annotation.Nullable;
import mezz.jei.api.IBookmarkOverlay;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.config.Config;
import mezz.jei.gui.GuiHelper;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.gui.overlay.GridAlignment;
import mezz.jei.gui.overlay.IIngredientGridSource;
import mezz.jei.gui.overlay.IngredientGrid;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IShowsRecipeFocuses;
import mezz.jei.util.CommandUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class BookmarkOverlay implements IShowsRecipeFocuses, ILeftAreaContent, IBookmarkOverlay {
   private static final int BUTTON_SIZE = 20;
   private Rectangle parentArea = new Rectangle();
   private Rectangle displayArea = new Rectangle();
   private final IngredientGridWithNavigation contents;
   private final GuiIconToggleButton bookmarkButton;
   private boolean hasRoom = false;
   private final BookmarkList bookmarkList;

   public BookmarkOverlay(BookmarkList bookmarkList, GuiHelper guiHelper, GuiScreenHelper guiScreenHelper) {
      this.bookmarkList = bookmarkList;
      this.bookmarkButton = BookmarkButton.create(this, bookmarkList, guiHelper);
      this.contents = new IngredientGridWithNavigation(bookmarkList, guiScreenHelper, GridAlignment.RIGHT);
      bookmarkList.addListener(() -> {
         this.contents.updateLayout(false);
      });
   }

   public boolean isListDisplayed() {
      return Config.isBookmarkOverlayEnabled() && this.hasRoom && !this.bookmarkList.isEmpty();
   }

   public boolean hasRoom() {
      return this.hasRoom;
   }

   public void updateBounds(Rectangle area, Set<Rectangle> guiExclusionAreas) {
      this.parentArea = area;
      this.hasRoom = this.updateBounds(guiExclusionAreas);
   }

   public void drawScreen(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
      if (this.isListDisplayed()) {
         this.contents.draw(minecraft, mouseX, mouseY, partialTicks);
      }

      this.bookmarkButton.draw(minecraft, mouseX, mouseY, partialTicks);
   }

   public void drawOnForeground(GuiContainer gui, int mouseX, int mouseY) {
   }

   public void drawTooltips(Minecraft minecraft, int mouseX, int mouseY) {
      if (this.isListDisplayed()) {
         this.contents.drawTooltips(minecraft, mouseX, mouseY);
      }

      this.bookmarkButton.drawTooltips(minecraft, mouseX, mouseY);
   }

   private static int getMinWidth() {
      return Math.max(80, 4 * IngredientGrid.INGREDIENT_WIDTH);
   }

   public boolean updateBounds(Set<Rectangle> guiExclusionAreas) {
      this.displayArea = new Rectangle(this.parentArea);
      int minWidth = getMinWidth();
      if (this.displayArea.width < minWidth) {
         return false;
      } else {
         Rectangle availableContentsArea = new Rectangle(this.displayArea.x, this.displayArea.y, this.displayArea.width, this.displayArea.height - 24);
         boolean contentsHasRoom = this.contents.updateBounds(availableContentsArea, guiExclusionAreas, minWidth);
         Rectangle contentsArea = this.contents.getArea();
         this.displayArea.x = contentsArea.x;
         this.displayArea.width = contentsArea.width;
         this.bookmarkButton.updateBounds(new Rectangle(this.displayArea.x, (int)Math.floor(this.displayArea.getMaxY()) - 20 - 2, 20, 20));
         this.contents.updateLayout(false);
         return contentsHasRoom;
      }
   }

   @Nullable
   public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
      return this.isListDisplayed() ? this.contents.getIngredientUnderMouse(mouseX, mouseY) : null;
   }

   public boolean canSetFocusWithMouse() {
      return this.isListDisplayed() && this.contents.canSetFocusWithMouse();
   }

   public boolean handleMouseScrolled(int mouseX, int mouseY, int scrollDelta) {
      return this.isListDisplayed() && this.displayArea.contains(mouseX, mouseY) && this.contents.handleMouseScrolled(mouseX, mouseY, scrollDelta);
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.isListDisplayed()) {
         if (this.displayArea.contains(mouseX, mouseY)) {
            Minecraft minecraft = Minecraft.getMinecraft();
            GuiScreen currentScreen = minecraft.currentScreen;
            if (currentScreen != null && !(currentScreen instanceof RecipesGui) && (mouseButton == 0 || mouseButton == 1 || minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100))) {
               IClickedIngredient<?> clicked = this.getIngredientUnderMouse(mouseX, mouseY);
               if (clicked != null && Config.isCheatItemsEnabled()) {
                  ItemStack itemStack = clicked.getCheatItemStack();
                  if (!itemStack.isEmpty()) {
                     CommandUtil.giveStack(itemStack, mouseButton);
                  }

                  clicked.onClickHandled();
                  return true;
               }
            }
         }

         if (this.contents.isMouseOver(mouseX, mouseY)) {
            this.contents.handleMouseClicked(mouseX, mouseY, mouseButton);
         }
      }

      return this.bookmarkButton.isMouseOver(mouseX, mouseY) ? this.bookmarkButton.handleMouseClick(mouseX, mouseY) : false;
   }

   @Nullable
   public Object getIngredientUnderMouse() {
      if (this.isListDisplayed()) {
         IIngredientListElement elementUnderMouse = this.contents.getElementUnderMouse();
         if (elementUnderMouse != null) {
            return elementUnderMouse.getIngredient();
         }
      }

      return null;
   }
}
