package mezz.jei.gui.recipes;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.IRecipesGui;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.config.Config;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.Focus;
import mezz.jei.gui.GuiHelper;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.gui.elements.DrawableNineSliceTexture;
import mezz.jei.gui.elements.GuiIconButtonSmall;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IShowsRecipeFocuses;
import mezz.jei.input.MouseHelper;
import mezz.jei.runtime.JeiRuntime;
import mezz.jei.transfer.RecipeTransferUtil;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.StringUtil;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.client.config.HoverChecker;
import org.lwjgl.input.Mouse;

public class RecipesGui extends GuiScreen implements IRecipesGui, IShowsRecipeFocuses, IRecipeLogicStateListener {
   private static final int borderPadding = 6;
   private static final int innerPadding = 14;
   private static final int buttonWidth = 13;
   private static final int buttonHeight = 13;
   private int headerHeight;
   private final IRecipeGuiLogic logic;
   private final List<RecipeLayout> recipeLayouts = new ArrayList();
   private String pageString = "1/1";
   private String title = "";
   private final DrawableNineSliceTexture background;
   private final RecipeCatalysts recipeCatalysts;
   private final RecipeGuiTabs recipeGuiTabs;
   private HoverChecker titleHoverChecker = new HoverChecker(0, 0, 0, 0, 0);
   private final GuiButton nextRecipeCategory;
   private final GuiButton previousRecipeCategory;
   private final GuiButton nextPage;
   private final GuiButton previousPage;
   @Nullable
   private GuiScreen parentScreen;
   private int xSize;
   private int ySize;
   private int guiLeft;
   private int guiTop;
   private boolean init = false;

   public RecipesGui(IRecipeRegistry recipeRegistry, IngredientRegistry ingredientRegistry) {
      this.logic = new RecipeGuiLogic(recipeRegistry, this, ingredientRegistry);
      this.recipeCatalysts = new RecipeCatalysts();
      this.recipeGuiTabs = new RecipeGuiTabs(this.logic);
      this.mc = Minecraft.getMinecraft();
      GuiHelper guiHelper = Internal.getHelpers().getGuiHelper();
      IDrawableStatic arrowNext = guiHelper.getArrowNext();
      IDrawableStatic arrowPrevious = guiHelper.getArrowPrevious();
      this.nextRecipeCategory = new GuiIconButtonSmall(2, 0, 0, 13, 13, arrowNext);
      this.previousRecipeCategory = new GuiIconButtonSmall(3, 0, 0, 13, 13, arrowPrevious);
      this.nextPage = new GuiIconButtonSmall(4, 0, 0, 13, 13, arrowNext);
      this.previousPage = new GuiIconButtonSmall(5, 0, 0, 13, 13, arrowPrevious);
      this.background = guiHelper.getGuiBackground();
   }

   private static void drawCenteredString(FontRenderer fontRenderer, String string, int guiWidth, int xOffset, int yPos, int color, boolean shadow) {
      fontRenderer.drawString(string, (float)((guiWidth - fontRenderer.getStringWidth(string)) / 2 + xOffset), (float)yPos, color, shadow);
   }

   public int getGuiLeft() {
      return this.guiLeft;
   }

   public int getGuiTop() {
      return this.guiTop;
   }

   public int getXSize() {
      return this.xSize;
   }

   public int getYSize() {
      return this.ySize;
   }

