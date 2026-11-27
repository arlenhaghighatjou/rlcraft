package mezz.jei.gui.recipes;

import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.gui.GuiHelper;
import mezz.jei.gui.elements.DrawableNineSliceTexture;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IShowsRecipeFocuses;
import mezz.jei.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class RecipeCatalysts implements IShowsRecipeFocuses {
   private static final int ingredientSize = 16;
   private static final int ingredientBorderSize = 1;
   private static final int borderSize = 5;
   private static final int overlapSize = 6;
   private final DrawableNineSliceTexture backgroundTab;
   private final List<GuiIngredient<Object>> ingredients = new ArrayList();
   private final DrawableNineSliceTexture slotBackground;
   private int left = 0;
   private int top = 0;
   private int width = 0;
   private int height = 0;

   public RecipeCatalysts() {
      GuiHelper guiHelper = Internal.getHelpers().getGuiHelper();
      this.backgroundTab = guiHelper.getCatalystTab();
      this.slotBackground = guiHelper.getNineSliceSlot();
   }

   public boolean isEmpty() {
      return this.ingredients.isEmpty();
   }

   public int getWidth() {
      return this.width - 6;
   }

   public void updateLayout(List<Object> ingredients, RecipesGui recipesGui) {
      this.ingredients.clear();
      if (!ingredients.isEmpty()) {
         int availableHeight = recipesGui.getYSize() - 8;
         int borderHeight = 12;
         int maxIngredientsPerColumn = (availableHeight - borderHeight) / 16;
         int columnCount = MathUtil.divideCeil(ingredients.size(), maxIngredientsPerColumn);
         maxIngredientsPerColumn = MathUtil.divideCeil(ingredients.size(), columnCount);
         this.width = 12 + columnCount * 16;
         this.height = 12 + maxIngredientsPerColumn * 16;
         this.top = recipesGui.getGuiTop();
         this.left = recipesGui.getGuiLeft() - this.width + 6;

         for(int i = 0; i < ingredients.size(); ++i) {
            Object ingredientForSlot = ingredients.get(i);
            GuiIngredient<Object> guiIngredient = this.<Object>createGuiIngredient(ingredientForSlot, i, maxIngredientsPerColumn);
            this.ingredients.add(guiIngredient);
         }
      }

   }

   private <T> GuiIngredient<T> createGuiIngredient(T ingredient, int index, int maxIngredientsPerColumn) {
      IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientRenderer<T> ingredientRenderer = ingredientRegistry.getIngredientRenderer(ingredient);
      IIngredientHelper<T> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredient);
      int column = index / maxIngredientsPerColumn;
      int row = index % maxIngredientsPerColumn;
      Rectangle rect = new Rectangle(this.left + 5 + column * 16 + 1, this.top + 5 + row * 16 + 1, 16, 16);
      GuiIngredient<T> guiIngredient = new GuiIngredient<T>(index, true, ingredientRenderer, ingredientHelper, rect, 0, 0, 0);
      guiIngredient.set(Collections.singletonList(ingredient), (IFocus)null);
      return guiIngredient;
   }

   @Nullable
   public GuiIngredient draw(Minecraft minecraft, int mouseX, int mouseY) {
      int ingredientCount = this.ingredients.size();
      if (ingredientCount > 0) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableDepth();
         GlStateManager.enableAlpha();
         int slotWidth = this.width - 10;
         int slotHeight = this.height - 10;
         this.backgroundTab.draw(minecraft, this.left, this.top, this.width, this.height);
         this.slotBackground.draw(minecraft, this.left + 5, this.top + 5, slotWidth, slotHeight);
         GlStateManager.disableAlpha();
         GlStateManager.enableDepth();
         GuiIngredient hovered = null;

         for(GuiIngredient guiIngredient : this.ingredients) {
            if (guiIngredient.isMouseOver(0, 0, mouseX, mouseY)) {
               hovered = guiIngredient;
            }

            guiIngredient.draw(minecraft, 0, 0);
         }

         return hovered;
      } else {
         return null;
      }
   }

   @Nullable
   private GuiIngredient getHovered(int mouseX, int mouseY) {
      for(GuiIngredient guiIngredient : this.ingredients) {
         if (guiIngredient.isMouseOver(0, 0, mouseX, mouseY)) {
            return guiIngredient;
         }
      }

      return null;
   }

   @Nullable
   public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
      GuiIngredient hovered = this.getHovered(mouseX, mouseY);
      if (hovered != null) {
         Object ingredientUnderMouse = hovered.getDisplayedIngredient();
         if (ingredientUnderMouse != null) {
            return ClickedIngredient.create(ingredientUnderMouse, hovered.getRect());
         }
      }

      return null;
   }

   public boolean canSetFocusWithMouse() {
      return true;
   }
}
