package mezz.jei.gui.overlay;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.awt.Rectangle;
import java.util.Set;
import javax.annotation.Nullable;
import mezz.jei.api.IIngredientListOverlay;
import mezz.jei.api.gui.IGuiProperties;
import mezz.jei.config.Config;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.gui.ghost.GhostIngredientDragManager;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.input.GuiTextFieldFilter;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IMouseHandler;
import mezz.jei.input.IShowsRecipeFocuses;
import mezz.jei.util.CommandUtil;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.item.ItemStack;

public class IngredientListOverlay implements IIngredientListOverlay, IMouseHandler, IShowsRecipeFocuses {
   private static final int BORDER_PADDING = 2;
   private static final int BUTTON_SIZE = 20;
   private static final int SEARCH_HEIGHT = 20;
   private boolean hasRoom;
   private final IngredientFilter ingredientFilter;
   private final GuiIconToggleButton configButton;
   private final IngredientGridWithNavigation contents;
   private final GuiScreenHelper guiScreenHelper;
   private final GuiTextFieldFilter searchField;
   private final GhostIngredientDragManager ghostIngredientDragManager;
   private Rectangle displayArea = new Rectangle();
   @Nullable
   private IGuiProperties guiProperties;

   private static boolean isSearchBarCentered(IGuiProperties guiProperties) {
      return Config.isCenterSearchBarEnabled() && guiProperties.getGuiTop() + guiProperties.getGuiYSize() + 20 < guiProperties.getScreenHeight();
   }

   public IngredientListOverlay(IngredientFilter ingredientFilter, IngredientRegistry ingredientRegistry, GuiScreenHelper guiScreenHelper) {
      this.ingredientFilter = ingredientFilter;
      this.guiScreenHelper = guiScreenHelper;
      this.contents = new IngredientGridWithNavigation(ingredientFilter, guiScreenHelper, GridAlignment.LEFT);
      ingredientFilter.addListener(() -> {
         this.onSetFilterText(Config.getFilterText());
      });
      this.searchField = new GuiTextFieldFilter(0, ingredientFilter);
      this.configButton = ConfigButton.create(this);
      this.ghostIngredientDragManager = new GhostIngredientDragManager(this.contents, guiScreenHelper, ingredientRegistry);
      this.setKeyboardFocus(false);
   }

   public void rebuildItemFilter() {
      Log.get().info("Updating ingredient filter...");
      long start_time = System.currentTimeMillis();
      this.ingredientFilter.modesChanged();
      Log.get().info("Updated  ingredient filter in {} ms", Long.valueOf(System.currentTimeMillis() - start_time));
      this.updateLayout(true);
   }

   public boolean isListDisplayed() {
      return Config.isOverlayEnabled() && this.guiProperties != null && this.hasRoom;
   }

   private static Rectangle getDisplayArea(IGuiProperties guiProperties) {
      int x = guiProperties.getGuiLeft() + guiProperties.getGuiXSize() + 2;
      int y = 2;
      int width = guiProperties.getScreenWidth() - x - 2;
      int height = guiProperties.getScreenHeight() - 2 - 2;
      return new Rectangle(x, 2, width, height);
   }