   public int getRecipeCatalystExtraWidth() {
      return this.recipeCatalysts.isEmpty() ? 0 : this.recipeCatalysts.getWidth();
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void initGui() {
      super.initGui();
      this.xSize = 198;
      this.ySize = this.height - 68;
      int extraSpace = 0;
      int maxHeight = Config.getMaxRecipeGuiHeight();
      if (this.ySize > maxHeight) {
         extraSpace = this.ySize - maxHeight;
         this.ySize = maxHeight;
      }

      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = 45 + extraSpace / 2;
      int rightButtonX = this.guiLeft + this.xSize - 6 - 13;
      int leftButtonX = this.guiLeft + 6;
      int titleHeight = this.fontRenderer.FONT_HEIGHT + 6;
      int recipeClassButtonTop = this.guiTop + titleHeight - 13 + 2;
      this.nextRecipeCategory.x = rightButtonX;
      this.nextRecipeCategory.y = recipeClassButtonTop;
      this.previousRecipeCategory.x = leftButtonX;
      this.previousRecipeCategory.y = recipeClassButtonTop;
      int pageButtonTop = recipeClassButtonTop + 13 + 2;
      this.nextPage.x = rightButtonX;
      this.nextPage.y = pageButtonTop;
      this.previousPage.x = leftButtonX;
      this.previousPage.y = pageButtonTop;
      this.headerHeight = pageButtonTop + 13 - this.guiTop;
      this.addButtons();
      this.init = true;
      this.updateLayout();
   }

   private void addButtons() {
      this.buttonList.add(this.nextRecipeCategory);
      this.buttonList.add(this.previousRecipeCategory);
      this.buttonList.add(this.nextPage);
      this.buttonList.add(this.previousPage);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.mc != null) {
         this.drawDefaultBackground();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.zLevel = 0.0F;
         this.background.draw(Minecraft.getMinecraft(), this.guiLeft, this.guiTop, this.xSize, this.ySize);
         GlStateManager.disableBlend();
         drawRect(this.guiLeft + 6 + 13, this.nextRecipeCategory.y, this.guiLeft + this.xSize - 6 - 13, this.nextRecipeCategory.y + 13, 805306368);
         drawRect(this.guiLeft + 6 + 13, this.nextPage.y, this.guiLeft + this.xSize - 6 - 13, this.nextPage.y + 13, 805306368);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int textPadding = (13 - this.fontRenderer.FONT_HEIGHT) / 2;
         drawCenteredString(this.fontRenderer, this.title, this.xSize, this.guiLeft, this.nextRecipeCategory.y + textPadding, Color.WHITE.getRGB(), true);
         drawCenteredString(this.fontRenderer, this.pageString, this.xSize, this.guiLeft, this.nextPage.y + textPadding, Color.WHITE.getRGB(), true);
         this.nextRecipeCategory.drawButton(this.mc, mouseX, mouseY, partialTicks);
         this.previousRecipeCategory.drawButton(this.mc, mouseX, mouseY, partialTicks);
         this.nextPage.drawButton(this.mc, mouseX, mouseY, partialTicks);
         this.previousPage.drawButton(this.mc, mouseX, mouseY, partialTicks);
         RecipeLayout hoveredLayout = null;

         for(RecipeLayout recipeLayout : this.recipeLayouts) {
            if (recipeLayout.isMouseOver(mouseX, mouseY)) {
               hoveredLayout = recipeLayout;
            }

            recipeLayout.drawRecipe(this.mc, mouseX, mouseY);
         }

         GuiIngredient hoveredRecipeCatalyst = this.recipeCatalysts.draw(this.mc, mouseX, mouseY);
         this.recipeGuiTabs.draw(this.mc, mouseX, mouseY);
         if (hoveredLayout != null) {
            hoveredLayout.drawOverlays(this.mc, mouseX, mouseY);
         }

         if (hoveredRecipeCatalyst != null) {
            hoveredRecipeCatalyst.drawOverlays(this.mc, 0, 0, mouseX, mouseY);
         }

         if (this.titleHoverChecker.checkHover(mouseX, mouseY) && !this.logic.hasAllCategories()) {
            String showAllRecipesString = Translator.translateToLocal("jei.tooltip.show.all.recipes");
            TooltipRenderer.drawHoveringText(this.mc, showAllRecipesString, mouseX, mouseY);
         }

      }
   }

   public boolean isMouseOver(int mouseX, int mouseY) {
      if (this.mc != null && this.mc.currentScreen == this) {
         if (mouseX >= this.guiLeft && mouseY >= this.guiTop && mouseX < this.guiLeft + this.xSize && mouseY < this.guiTop + this.ySize) {
            return true;
         }

         for(RecipeLayout recipeLayout : this.recipeLayouts) {
            if (recipeLayout.isMouseOver(mouseX, mouseY)) {
               return true;
            }
         }
      }

      return false;
   }

