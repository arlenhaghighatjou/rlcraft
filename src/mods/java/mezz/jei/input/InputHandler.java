package mezz.jei.input;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.config.Config;
import mezz.jei.config.IngredientBlacklistType;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.Focus;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.gui.overlay.bookmarks.LeftAreaDispatcher;
import mezz.jei.gui.recipes.RecipeClickableArea;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.recipes.RecipeRegistry;
import mezz.jei.runtime.JeiRuntime;
import mezz.jei.util.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class InputHandler {
   private final RecipeRegistry recipeRegistry;
   private final IIngredientRegistry ingredientRegistry;
   private final IngredientFilter ingredientFilter;
   private final RecipesGui recipesGui;
   private final IngredientListOverlay ingredientListOverlay;
   private final LeftAreaDispatcher leftAreaDispatcher;
   private final BookmarkList bookmarkList;
   private final List<IShowsRecipeFocuses> showsRecipeFocuses = new ArrayList();
   private final IntSet clickHandled = new IntArraySet();

   public InputHandler(JeiRuntime runtime, IngredientRegistry ingredientRegistry, IngredientListOverlay ingredientListOverlay, GuiScreenHelper guiScreenHelper, LeftAreaDispatcher leftAreaDispatcher, BookmarkList bookmarkList) {
      this.recipeRegistry = runtime.getRecipeRegistry();
      this.ingredientRegistry = ingredientRegistry;
      this.ingredientFilter = runtime.getIngredientFilter();
      this.recipesGui = runtime.getRecipesGui();
      this.ingredientListOverlay = ingredientListOverlay;
      this.leftAreaDispatcher = leftAreaDispatcher;
      this.bookmarkList = bookmarkList;
      this.showsRecipeFocuses.add(this.recipesGui);
      this.showsRecipeFocuses.add(ingredientListOverlay);
      this.showsRecipeFocuses.add(leftAreaDispatcher);
      this.showsRecipeFocuses.add(new GuiContainerWrapper(guiScreenHelper));
   }

   @SubscribeEvent
   public void onGuiKeyboardEvent(Pre event) {
      if (this.hasKeyboardFocus() && this.handleKeyEvent()) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void onGuiKeyboardEvent(Post event) {
      if (!this.hasKeyboardFocus() && this.handleKeyEvent()) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void onGuiMouseEvent(net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre event) {
      GuiScreen guiScreen = event.getGui();
      Minecraft minecraft = guiScreen.mc;
      if (minecraft != null) {
         int x = Mouse.getEventX() * guiScreen.width / minecraft.displayWidth;
         int y = guiScreen.height - Mouse.getEventY() * guiScreen.height / minecraft.displayHeight - 1;
         if (this.handleMouseEvent(guiScreen, x, y)) {
            event.setCanceled(true);
         }
      }

   }

   public boolean handleMouseEvent(GuiScreen guiScreen, int mouseX, int mouseY) {
      boolean cancelEvent = false;
      int eventButton = Mouse.getEventButton();
      if (eventButton > -1) {
         if (Mouse.getEventButtonState()) {
            if (!this.clickHandled.contains(eventButton)) {
               cancelEvent = this.handleMouseClick(guiScreen, eventButton, mouseX, mouseY);
               if (cancelEvent) {
                  this.clickHandled.add(eventButton);
               }
            }
         } else if (this.clickHandled.contains(eventButton)) {
            this.clickHandled.remove(eventButton);
            cancelEvent = true;
         }
      } else if (Mouse.getEventDWheel() != 0) {
         cancelEvent = this.handleMouseScroll(Mouse.getEventDWheel(), mouseX, mouseY);
      }

      return cancelEvent;
   }

   private boolean handleMouseScroll(int dWheel, int mouseX, int mouseY) {
      return this.ingredientListOverlay.handleMouseScrolled(mouseX, mouseY, dWheel) || this.leftAreaDispatcher.handleMouseScrolled(mouseX, mouseY, dWheel);
   }

   private boolean handleMouseClick(GuiScreen guiScreen, int mouseButton, int mouseX, int mouseY) {
      IClickedIngredient<?> clicked = this.getFocusUnderMouseForClick(mouseX, mouseY);
      if (Config.isEditModeEnabled() && clicked != null && this.handleClickEdit(clicked)) {
         return true;
      } else if (this.ingredientListOverlay.handleMouseClicked(mouseX, mouseY, mouseButton)) {
         return true;
      } else if (this.leftAreaDispatcher.handleMouseClicked(mouseX, mouseY, mouseButton)) {
         return true;
      } else if (clicked != null && this.handleMouseClickedFocus(mouseButton, clicked)) {
         return true;
      } else if (this.handleFocusKeybinds(mouseButton - 100)) {
         return true;
      } else {
         if (guiScreen instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer)guiScreen;
            RecipeClickableArea clickableArea = this.recipeRegistry.getRecipeClickableArea(guiContainer, mouseX - guiContainer.getGuiLeft(), mouseY - guiContainer.getGuiTop());
            if (clickableArea != null) {
               List<String> recipeCategoryUids = clickableArea.getRecipeCategoryUids();
               this.recipesGui.showCategories(recipeCategoryUids);
               return true;
            }
         }

         return this.handleGlobalKeybinds(mouseButton - 100);
      }
   }

   @Nullable
   private IClickedIngredient<?> getFocusUnderMouseForClick(int mouseX, int mouseY) {
      for(IShowsRecipeFocuses gui : this.showsRecipeFocuses) {
         if (gui.canSetFocusWithMouse()) {
            IClickedIngredient<?> clicked = gui.getIngredientUnderMouse(mouseX, mouseY);
            if (clicked != null) {
               return clicked;
            }
         }
      }

      return null;
   }

   @Nullable
   private IClickedIngredient<?> getIngredientUnderMouseForKey(int mouseX, int mouseY) {
      for(IShowsRecipeFocuses gui : this.showsRecipeFocuses) {
         IClickedIngredient<?> clicked = gui.getIngredientUnderMouse(mouseX, mouseY);
         if (clicked != null) {
            return clicked;
         }
      }

      return null;
   }

   private <V> boolean handleMouseClickedFocus(int mouseButton, IClickedIngredient<V> clicked) {
      if (mouseButton == 0) {
         IFocus<?> focus = new Focus(IFocus.Mode.OUTPUT, clicked.getValue());
         this.recipesGui.show(focus);
         clicked.onClickHandled();
         return true;
      } else if (mouseButton == 1) {
         IFocus<?> focus = new Focus(IFocus.Mode.INPUT, clicked.getValue());
         this.recipesGui.show(focus);
         clicked.onClickHandled();
         return true;
      } else {
         return false;
      }
   }

   private <V> boolean handleClickEdit(IClickedIngredient<V> clicked) {
      V ingredient = clicked.getValue();
      IngredientBlacklistType blacklistType = GuiScreen.isCtrlKeyDown() ? IngredientBlacklistType.WILDCARD : IngredientBlacklistType.ITEM;
      IIngredientHelper<V> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredient);
      if (Config.isIngredientOnConfigBlacklist(ingredient, ingredientHelper)) {
         Config.removeIngredientFromConfigBlacklist(this.ingredientFilter, this.ingredientRegistry, ingredient, blacklistType, ingredientHelper);
      } else {
         Config.addIngredientToConfigBlacklist(this.ingredientFilter, this.ingredientRegistry, ingredient, blacklistType, ingredientHelper);
      }

      clicked.onClickHandled();
      return true;
   }

   private boolean hasKeyboardFocus() {
      return this.ingredientListOverlay.hasKeyboardFocus();
   }

   private boolean handleKeyEvent() {
      char typedChar = Keyboard.getEventCharacter();
      int eventKey = Keyboard.getEventKey();
      return (eventKey == 0 && typedChar >= ' ' || Keyboard.getEventKeyState()) && this.handleKeyDown(typedChar, eventKey);
   }

   private boolean handleKeyDown(char typedChar, int eventKey) {
      if (this.ingredientListOverlay.hasKeyboardFocus()) {
         if (KeyBindings.isInventoryCloseKey(eventKey) || KeyBindings.isEnterKey(eventKey)) {
            this.ingredientListOverlay.setKeyboardFocus(false);
            return true;
         }

         if (this.ingredientListOverlay.onKeyPressed(typedChar, eventKey)) {
            return true;
         }
      }

      if (this.handleGlobalKeybinds(eventKey)) {
         return true;
      } else if (!this.isContainerTextFieldFocused()) {
         if (this.handleFocusKeybinds(eventKey)) {
            return true;
         } else {
            return this.ingredientListOverlay.onKeyPressed(typedChar, eventKey);
         }
      } else {
         return false;
      }
   }

   private boolean handleGlobalKeybinds(int eventKey) {
      if (KeyBindings.toggleOverlay.isActiveAndMatches(eventKey)) {
         Config.toggleOverlayEnabled();
         return false;
      } else if (KeyBindings.toggleBookmarkOverlay.isActiveAndMatches(eventKey)) {
         Config.toggleBookmarkEnabled();
         return false;
      } else {
         return this.ingredientListOverlay.onGlobalKeyPressed(eventKey);
      }
   }

   private boolean handleFocusKeybinds(int eventKey) {
      boolean showRecipe = KeyBindings.showRecipe.isActiveAndMatches(eventKey);
      boolean showUses = KeyBindings.showUses.isActiveAndMatches(eventKey);
      boolean bookmark = KeyBindings.bookmark.isActiveAndMatches(eventKey);
      if (showRecipe || showUses || bookmark) {
         IClickedIngredient<?> clicked = this.getIngredientUnderMouseForKey(MouseHelper.getX(), MouseHelper.getY());
         if (clicked != null) {
            if (bookmark) {
               if (this.bookmarkList.remove(clicked.getValue())) {
                  if (this.bookmarkList.isEmpty() && Config.isBookmarkOverlayEnabled()) {
                     Config.toggleBookmarkEnabled();
                  }

                  return true;
               }

               if (!Config.isBookmarkOverlayEnabled()) {
                  Config.toggleBookmarkEnabled();
               }

               return this.bookmarkList.add(clicked.getValue());
            }

            IFocus.Mode mode = showRecipe ? IFocus.Mode.OUTPUT : IFocus.Mode.INPUT;
            this.recipesGui.show(new Focus(mode, clicked.getValue()));
            clicked.onClickHandled();
            return true;
         }
      }

      return false;
   }

   private boolean isContainerTextFieldFocused() {
      GuiScreen gui = Minecraft.getMinecraft().currentScreen;
      if (gui == null) {
         return false;
      } else {
         GuiTextField textField = (GuiTextField)ReflectionUtil.getFieldWithClass(gui, GuiTextField.class);
         return textField != null && textField.getVisible() && textField.isFocused();
      }
   }
}