   public void updateScreen(@Nullable GuiScreen guiScreen, boolean forceUpdate) {
      boolean wasDisplayed = this.isListDisplayed();
      IGuiProperties guiProperties = this.guiScreenHelper.getGuiProperties(guiScreen);
      if (guiProperties == null) {
         if (this.guiProperties != null) {
            this.guiProperties = null;
            this.setKeyboardFocus(false);
            this.ghostIngredientDragManager.stopDrag();
         }
      } else if (forceUpdate || this.guiProperties == null || !GuiProperties.areEqual(this.guiProperties, guiProperties)) {
         this.guiProperties = guiProperties;
         this.displayArea = getDisplayArea(guiProperties);
         boolean searchBarCentered = isSearchBarCentered(guiProperties);
         int searchHeight = searchBarCentered ? 0 : 22;
         Set<Rectangle> guiExclusionAreas = this.guiScreenHelper.getGuiExclusionAreas();
         Rectangle availableContentsArea = new Rectangle(this.displayArea.x, this.displayArea.y, this.displayArea.width, this.displayArea.height - searchHeight);
         this.hasRoom = this.contents.updateBounds(availableContentsArea, guiExclusionAreas, 80);
         Rectangle contentsArea = this.contents.getArea();
         this.displayArea.x = contentsArea.x;
         this.displayArea.width = contentsArea.width;
         if (searchBarCentered && this.isListDisplayed()) {
            this.searchField.updateBounds(new Rectangle(guiProperties.getGuiLeft(), guiProperties.getScreenHeight() - 20 - 2, guiProperties.getGuiXSize() - 20 + 1, 20));
         } else {
            this.searchField.updateBounds(new Rectangle(this.displayArea.x, this.displayArea.y + this.displayArea.height - 20 - 2, this.displayArea.width - 20 + 1, 20));
         }

         this.configButton.updateBounds(new Rectangle(this.searchField.x + this.searchField.width - 1, this.searchField.y, 20, 20));
         this.updateLayout(false);
      }

      if (wasDisplayed && !this.isListDisplayed()) {
         Config.saveFilterText();
      }

   }

   public void updateLayout(boolean filterChanged) {
      this.contents.updateLayout(filterChanged);
      this.searchField.update();
   }

   public void drawScreen(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
      if (this.guiProperties != null) {
         if (this.isListDisplayed()) {
            GlStateManager.disableLighting();
            this.searchField.drawTextBox();
            this.contents.draw(minecraft, mouseX, mouseY, partialTicks);
            this.configButton.draw(minecraft, mouseX, mouseY, partialTicks);
         } else {
            this.configButton.draw(minecraft, mouseX, mouseY, partialTicks);
         }
      }

   }

   public void drawTooltips(Minecraft minecraft, int mouseX, int mouseY) {
      if (this.isListDisplayed()) {
         this.configButton.drawTooltips(minecraft, mouseX, mouseY);
         this.ghostIngredientDragManager.drawTooltips(minecraft, mouseX, mouseY);
         this.contents.drawTooltips(minecraft, mouseX, mouseY);
      } else if (this.guiProperties != null) {
         this.configButton.drawTooltips(minecraft, mouseX, mouseY);
      }

   }

