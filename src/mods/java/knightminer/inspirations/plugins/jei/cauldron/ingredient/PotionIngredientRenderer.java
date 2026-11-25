package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import java.util.ArrayList;
import java.util.List;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.plugins.jei.cauldron.CauldronRecipeCategory;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

public enum PotionIngredientRenderer implements IIngredientRenderer<PotionIngredient> {
   INVENTORY,
   LEVEL_1,
   LEVEL_2,
   LEVEL_3,
   LEVEL_4,
   INVALID;

   private int level = this.ordinal();
   public static final ResourceLocation POTION_TEXTURE = Util.getResource("blocks/fluid_potion");

   private PotionIngredientRenderer() {
   }

   public static PotionIngredientRenderer forLevel(int level) {
      return level >= 1 && level <= 4 && (level != 4 || Config.enableBiggerCauldron) ? values()[level] : INVALID;
   }

   public void render(Minecraft minecraft, int x, int y, PotionIngredient potion) {
      if (potion != null && this.level != INVALID.level) {
         float[] color = Util.getColorComponents(PotionUtils.getPotionColor(potion.getPotion()));
         ClientUtil.renderJEICauldronFluid(minecraft, x, y, POTION_TEXTURE, color, this.level);
      }
   }

   public List<String> getTooltip(Minecraft minecraft, PotionIngredient ingredient, ITooltipFlag tooltipFlag) {
      List<String> tooltip = new ArrayList();
      PotionType potion = ingredient.getPotion();
      tooltip.add(Util.translate(potion.getNamePrefixed("potion.effect.")));
      Util.addPotionTooltip(potion, tooltip);
      CauldronRecipeCategory.addLevelTooltip(this.level, tooltip);
      return tooltip;
   }
}
