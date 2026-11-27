package mezz.jei.gui.ingredients;

import net.lax1dude.eaglercraft.v1_8.HString;
import com.arlenh7.eaglercraft.awt.Color;
import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.startup.ForgeModIdHelper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class GuiIngredient<T> extends Gui implements IGuiIngredient<T> {
   private static final String oreDictionaryIngredient = Translator.translateToLocal("jei.tooltip.recipe.ore.dict");
   private final int slotIndex;
   private final boolean input;
   private final Rectangle rect;
   private final int xPadding;
   private final int yPadding;
   private final CycleTimer cycleTimer;
   private final List<T> displayIngredients = new ArrayList();
   private final List<T> allIngredients = new ArrayList();
   private final IIngredientRenderer<T> ingredientRenderer;
   private final IIngredientHelper<T> ingredientHelper;
   @Nullable
   private ITooltipCallback<T> tooltipCallback;
   @Nullable
   private IDrawable background;
   private boolean enabled;

   public GuiIngredient(int slotIndex, boolean input, IIngredientRenderer<T> ingredientRenderer, IIngredientHelper<T> ingredientHelper, Rectangle rect, int xPadding, int yPadding, int cycleOffset) {
      this.ingredientRenderer = ingredientRenderer;
      this.ingredientHelper = ingredientHelper;
      this.slotIndex = slotIndex;
      this.input = input;
      this.rect = rect;
      this.xPadding = xPadding;
      this.yPadding = yPadding;
      this.cycleTimer = new CycleTimer(cycleOffset);
   }

   public Rectangle getRect() {
      return this.rect;
   }

   public boolean isMouseOver(int xOffset, int yOffset, int mouseX, int mouseY) {
      return this.enabled && mouseX >= xOffset + this.rect.x && mouseY >= yOffset + this.rect.y && mouseX < xOffset + this.rect.x + this.rect.width && mouseY < yOffset + this.rect.y + this.rect.height;
   }

   @Nullable
   public T getDisplayedIngredient() {
      return (T)this.cycleTimer.getCycledItem(this.displayIngredients);
   }

   public List<T> getAllIngredients() {
      return this.allIngredients;
   }

   public void set(@Nullable List<T> ingredients, @Nullable IFocus<T> focus) {
      this.displayIngredients.clear();
      this.allIngredients.clear();
      List<T> displayIngredients;
      if (ingredients == null) {
         displayIngredients = Collections.emptyList();
      } else {
         displayIngredients = this.ingredientHelper.expandSubtypes(ingredients);
      }

      T match = (T)this.getMatch(displayIngredients, focus);
      if (match != null) {
         this.displayIngredients.add(match);
      } else {
         displayIngredients = this.filterOutHidden(displayIngredients);
         this.displayIngredients.addAll(displayIngredients);
      }

      if (ingredients != null) {
         this.allIngredients.addAll(ingredients);
      }

      this.enabled = !this.displayIngredients.isEmpty();
   }

   private List<T> filterOutHidden(List<T> ingredients) {
      if (ingredients.isEmpty()) {
         return ingredients;
      } else {
         IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
         IngredientFilter ingredientFilter = Internal.getIngredientFilter();
         List<T> visible = new ArrayList();

         for(T ingredient : ingredients) {
            if (ingredient == null || ingredientRegistry.isIngredientVisible(ingredient, ingredientFilter)) {
               visible.add(ingredient);
            }

            if (visible.size() > 100) {
               return visible;
            }
         }

         if (visible.size() > 0) {
            return visible;
         } else {
            return ingredients;
         }
      }
   }

   public void setBackground(IDrawable background) {
      this.background = background;
   }

   @Nullable
   private T getMatch(Collection<T> ingredients, @Nullable IFocus<T> focus) {
      if (focus != null && this.isMode(focus.getMode())) {
         T focusValue = focus.getValue();
         return this.ingredientHelper.getMatch(ingredients, focusValue);
      } else {
         return (T)null;
      }
   }

   public void setTooltipCallback(@Nullable ITooltipCallback<T> tooltipCallback) {
      this.tooltipCallback = tooltipCallback;
   }

   public void draw(Minecraft minecraft, int xOffset, int yOffset) {
      this.cycleTimer.onDraw();
      if (this.background != null) {
         this.background.draw(minecraft, xOffset + this.rect.x, yOffset + this.rect.y);
      }

      T value = (T)this.getDisplayedIngredient();

      try {
         this.ingredientRenderer.render(minecraft, xOffset + this.rect.x + this.xPadding, yOffset + this.rect.y + this.yPadding, value);
      } catch (LinkageError | RuntimeException var6) {
         if (value != null) {
            throw ErrorUtil.createRenderIngredientException(var6, value);
         } else {
            throw var6;
         }
      }
   }

   public void drawHighlight(Minecraft minecraft, Color color, int xOffset, int yOffset) {
      int x = this.rect.x + xOffset + this.xPadding;
      int y = this.rect.y + yOffset + this.yPadding;
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      drawRect(x, y, x + this.rect.width - this.xPadding * 2, y + this.rect.height - this.yPadding * 2, color.getRGB());
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void drawOverlays(Minecraft minecraft, int xOffset, int yOffset, int mouseX, int mouseY) {
      T value = (T)this.getDisplayedIngredient();
      if (value != null) {
         this.drawTooltip(minecraft, xOffset, yOffset, mouseX, mouseY, value);
      }

   }

   private void drawTooltip(Minecraft minecraft, int xOffset, int yOffset, int mouseX, int mouseY, T value) {
      try {
         GlStateManager.disableDepth();
         RenderHelper.disableStandardItemLighting();
         drawRect(xOffset + this.rect.x + this.xPadding, yOffset + this.rect.y + this.yPadding, xOffset + this.rect.x + this.rect.width - this.xPadding, yOffset + this.rect.y + this.rect.height - this.yPadding, Integer.MAX_VALUE);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         TooltipFlags tooltipFlag = minecraft.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL;
         List<String> tooltip = this.ingredientRenderer.getTooltip(minecraft, value, tooltipFlag);
         tooltip = ForgeModIdHelper.getInstance().addModNameToIngredientTooltip(tooltip, value, this.ingredientHelper);
         if (this.tooltipCallback != null) {
            this.tooltipCallback.onTooltip(this.slotIndex, this.input, value, tooltip);
         }

         FontRenderer fontRenderer = this.ingredientRenderer.getFontRenderer(minecraft, value);
         if (value instanceof ItemStack) {
            Collection<ItemStack> itemStacks = (Collection<ItemStack>)this.allIngredients;
            String oreDictEquivalent = Internal.getStackHelper().getOreDictEquivalent(itemStacks);
            if (oreDictEquivalent != null) {
               String acceptsAny = HString.format(oreDictionaryIngredient, oreDictEquivalent);
               tooltip.add(TextFormatting.GRAY + acceptsAny);
            }

            TooltipRenderer.drawHoveringText((ItemStack)value, minecraft, tooltip, xOffset + mouseX, yOffset + mouseY, fontRenderer);
         } else {
            TooltipRenderer.drawHoveringText(minecraft, tooltip, xOffset + mouseX, yOffset + mouseY, fontRenderer);
         }

         GlStateManager.enableDepth();
      } catch (RuntimeException var13) {
         Log.get().error("Exception when rendering tooltip on {}.", value, var13);
      }

   }

   public boolean isInput() {
      return this.input;
   }

   public boolean isMode(IFocus.Mode mode) {
      return this.input && mode == IFocus.Mode.INPUT || !this.input && mode == IFocus.Mode.OUTPUT;
   }
}