   public void drawOnForeground(Minecraft minecraft, GuiContainer gui, int mouseX, int mouseY) {
      if (this.isListDisplayed()) {
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)(-gui.getGuiLeft()), (float)(-gui.getGuiTop()), 0.0F);
         this.ghostIngredientDragManager.drawOnForeground(minecraft, mouseX, mouseY);
         GlStateManager.popMatrix();
      }

   }

   public void handleTick() {
      if (this.isListDisplayed()) {
         this.searchField.updateCursorCounter();
      }

   }

   public boolean isMouseOver(int mouseX, int mouseY) {
      if (this.isListDisplayed()) {
         return Config.isCenterSearchBarEnabled() && this.searchField.isMouseOver(mouseX, mouseY) ? true : this.displayArea.contains(mouseX, mouseY);
      } else {
         return this.guiProperties != null ? this.configButton.isMouseOver(mouseX, mouseY) : false;
      }
   }

   @Nullable
   public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
      if (this.isListDisplayed()) {
         IClickedIngredient<?> clicked = this.contents.getIngredientUnderMouse(mouseX, mouseY);
         if (clicked != null) {
            clicked.setOnClickHandler(() -> {
               this.setKeyboardFocus(false);
            });
            return clicked;
         }
      }

      return null;
   }

   public boolean canSetFocusWithMouse() {
      return this.isListDisplayed() && this.contents.canSetFocusWithMouse();
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.isListDisplayed()) {
         if (this.ghostIngredientDragManager.handleMouseClicked(mouseX, mouseY)) {
            return true;
         }

         if (this.configButton.handleMouseClick(mouseX, mouseY)) {
            return true;
         }

         if (!this.isMouseOver(mouseX, mouseY)) {
            this.setKeyboardFocus(false);
            return false;
         }

         if (this.contents.handleMouseClicked(mouseX, mouseY, mouseButton)) {
            this.setKeyboardFocus(false);
            return true;
         }

         boolean searchClicked = this.searchField.isMouseOver(mouseX, mouseY);
         this.setKeyboardFocus(searchClicked);
         if (searchClicked) {
            boolean updated = this.searchField.handleMouseClicked(mouseX, mouseY, mouseButton);
            if (updated) {
               this.updateLayout(false);
            }

            return true;
         }

         Minecraft minecraft = Minecraft.getMinecraft();
         GuiScreen currentScreen = minecraft.currentScreen;
         if (currentScreen != null && !(currentScreen instanceof RecipesGui) && (mouseButton == 0 || mouseButton == 1 || minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100))) {
            IClickedIngredient<?> clicked = this.getIngredientUnderMouse(mouseX, mouseY);
            if (clicked != null) {
               if (Config.isCheatItemsEnabled()) {
                  ItemStack itemStack = clicked.getCheatItemStack();
                  if (!itemStack.isEmpty()) {
                     CommandUtil.giveStack(itemStack, mouseButton);
                  }

                  clicked.onClickHandled();
                  return true;
               }

               EntityPlayerSP player = minecraft.player;
               if (player != null) {
                  ItemStack mouseItem = player.inventory.getItemStack();
                  if (mouseItem.isEmpty() && this.ghostIngredientDragManager.handleClickGhostIngredient(currentScreen, clicked)) {
                     return true;
                  }
               }
            }
         }
      } else if (this.guiProperties != null) {
         return this.configButton.handleMouseClick(mouseX, mouseY);
      }

      return false;
   }

   public boolean handleMouseScrolled(int mouseX, int mouseY, int scrollDelta) {
      return this.isListDisplayed() && this.isMouseOver(mouseX, mouseY) && this.contents.handleMouseScrolled(mouseX, mouseY, scrollDelta);
   }

   public boolean hasKeyboardFocus() {
      return this.isListDisplayed() && this.searchField.isFocused();
   }

   public void setKeyboardFocus(boolean keyboardFocus) {
      this.searchField.setFocused(keyboardFocus);
   }

   public boolean onGlobalKeyPressed(int eventKey) {
      if (this.isListDisplayed()) {
         if (KeyBindings.toggleCheatMode.isActiveAndMatches(eventKey)) {
            Config.toggleCheatItemsEnabled();
            return true;
         }

         if (KeyBindings.toggleEditMode.isActiveAndMatches(eventKey)) {
            Config.toggleEditModeEnabled();
            return true;
         }

         if (KeyBindings.focusSearch.isActiveAndMatches(eventKey)) {
            this.setKeyboardFocus(true);
            return true;
         }
      }

      return false;
   }

   public boolean onKeyPressed(char typedChar, int eventKey) {
      if (this.isListDisplayed()) {
         if (this.hasKeyboardFocus() && this.searchField.textboxKeyTyped(typedChar, eventKey)) {
            boolean changed = Config.setFilterText(this.searchField.getText());
            if (changed) {
               this.updateLayout(true);
            }

            return true;
         } else {
            return this.contents.onKeyPressed(typedChar, eventKey);
         }
      } else {
         return false;
      }
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

   public void onSetFilterText(String filterText) {
      this.searchField.setText(filterText);
      this.updateLayout(true);
   }

   public ImmutableList<Object> getVisibleIngredients() {
      if (!this.isListDisplayed()) {
         return ImmutableList.of();
      } else {
         Builder<Object> visibleIngredients = ImmutableList.builder();

         for(IIngredientListElement element : this.contents.getVisibleElements()) {
            Object ingredient = element.getIngredient();
            visibleIngredients.add(ingredient);
         }

         return visibleIngredients.build();
      }
   }
}