   @Nullable
   public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
      if (this.isOpen()) {
         IClickedIngredient<?> clicked = this.recipeCatalysts.getIngredientUnderMouse(mouseX, mouseY);
         if (clicked != null) {
            return clicked;
         }

         if (this.isMouseOver(mouseX, mouseY)) {
            for(RecipeLayout recipeLayouts : this.recipeLayouts) {
               GuiIngredient<?> clicked = recipeLayouts.getGuiIngredientUnderMouse(mouseX, mouseY);
               if (clicked != null) {
                  Object displayedIngredient = clicked.getDisplayedIngredient();
                  if (displayedIngredient != null) {
                     return ClickedIngredient.create(displayedIngredient, clicked.getRect());
                  }
               }
            }
         }
      }

      return null;
   }

   public boolean canSetFocusWithMouse() {
      return true;
   }

   public void handleMouseInput() throws IOException {
      if (this.mc != null) {
         int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
         int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
         if (this.isMouseOver(x, y)) {
            int scrollDelta = Mouse.getEventDWheel();
            if (scrollDelta < 0) {
               this.logic.nextPage();
               return;
            }

            if (scrollDelta > 0) {
               this.logic.previousPage();
               return;
            }
         }

         super.handleMouseInput();
      }
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (this.mc != null) {
         if (this.isMouseOver(mouseX, mouseY)) {
            if (this.titleHoverChecker.checkHover(mouseX, mouseY)) {
               if (this.logic.setCategoryFocus()) {
                  return;
               }
            } else {
               for(RecipeLayout recipeLayout : this.recipeLayouts) {
                  if (recipeLayout.handleClick(this.mc, mouseX, mouseY, mouseButton)) {
                     return;
                  }
               }
            }
         }

         if (!this.recipeGuiTabs.isMouseOver(mouseX, mouseY) || !this.recipeGuiTabs.handleMouseClicked(mouseX, mouseY, mouseButton)) {
            if (!this.handleKeybinds(mouseButton - 100)) {
               super.mouseClicked(mouseX, mouseY, mouseButton);
            }
         }
      }
   }

   protected void keyTyped(char typedChar, int keyCode) {
      if (this.handleKeybinds(keyCode)) {
         this.keyHandled = true;
      }

   }

   private boolean handleKeybinds(int eventKey) {
      if (!KeyBindings.isInventoryCloseKey(eventKey) && !KeyBindings.isInventoryToggleKey(eventKey)) {
         if (KeyBindings.recipeBack.isActiveAndMatches(eventKey)) {
            this.back();
            return true;
         } else {
            JeiRuntime runtime = Internal.getRuntime();
            if (runtime != null) {
               IngredientListOverlay itemListOverlay = runtime.getIngredientListOverlay();
               if (!itemListOverlay.isMouseOver(MouseHelper.getX(), MouseHelper.getY())) {
                  if (KeyBindings.nextPage.isActiveAndMatches(eventKey)) {
                     this.logic.nextPage();
                     return true;
                  }

                  if (KeyBindings.previousPage.isActiveAndMatches(eventKey)) {
                     this.logic.previousPage();
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         this.close();
         return true;
      }
   }

   public boolean isOpen() {
      return this.mc != null && this.mc.currentScreen == this;
   }

   private void open() {
      if (this.mc != null) {
         if (!this.isOpen()) {
            this.parentScreen = this.mc.currentScreen;
         }

         this.mc.displayGuiScreen(this);
      }
   }

   public void close() {
      if (this.mc != null) {
         if (this.isOpen()) {
            if (this.parentScreen != null) {
               this.mc.displayGuiScreen(this.parentScreen);
               this.parentScreen = null;
            } else {
               EntityPlayerSP player = this.mc.player;
               if (player != null) {
                  player.closeScreen();
               }
            }

            this.logic.clearHistory();
         }

      }
   }

   public <V> void show(IFocus<V> focus) {
      IFocus<V> var2 = Focus.check(focus);
      if (this.logic.setFocus(var2)) {
         this.open();
      }

   }

   public void showCategories(List<String> recipeCategoryUids) {
      ErrorUtil.checkNotEmpty(recipeCategoryUids, "recipeCategoryUids");
      if (this.logic.setCategoryFocus(recipeCategoryUids)) {
         this.open();
      }

   }

   @Nullable
   public Object getIngredientUnderMouse() {
      IClickedIngredient<?> ingredient = this.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
      return ingredient != null ? ingredient.getValue() : null;
   }

   public void back() {
      this.logic.back();
   }

   protected void actionPerformed(GuiButton guibutton) {
      if (guibutton.id == this.nextPage.id) {
         this.logic.nextPage();
      } else if (guibutton.id == this.previousPage.id) {
         this.logic.previousPage();
      } else if (guibutton.id == this.nextRecipeCategory.id) {
         this.logic.nextRecipeCategory();
      } else if (guibutton.id == this.previousRecipeCategory.id) {
         this.logic.previousRecipeCategory();
      } else if (guibutton.id >= 100 && this.mc != null) {
         int recipeIndex = guibutton.id - 100;
         RecipeLayout recipeLayout = (RecipeLayout)this.recipeLayouts.get(recipeIndex);
         boolean maxTransfer = GuiScreen.isShiftKeyDown();
         Container container = this.getParentContainer();
         EntityPlayerSP player = this.mc.player;
         if (container != null && player != null && RecipeTransferUtil.transferRecipe(container, recipeLayout, player, maxTransfer)) {
            this.close();
         }
      }

   }

   private void updateLayout() {
      if (this.init && this.mc != null) {
         IRecipeCategory recipeCategory = this.logic.getSelectedRecipeCategory();
         IDrawable recipeBackground = recipeCategory.getBackground();
         int availableHeight = this.ySize - this.headerHeight;
         int heightPerRecipe = recipeBackground.getHeight() + 14;
         int recipesPerPage = availableHeight / heightPerRecipe;
         if (recipesPerPage == 0) {
            availableHeight = heightPerRecipe;
            recipesPerPage = 1;
         }

         int recipeXOffset = this.guiLeft + (this.xSize - recipeBackground.getWidth()) / 2;
         int recipeSpacing = (availableHeight - recipesPerPage * recipeBackground.getHeight()) / (recipesPerPage + 1);
         this.logic.setRecipesPerPage(recipesPerPage);
         this.title = recipeCategory.getTitle();
         int titleWidth = this.fontRenderer.getStringWidth(this.title);
         int availableTitleWidth = this.nextPage.x - (this.previousPage.x + this.previousPage.width) - 28;
         if (titleWidth > availableTitleWidth) {
            this.title = StringUtil.truncateStringToWidth(this.title, availableTitleWidth, this.fontRenderer);
            titleWidth = this.fontRenderer.getStringWidth(this.title);
         }

         int titleX = this.guiLeft + (this.xSize - titleWidth) / 2;
         int titleY = this.guiTop + 6;
         this.titleHoverChecker = new HoverChecker(titleY, titleY + this.fontRenderer.FONT_HEIGHT, titleX, titleX + titleWidth, 0);
         int spacingY = recipeBackground.getHeight() + recipeSpacing;
         this.recipeLayouts.clear();
         this.recipeLayouts.addAll(this.logic.getRecipeLayouts(recipeXOffset, this.guiTop + this.headerHeight + recipeSpacing, spacingY));
         this.addRecipeTransferButtons(this.mc, this.recipeLayouts);
         this.nextPage.enabled = this.previousPage.enabled = this.logic.hasMultiplePages();
         this.nextRecipeCategory.enabled = this.previousRecipeCategory.enabled = this.logic.hasMultipleCategories();
         this.pageString = this.logic.getPageString();
         List<Object> recipeCatalysts = this.logic.getRecipeCatalysts();
         this.recipeCatalysts.updateLayout(recipeCatalysts, this);
         this.recipeGuiTabs.initLayout(this);
      }
   }

   private void addRecipeTransferButtons(Minecraft minecraft, List<RecipeLayout> recipeLayouts) {
      this.buttonList.clear();
      this.addButtons();
      EntityPlayer player = minecraft.player;
      if (player != null) {
         Container container = this.getParentContainer();

         for(RecipeLayout recipeLayout : recipeLayouts) {
            RecipeTransferButton button = recipeLayout.getRecipeTransferButton();
            if (button != null) {
               button.init(container, player);
               this.buttonList.add(button);
            }
         }
      }

   }

   @Nullable
   public GuiScreen getParentScreen() {
      return this.parentScreen;
   }

   @Nullable
   private Container getParentContainer() {
      return this.parentScreen instanceof GuiContainer ? ((GuiContainer)this.parentScreen).inventorySlots : null;
   }

   public void onStateChange() {
      this.updateLayout();
   }
}
