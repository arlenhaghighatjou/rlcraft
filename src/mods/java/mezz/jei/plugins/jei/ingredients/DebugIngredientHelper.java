package mezz.jei.plugins.jei.ingredients;

import java.awt.Color;
import java.util.Collections;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.util.CommandUtilServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class DebugIngredientHelper implements IIngredientHelper<DebugIngredient> {
   public DebugIngredientHelper() {
   }

   @Nullable
   public DebugIngredient getMatch(Iterable<DebugIngredient> ingredients, DebugIngredient ingredientToMatch) {
      for(DebugIngredient debugIngredient : ingredients) {
         if (debugIngredient.getNumber() == ingredientToMatch.getNumber()) {
            return debugIngredient;
         }
      }

      return null;
   }

   public String getDisplayName(DebugIngredient ingredient) {
      return "JEI Debug Item #" + ingredient.getNumber();
   }

   public String getUniqueId(DebugIngredient ingredient) {
      return "JEI_debug_" + ingredient.getNumber();
   }

   public String getWildcardId(DebugIngredient ingredient) {
      return this.getUniqueId(ingredient);
   }

   public String getModId(DebugIngredient ingredient) {
      return "jei";
   }

   public Iterable<Color> getColors(DebugIngredient ingredient) {
      return Collections.emptyList();
   }

   public String getResourceId(DebugIngredient ingredient) {
      return "debug_" + ingredient.getNumber();
   }

   public ItemStack getCheatItemStack(DebugIngredient ingredient) {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      if (player != null) {
         CommandUtilServer.writeChatMessage(player, "Debug ingredients cannot be cheated", TextFormatting.RED);
      }

      return ItemStack.EMPTY;
   }

   public DebugIngredient copyIngredient(DebugIngredient ingredient) {
      return ingredient.copy();
   }

   public String getErrorInfo(@Nullable DebugIngredient ingredient) {
      return ingredient == null ? "debug ingredient: null" : this.getDisplayName(ingredient);
   }
}
