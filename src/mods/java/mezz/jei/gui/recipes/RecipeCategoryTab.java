package mezz.jei.gui.recipes;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.Internal;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.startup.ForgeModIdHelper;
import mezz.jei.util.LegacyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;

public class RecipeCategoryTab extends RecipeGuiTab {
   private final IRecipeGuiLogic logic;
   private final IRecipeCategory category;

   public RecipeCategoryTab(IRecipeGuiLogic logic, IRecipeCategory category, int x, int y) {
      super(x, y);
      this.logic = logic;
      this.category = category;
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
      this.logic.setRecipeCategory(this.category);
      SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
      soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      return true;
   }

   public void draw(Minecraft minecraft, boolean selected, int mouseX, int mouseY) {
      super.draw(minecraft, selected, mouseX, mouseY);
      int iconX = this.x + 4;
      int iconY = this.y + 4;
      IDrawable icon = this.category.getIcon();
      if (icon != null) {
         iconX = iconX + (16 - icon.getWidth()) / 2;
         iconY = iconY + (16 - icon.getHeight()) / 2;
         icon.draw(minecraft, iconX, iconY);
      } else {
         List<Object> recipeCatalysts = this.logic.getRecipeCatalysts(this.category);
         if (!recipeCatalysts.isEmpty()) {
            Object ingredient = recipeCatalysts.get(0);
            renderIngredient(minecraft, iconX, iconY, ingredient);
         } else {
            String text = this.category.getTitle().substring(0, 2);
            FontRenderer fontRenderer = minecraft.fontRenderer;
            float textCenterX = (float)this.x + 12.0F;
            float textCenterY = (float)this.y + 12.0F - 3.0F;
            int color = this.isMouseOver(mouseX, mouseY) ? 16777120 : 14737632;
            fontRenderer.drawStringWithShadow(text, textCenterX - (float)fontRenderer.getStringWidth(text) / 2.0F, textCenterY, color);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

   }

   private static <T> void renderIngredient(Minecraft minecraft, int iconX, int iconY, T ingredient) {
      IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientRenderer<T> ingredientRenderer = ingredientRegistry.getIngredientRenderer(ingredient);
      GlStateManager.enableDepth();
      ingredientRenderer.render(minecraft, iconX, iconY, ingredient);
      GlStateManager.enableAlpha();
      GlStateManager.disableDepth();
   }

   public boolean isSelected(IRecipeCategory selectedCategory) {
      return this.category.getUid().equals(selectedCategory.getUid());
   }

   public List<String> getTooltip() {
      List<String> tooltip = new ArrayList();
      String title = this.category.getTitle();
      if (title != null) {
         tooltip.add(title);
      }

      String modName = LegacyUtil.getModName(this.category);
      if (modName != null) {
         modName = ForgeModIdHelper.getInstance().getFormattedModNameForModId(modName);
         if (modName != null) {
            tooltip.add(modName);
         }
      }

      return tooltip;
   }
}
