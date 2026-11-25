package mezz.jei.gui.ingredients;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.gui.Focus;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;

public class GuiIngredientGroup<T> implements IGuiIngredientGroup<T> {
   private final Map<Integer, GuiIngredient<T>> guiIngredients = new HashMap();
   private final Set<Integer> inputSlots = new HashSet();
   private final IIngredientHelper<T> ingredientHelper;
   private final IIngredientRenderer<T> ingredientRenderer;
   private final IIngredientType<T> ingredientType;
   private final int cycleOffset;
   @Nullable
   private IFocus<T> focus;
   @Nullable
   private ITooltipCallback<T> tooltipCallback;

   public GuiIngredientGroup(IIngredientType<T> ingredientType, @Nullable IFocus<T> focus, int cycleOffset) {
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      this.ingredientType = ingredientType;
      if (focus == null) {
         this.focus = null;
      } else {
         this.focus = Focus.<T>check(focus);
      }

      IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      this.ingredientHelper = ingredientRegistry.getIngredientHelper(ingredientType);
      this.ingredientRenderer = ingredientRegistry.getIngredientRenderer(ingredientType);
      this.cycleOffset = cycleOffset;
   }

   public void init(int slotIndex, boolean input, int xPosition, int yPosition) {
      this.init(slotIndex, input, this.ingredientRenderer, xPosition, yPosition, 16, 16, 0, 0);
   }

   public void init(int slotIndex, boolean input, IIngredientRenderer<T> ingredientRenderer, int xPosition, int yPosition, int width, int height, int xPadding, int yPadding) {
      Rectangle rect = new Rectangle(xPosition, yPosition, width, height);
      GuiIngredient<T> guiIngredient = new GuiIngredient<T>(slotIndex, input, ingredientRenderer, this.ingredientHelper, rect, xPadding, yPadding, this.cycleOffset);
      this.guiIngredients.put(Integer.valueOf(slotIndex), guiIngredient);
      if (input) {
         this.inputSlots.add(Integer.valueOf(slotIndex));
      }

   }

   public void set(IIngredients ingredients) {
      List<List<T>> inputs = ingredients.getInputs(this.ingredientType);
      List<List<T>> outputs = ingredients.getOutputs(this.ingredientType);
      int inputIndex = 0;
      int outputIndex = 0;
      List<Integer> slots = new ArrayList(this.guiIngredients.keySet());
      Collections.sort(slots);

      for(Integer slot : slots) {
         if (this.inputSlots.contains(slot)) {
            if (inputIndex < inputs.size()) {
               List<T> input = (List)inputs.get(inputIndex);
               ++inputIndex;
               this.set(slot.intValue(), input);
            }
         } else if (outputIndex < outputs.size()) {
            List<T> output = (List)outputs.get(outputIndex);
            ++outputIndex;
            this.set(slot.intValue(), output);
         }
      }

   }

   public void set(int slotIndex, @Nullable List<T> ingredients) {
      if (ingredients != null) {
         for(T ingredient : ingredients) {
            Class<? extends T> ingredientClass = this.ingredientType.getIngredientClass();
            if (!ingredientClass.isInstance(ingredient) && ingredient != null) {
               Log.get().error("Received wrong type of ingredient. Expected {}, got {}", ingredientClass, ingredient.getClass(), new IllegalArgumentException());
               return;
            }
         }
      }

      GuiIngredient<T> guiIngredient = (GuiIngredient)this.guiIngredients.get(Integer.valueOf(slotIndex));
      IFocus.Mode ingredientMode = guiIngredient.isInput() ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT;
      if (this.focus != null && this.focus.getMode() != ingredientMode) {
         guiIngredient.set(ingredients, (IFocus)null);
      } else {
         guiIngredient.set(ingredients, this.focus);
      }

   }

   public void set(int slotIndex, @Nullable T value) {
      this.set(slotIndex, Collections.singletonList(value));
   }

   public void setBackground(int slotIndex, IDrawable background) {
      GuiIngredient<T> guiIngredient = (GuiIngredient)this.guiIngredients.get(Integer.valueOf(slotIndex));
      guiIngredient.setBackground(background);
   }

   public void addTooltipCallback(ITooltipCallback<T> tooltipCallback) {
      this.tooltipCallback = tooltipCallback;
   }

   public Map<Integer, GuiIngredient<T>> getGuiIngredients() {
      return this.guiIngredients;
   }

   @Nullable
   public GuiIngredient<T> getHoveredIngredient(int xOffset, int yOffset, int mouseX, int mouseY) {
      for(GuiIngredient<T> ingredient : this.guiIngredients.values()) {
         if (ingredient.isMouseOver(xOffset, yOffset, mouseX, mouseY)) {
            return ingredient;
         }
      }

      return null;
   }

   public void draw(Minecraft minecraft, int xOffset, int yOffset, Color highlightColor, int mouseX, int mouseY) {
      for(GuiIngredient<T> ingredient : this.guiIngredients.values()) {
         ingredient.draw(minecraft, xOffset, yOffset);
         if (ingredient.isMouseOver(xOffset, yOffset, mouseX, mouseY)) {
            ingredient.setTooltipCallback(this.tooltipCallback);
            ingredient.drawHighlight(minecraft, highlightColor, xOffset, yOffset);
         }
      }

   }

   public void setOverrideDisplayFocus(@Nullable IFocus<T> focus) {
      if (focus == null) {
         this.focus = null;
      } else {
         this.focus = Focus.<T>check(focus);
      }

   }
}
