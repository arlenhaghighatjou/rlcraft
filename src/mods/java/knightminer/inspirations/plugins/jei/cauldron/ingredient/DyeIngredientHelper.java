package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import com.google.common.collect.ImmutableList;
import com.arlenh7.eaglercraft.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.InspirationsRecipes;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public enum DyeIngredientHelper implements IIngredientHelper<DyeIngredient> {
   INSTANCE;

   public static final List<DyeIngredient> ALL_DYES = Config.enableCauldronDyeing ? (List)Arrays.stream(EnumDyeColor.values()).map(DyeIngredient::new).collect(Collectors.toList()) : Collections.emptyList();

   private DyeIngredientHelper() {
   }

   public List<DyeIngredient> expandSubtypes(List<DyeIngredient> ingredients) {
      return ingredients;
   }

   public DyeIngredient getMatch(Iterable<DyeIngredient> ingredients, DyeIngredient match) {
      for(DyeIngredient dye : ingredients) {
         if (dye.getDye() == match.getDye()) {
            return dye;
         }
      }

      return null;
   }

   public String getDisplayName(DyeIngredient ingredient) {
      return Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", ingredient.getDye().getUnlocalizedName()));
   }

   public String getUniqueId(DyeIngredient ingredient) {
      return this.getResourceId(ingredient);
   }

   public String getWildcardId(DyeIngredient ingredient) {
      return this.getUniqueId(ingredient);
   }

   public String getModId(DyeIngredient ingredient) {
      return "inspirations";
   }

   public Iterable<Color> getColors(DyeIngredient ingredient) {
      return ImmutableList.of(new Color(ingredient.getDye().colorValue));
   }

   public String getResourceId(DyeIngredient ingredient) {
      return ingredient.getDye().getName();
   }

   public DyeIngredient copyIngredient(DyeIngredient ingredient) {
      return ingredient;
   }

   public String getErrorInfo(DyeIngredient ingredient) {
      return ingredient != null && ingredient.getDye() != null ? this.getResourceId(ingredient) : null;
   }

   public ItemStack getCheatItemStack(DyeIngredient ingredient) {
      return new ItemStack(InspirationsRecipes.dyedWaterBottle, 1, ingredient.getDye().getDyeDamage());
   }
}
