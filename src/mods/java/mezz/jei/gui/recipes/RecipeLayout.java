package mezz.jei.gui.recipes;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.Focus;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.gui.elements.DrawableNineSliceTexture;
import mezz.jei.gui.ingredients.GuiFluidStackGroup;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.gui.ingredients.GuiIngredientGroup;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.ingredients.Ingredients;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.LegacyUtil;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeLayout implements IRecipeLayoutDrawable {
   private static final int RECIPE_BUTTON_SIZE = 13;
   private static final int RECIPE_BORDER_PADDING = 4;
   public static final int recipeTransferButtonIndex = 100;
   private final int ingredientCycleOffset = (int)(Math.random() * 10000.0D % 2.147483647E9D);
   private final IRecipeCategory recipeCategory;
   private final GuiItemStackGroup guiItemStackGroup;
   private final GuiFluidStackGroup guiFluidStackGroup;
   private final Map<IIngredientType, GuiIngredientGroup> guiIngredientGroups;
   @Nullable
   private final RecipeTransferButton recipeTransferButton;
   private final IRecipeWrapper recipeWrapper;
   @Nullable
   private final IFocus<?> focus;
   private final Color highlightColor = new Color(Integer.MAX_VALUE, true);
   @Nullable
   private ShapelessIcon shapelessIcon;
   private final DrawableNineSliceTexture recipeBorder;
   private int posX;
   private int posY;

   @Nullable
   public static <T extends IRecipeWrapper> RecipeLayout create(int index, IRecipeCategory<T> recipeCategory, T recipeWrapper, @Nullable IFocus focus, int posX, int posY) {
      RecipeLayout recipeLayout = new RecipeLayout(index, recipeCategory, recipeWrapper, focus, posX, posY);

      try {
         IIngredients ingredients = new Ingredients();
         recipeWrapper.getIngredients(ingredients);
         recipeCategory.setRecipe(recipeLayout, recipeWrapper, ingredients);
         return recipeLayout;
      } catch (LinkageError | RuntimeException var8) {
         Log.get().error("Error caught from Recipe Category: {}", recipeCategory.getClass().getCanonicalName(), var8);
         return null;
      }
   }

   private <T extends IRecipeWrapper> RecipeLayout(int index, IRecipeCategory<T> recipeCategory, T recipeWrapper, @Nullable IFocus<?> focus, int posX, int posY) {
      ErrorUtil.checkNotNull(recipeCategory, "recipeCategory");
      ErrorUtil.checkNotNull(recipeWrapper, "recipeWrapper");
      if (focus != null) {
         focus = Focus.check(focus);
      }

      this.recipeCategory = recipeCategory;
      this.focus = focus;
      IFocus<ItemStack> itemStackFocus = null;
      IFocus<FluidStack> fluidStackFocus = null;
      if (focus != null) {
         Object focusValue = focus.getValue();
         if (focusValue instanceof ItemStack) {
            itemStackFocus = focus;
         } else if (focusValue instanceof FluidStack) {
            fluidStackFocus = focus;
         }
      }

      this.guiItemStackGroup = new GuiItemStackGroup(itemStackFocus, this.ingredientCycleOffset);
      this.guiFluidStackGroup = new GuiFluidStackGroup(fluidStackFocus, this.ingredientCycleOffset);
      this.guiIngredientGroups = new IdentityHashMap();
      this.guiIngredientGroups.put(VanillaTypes.ITEM, this.guiItemStackGroup);
      this.guiIngredientGroups.put(VanillaTypes.FLUID, this.guiFluidStackGroup);
      if (index >= 0) {
         IDrawable icon = Internal.getHelpers().getGuiHelper().getRecipeTransfer();
         this.recipeTransferButton = new RecipeTransferButton(100 + index, 0, 0, 13, 13, icon, this);
      } else {
         this.recipeTransferButton = null;
      }

      this.setPosition(posX, posY);
      this.recipeWrapper = recipeWrapper;
      this.recipeBorder = Internal.getHelpers().getGuiHelper().getRecipeBackground();
   }

   public void setPosition(int posX, int posY) {
      this.posX = posX;
      this.posY = posY;
      if (this.recipeTransferButton != null) {
         int width = this.recipeCategory.getBackground().getWidth();
         int height = this.recipeCategory.getBackground().getHeight();
         this.recipeTransferButton.x = posX + width + 4 + 2;
         this.recipeTransferButton.y = posY + height - 13;
      }

   }

   /** @deprecated */
   @Deprecated
   public void draw(Minecraft minecraft, int mouseX, int mouseY) {
      this.drawRecipe(minecraft, mouseX, mouseY);
      this.drawOverlays(minecraft, mouseX, mouseY);
   }

   public void drawRecipe(Minecraft minecraft, int mouseX, int mouseY) {
      IDrawable background = this.recipeCategory.getBackground();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      GlStateManager.enableAlpha();
      int recipeMouseX = mouseX - this.posX;
      int recipeMouseY = mouseY - this.posY;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)this.posX, (float)this.posY, 0.0F);
      IDrawable categoryBackground = this.recipeCategory.getBackground();
      int width = categoryBackground.getWidth() + 8;
      int height = categoryBackground.getHeight() + 8;
      this.recipeBorder.draw(minecraft, -4, -4, width, height);
      background.draw(minecraft);
      this.recipeCategory.drawExtras(minecraft);
      this.recipeWrapper.drawInfo(minecraft, background.getWidth(), background.getHeight(), recipeMouseX, recipeMouseY);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.shapelessIcon != null) {
         this.shapelessIcon.draw(minecraft, background.getWidth());
      }

      GlStateManager.popMatrix();

      for(GuiIngredientGroup guiIngredientGroup : this.guiIngredientGroups.values()) {
         guiIngredientGroup.draw(minecraft, this.posX, this.posY, this.highlightColor, mouseX, mouseY);
      }

      if (this.recipeTransferButton != null) {
         float partialTicks = minecraft.getRenderPartialTicks();
         this.recipeTransferButton.drawButton(minecraft, mouseX, mouseY, partialTicks);
      }

      GlStateManager.disableBlend();
      GlStateManager.disableLighting();
      GlStateManager.disableAlpha();
   }

   public void drawOverlays(Minecraft minecraft, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      GlStateManager.enableAlpha();
      int recipeMouseX = mouseX - this.posX;
      int recipeMouseY = mouseY - this.posY;
      GuiIngredient hoveredIngredient = null;

      for(GuiIngredientGroup guiIngredientGroup : this.guiIngredientGroups.values()) {
         hoveredIngredient = guiIngredientGroup.getHoveredIngredient(this.posX, this.posY, mouseX, mouseY);
         if (hoveredIngredient != null) {
            break;
         }
      }

      if (this.recipeTransferButton != null) {
         this.recipeTransferButton.drawToolTip(minecraft, mouseX, mouseY);
      }

      GlStateManager.disableBlend();
      GlStateManager.disableLighting();
      if (hoveredIngredient != null) {
         hoveredIngredient.drawOverlays(minecraft, this.posX, this.posY, recipeMouseX, recipeMouseY);
      } else if (this.isMouseOver(mouseX, mouseY)) {
         List<String> categoryTooltipStrings = LegacyUtil.getTooltipStrings(this.recipeCategory, recipeMouseX, recipeMouseY);
         List<String> tooltipStrings = new ArrayList(categoryTooltipStrings);
         List<String> wrapperTooltips = this.recipeWrapper.getTooltipStrings(recipeMouseX, recipeMouseY);
         if (wrapperTooltips != null) {
            tooltipStrings.addAll(wrapperTooltips);
         }

         if (tooltipStrings.isEmpty() && this.shapelessIcon != null) {
            tooltipStrings = this.shapelessIcon.getTooltipStrings(recipeMouseX, recipeMouseY);
         }

         if (tooltipStrings != null && !tooltipStrings.isEmpty()) {
            TooltipRenderer.drawHoveringText(minecraft, tooltipStrings, mouseX, mouseY);
         }
      }

      GlStateManager.disableAlpha();
   }

   public boolean isMouseOver(int mouseX, int mouseY) {
      IDrawable background = this.recipeCategory.getBackground();
      Rectangle backgroundRect = new Rectangle(this.posX, this.posY, background.getWidth(), background.getHeight());
      return backgroundRect.contains(mouseX, mouseY) || this.recipeTransferButton != null && this.recipeTransferButton.isMouseOver();
   }

   @Nullable
   public Object getIngredientUnderMouse(int mouseX, int mouseY) {
      GuiIngredient<?> guiIngredient = this.getGuiIngredientUnderMouse(mouseX, mouseY);
      return guiIngredient != null ? guiIngredient.getDisplayedIngredient() : null;
   }

   @Nullable
   public GuiIngredient<?> getGuiIngredientUnderMouse(int mouseX, int mouseY) {
      for(GuiIngredientGroup<?> guiIngredientGroup : this.guiIngredientGroups.values()) {
         GuiIngredient<?> clicked = guiIngredientGroup.getHoveredIngredient(this.posX, this.posY, mouseX, mouseY);
         if (clicked != null) {
            return clicked;
         }
      }

      return null;
   }

   public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
      return this.recipeWrapper.handleClick(minecraft, mouseX - this.posX, mouseY - this.posY, mouseButton);
   }

   public GuiItemStackGroup getItemStacks() {
      return this.guiItemStackGroup;
   }

   public IGuiFluidStackGroup getFluidStacks() {
      return this.guiFluidStackGroup;
   }

   public <T> IGuiIngredientGroup<T> getIngredientsGroup(IIngredientType<T> ingredientType) {
      GuiIngredientGroup<T> guiIngredientGroup = (GuiIngredientGroup)this.guiIngredientGroups.get(ingredientType);
      if (guiIngredientGroup == null) {
         IFocus<T> focus = null;
         if (this.focus != null) {
            Object focusValue = this.focus.getValue();
            if (ingredientType.getIngredientClass().isInstance(focusValue)) {
               focus = this.focus;
            }
         }

         guiIngredientGroup = new GuiIngredientGroup<T>(ingredientType, focus, this.ingredientCycleOffset);
         this.guiIngredientGroups.put(ingredientType, guiIngredientGroup);
      }

      return guiIngredientGroup;
   }

   /** @deprecated */
   @Deprecated
   public <T> IGuiIngredientGroup<T> getIngredientsGroup(Class<T> ingredientClass) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientType<T> ingredientType = ingredientRegistry.getIngredientType(ingredientClass);
      return this.getIngredientsGroup(ingredientType);
   }

   public void setRecipeTransferButton(int posX, int posY) {
      if (this.recipeTransferButton != null) {
         this.recipeTransferButton.x = posX + this.posX;
         this.recipeTransferButton.y = posY + this.posY;
      }

   }

   public void setShapeless() {
      this.shapelessIcon = new ShapelessIcon();
   }

   @Nullable
   public IFocus<?> getFocus() {
      return this.focus;
   }

   @Nullable
   public RecipeTransferButton getRecipeTransferButton() {
      return this.recipeTransferButton;
   }

   public IRecipeCategory getRecipeCategory() {
      return this.recipeCategory;
   }

   public int getPosX() {
      return this.posX;
   }

   public int getPosY() {
      return this.posY;
   }
}
